package br.edu.unifacens.gamificacao.aluno.application;
import br.edu.unifacens.gamificacao.aluno.api.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/** Mesma especificação executada com adapter fake e adapter JPA. */
abstract class AlunoUseCaseContract {
    abstract AlunoService service();
    @Test void preservaRegraAc1EImpedeRecompensaDuplicada() {
        var s=service();
        var aluno=s.criar("ac1",new CriarAlunoRequest("Aluno",5));
        var evento=UUID.randomUUID();
        var aprovado=new ConcluirCursoRequest(new BigDecimal("8"),true,"curso-1",evento);
        assertEquals(8,s.concluirCurso("ac1",aluno.id(),aprovado).cursosDisponiveis());
        assertEquals(8,s.concluirCurso("ac1",aluno.id(),aprovado).cursosDisponiveis());
        assertEquals(8,s.concluirCurso("ac1",aluno.id(),
            new ConcluirCursoRequest(new BigDecimal("8"),true,"curso-1",UUID.randomUUID())).cursosDisponiveis());
        var limite=s.concluirCurso("ac1",aluno.id(),
            new ConcluirCursoRequest(new BigDecimal("7"),true,"curso-2",UUID.randomUUID()));
        assertEquals(8,limite.cursosDisponiveis()); assertEquals(1,limite.cursosConcluidos());
    }
    @Test void isolaInstituicoes() {
        var s=service();
        var aluno=s.criar("instituicao-b",new CriarAlunoRequest("Outra instituição",2));
        assertTrue(s.listar("ac1").stream().noneMatch(a->a.id().equals(aluno.id())));
        assertThrows(AlunoService.AlunoNaoEncontrado.class,()->s.concluirCurso("ac1",aluno.id(),
            new ConcluirCursoRequest(new BigDecimal("8"),true,"curso-1",UUID.randomUUID())));
    }
}

