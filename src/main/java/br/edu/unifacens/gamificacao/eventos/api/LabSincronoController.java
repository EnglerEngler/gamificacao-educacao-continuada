package br.edu.unifacens.gamificacao.eventos.api;
import br.edu.unifacens.gamificacao.aluno.api.*;
import br.edu.unifacens.gamificacao.eventos.application.NotificacaoPort;
import br.edu.unifacens.gamificacao.shared.web.Instituicao;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

/** Controle experimental. Nunca habilitado no profile de operação normal. */
@RestController @RequestMapping("/api/lab")
@ConditionalOnProperty(name="b1.lab.enabled",havingValue="true")
public class LabSincronoController {
    private final ConcluirCurso alunos;
    private final NotificacaoPort notificacao;
    private final ObjectMapper mapper;
    public LabSincronoController(ConcluirCurso alunos, NotificacaoPort notificacao, ObjectMapper mapper) {
        this.alunos=alunos; this.notificacao=notificacao; this.mapper=mapper;
    }
    @PostMapping("/alunos/{id}/conclusao-sincrona")
    public AlunoResponse concluir(@PathVariable Long id,
        @RequestHeader(value="X-Instituicao",defaultValue="ac1") String tenant,
        @Valid @RequestBody ConcluirCursoRequest request) {
        Instituicao.validar(tenant);
        UUID event=request.eventoId()==null ? UUID.randomUUID() : request.eventoId();
        String curso=request.cursoId()==null ? event.toString() : request.cursoId();
        try {
            notificacao.enviar(mapper.writeValueAsString(new CursoConcluido(event,tenant,id,curso,request.media())));
        } catch (Exception e) { throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Consumidor indisponível"); }
        return alunos.concluirCurso(tenant,id,new ConcluirCursoRequest(request.media(),request.concluido(),curso,event));
    }
}

