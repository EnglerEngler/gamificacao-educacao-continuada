package br.edu.unifacens.gamificacao.integration;
import br.edu.unifacens.gamificacao.aluno.application.AlunoService;
import br.edu.unifacens.gamificacao.aluno.api.*;
import br.edu.unifacens.gamificacao.eventos.api.RegistrarEvento;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties={"spring.profiles.active=h2","b1.outbox.interval-ms=3600000"})
class TransactionRollbackTest {
    @Autowired AlunoService alunos;
    @MockBean RegistrarEvento eventos;
    @Test void falhaAoGravarIntencaoDesfazRecompensaEConclusao() {
        var aluno=alunos.criar("rollback",new CriarAlunoRequest("Rollback",5));
        var request=new ConcluirCursoRequest(new BigDecimal("8"),true,"curso-rollback",UUID.randomUUID());
        doThrow(new IllegalStateException("Falha ao persistir intenção")).when(eventos).registrar(any());
        assertThrows(IllegalStateException.class,()->alunos.concluirCurso("rollback",aluno.id(),request));
        assertEquals(5,alunos.listar("rollback").getFirst().cursosDisponiveis());
        doNothing().when(eventos).registrar(any());
        assertEquals(8,alunos.concluirCurso("rollback",aluno.id(),request).cursosDisponiveis());
    }
}

