package br.edu.unifacens.gamificacao.integration;
import br.edu.unifacens.gamificacao.eventos.application.NotificacaoPort;
import br.edu.unifacens.gamificacao.ia.application.IaPort;
import br.edu.unifacens.gamificacao.eventos.internal.persistence.OutboxRepository;
import br.edu.unifacens.gamificacao.eventos.application.OutboxDispatcher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties={"spring.profiles.active=h2","b1.lab.enabled=true","b1.outbox.interval-ms=3600000"})
@AutoConfigureMockMvc
class CoreIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired OutboxRepository outbox;
    @Autowired OutboxDispatcher dispatcher;
    @MockBean NotificacaoPort notificacao;
    @MockBean IaPort ia;

    long criar(String tenant) throws Exception {
        String body=mvc.perform(post("/api/alunos").header("X-Instituicao",tenant)
            .contentType("application/json").content("{\"nome\":\"Teste\",\"cursosDisponiveis\":5}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("id").asLong();
    }

    @Test void falhaDoConsumidorNaoBloqueiaCoreEEventoPermaneceRecuperavel() throws Exception {
        doThrow(new RuntimeException("offline")).when(notificacao).enviar(anyString());
        long id=criar("outbox");
        mvc.perform(post("/api/alunos/"+id+"/cursos/conclusao").header("X-Instituicao","outbox")
            .contentType("application/json").content("{\"media\":8,\"concluido\":true,\"cursoId\":\"c1\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.cursosDisponiveis").value(8));
        dispatcher.despachar();
        assertTrue(outbox.countByEntregueFalse()>0);
        mvc.perform(post("/api/lab/alunos/"+id+"/conclusao-sincrona").header("X-Instituicao","outbox")
            .contentType("application/json").content("{\"media\":8,\"concluido\":true,\"cursoId\":\"c2\"}"))
            .andExpect(status().isServiceUnavailable());
        mvc.perform(get("/api/alunos").header("X-Instituicao","outbox"))
            .andExpect(jsonPath("$[0].cursosDisponiveis").value(8));
    }

    @Test void iaDegradaSemBloquearRanking() throws Exception {
        when(ia.recomendar(anyString())).thenThrow(new RuntimeException("offline"));
        when(ia.perguntar(anyString())).thenThrow(new RuntimeException("offline"));
        mvc.perform(get("/api/ia/recomendacoes")).andExpect(status().isOk()).andExpect(jsonPath("$.degradado").value(true));
        mvc.perform(post("/api/ia/assistente").contentType("application/json").content("{\"pergunta\":\"Como estudar?\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.degradado").value(true));
        mvc.perform(get("/api/ranking")).andExpect(status().isOk());
    }

    @Test void mqttContratoProtegeTokenTenantEDuplicidade() throws Exception {
        long id=criar("ac1");
        String payload="{\"eventoId\":\""+UUID.randomUUID()+"\",\"instituicao\":\"ac1\",\"dispositivoId\":\"lab01\",\"tipo\":\"CONCLUSAO\",\"alunoId\":"+id+",\"cursoId\":\"iot-1\",\"media\":8}";
        mvc.perform(post("/api/iot/eventos").header("X-Instituicao","ac1").contentType("application/json").content(payload))
            .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/iot/eventos").header("X-Instituicao","outra").header("X-Integration-Token","lab-b1-local")
            .contentType("application/json").content(payload)).andExpect(status().isForbidden());
        for (int i=0;i<2;i++) mvc.perform(post("/api/iot/eventos").header("X-Instituicao","ac1")
            .header("X-Integration-Token","lab-b1-local").contentType("application/json").content(payload))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value(i==0?"aceito":"duplicado"));
        mvc.perform(get("/api/alunos")).andExpect(jsonPath("$[?(@.id == "+id+")].cursosDisponiveis").value(org.hamcrest.Matchers.contains(8)));
    }

    @Test void rankingLimitaResultadoOrdenaEIsolaTenant() throws Exception {
        long id=criar("rank");
        criar("rank"); criar("rank-other");
        mvc.perform(post("/api/alunos/"+id+"/cursos/conclusao").header("X-Instituicao","rank")
            .contentType("application/json").content("{\"media\":8,\"concluido\":true,\"cursoId\":\"ranking-c1\"}"))
            .andExpect(status().isOk());
        mvc.perform(get("/api/ranking").header("X-Instituicao","rank").param("limite","1"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].alunoId").value(id));
        mvc.perform(get("/api/ranking").param("limite","101")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/ranking").param("pagina","-1")).andExpect(status().isBadRequest());
    }

    @Test void repeticaoConcorrenteGeraUmaRecompensa() throws Exception {
        long id=criar("concorrencia");
        var pool=java.util.concurrent.Executors.newFixedThreadPool(4);
        try {
            var tasks=new java.util.ArrayList<java.util.concurrent.Callable<Integer>>();
            for(int i=0;i<4;i++) tasks.add(()->mvc.perform(post("/api/alunos/"+id+"/cursos/conclusao")
                .header("X-Instituicao","concorrencia").contentType("application/json")
                .content("{\"media\":8,\"concluido\":true,\"cursoId\":\"mesmo-curso\"}"))
                .andReturn().getResponse().getStatus());
            for(var result:pool.invokeAll(tasks)) assertEquals(200,result.get());
        } finally { pool.shutdown(); }
        mvc.perform(get("/api/alunos").header("X-Instituicao","concorrencia"))
            .andExpect(jsonPath("$[0].cursosDisponiveis").value(8))
            .andExpect(jsonPath("$[0].cursosConcluidos").value(1));
    }
}

