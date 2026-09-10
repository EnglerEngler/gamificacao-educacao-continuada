package br.edu.unifacens.gamificacao.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class AlunoTest {
    @Test void desbloqueiaTresCursosQuandoConcluiComMediaMaiorQueSete() {
        Aluno aluno = new Aluno(5);
        aluno.concluirCurso(new BigDecimal("8.0"), true);
        assertEquals(8, aluno.getCursosDisponiveis());
    }
    @Test void naoDesbloqueiaCursosQuandoMediaForIgualASete() {
        Aluno aluno = new Aluno(5); aluno.concluirCurso(new BigDecimal("7.0"), true);
        assertEquals(5, aluno.getCursosDisponiveis());
    }
    @Test void naoDesbloqueiaCursosQuandoMediaForMenorQueSete() {
        Aluno aluno = new Aluno(5); aluno.concluirCurso(new BigDecimal("6.9"), true);
        assertEquals(5, aluno.getCursosDisponiveis());
    }
    @Test void naoProcessaCursoQueAindaEstaEmAndamento() {
        Aluno aluno = new Aluno(5); aluno.concluirCurso(new BigDecimal("9.0"), false);
        assertEquals(5, aluno.getCursosDisponiveis());
    }
    @Test void rejeitaValoresNegativosEMediaNula() {
        assertThrows(IllegalArgumentException.class, () -> new Aluno(-1));
        assertThrows(NullPointerException.class, () -> new Aluno(1).concluirCurso(null, true));
    }
}
