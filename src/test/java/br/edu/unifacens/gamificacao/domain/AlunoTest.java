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
        Aluno aluno = new Aluno(5);
        aluno.concluirCurso(new BigDecimal("7.0"), true);
        assertEquals(5, aluno.getCursosDisponiveis());
    }

    @Test void naoDesbloqueiaCursosQuandoMediaForMenorQueSete() {
        Aluno aluno = new Aluno(5);
        aluno.concluirCurso(new BigDecimal("6.9"), true);
        assertEquals(5, aluno.getCursosDisponiveis());
    }

    @Test void naoProcessaCursoQueAindaEstaEmAndamento() {
        Aluno aluno = new Aluno(5);
        aluno.concluirCurso(new BigDecimal("9.0"), false);
        assertEquals(5, aluno.getCursosDisponiveis());
        assertEquals(0, aluno.getCursosConcluidos());
    }

    @Test void atualizaParaPremiumAoConcluirDecimoSegundoCurso() {
        Aluno aluno = new Aluno(5, 11, "BASICO", 0);

        aluno.concluirCurso(new BigDecimal("8.0"), true);

        assertEquals(12, aluno.getCursosConcluidos());
        assertEquals("PREMIUM", aluno.getPlano());
        assertEquals(3, aluno.getMoedas());
    }

    @Test void permaneceBasicoAntesDeDozeCursosConcluidos() {
        Aluno aluno = new Aluno(5, 10, "BASICO", 0);

        aluno.concluirCurso(new BigDecimal("7.0"), true);

        assertEquals(11, aluno.getCursosConcluidos());
        assertEquals("BASICO", aluno.getPlano());
        assertEquals(0, aluno.getMoedas());
    }

    @Test void naoDuplicaMoedasQuandoAlunoJaEPremium() {
        Aluno aluno = new Aluno(5, 12, "PREMIUM", 3);

        aluno.concluirCurso(new BigDecimal("8.0"), true);

        assertEquals(13, aluno.getCursosConcluidos());
        assertEquals("PREMIUM", aluno.getPlano());
        assertEquals(3, aluno.getMoedas());
    }

    @Test void rejeitaValoresInvalidosEMediaNula() {
        assertThrows(IllegalArgumentException.class, () -> new Aluno(-1));
        assertThrows(IllegalArgumentException.class, () -> new Aluno(1, -1, "BASICO", 0));
        assertThrows(IllegalArgumentException.class, () -> new Aluno(1, 0, "BASICO", -1));
        assertThrows(NullPointerException.class, () -> new Aluno(1, 0, null, 0));
        assertThrows(NullPointerException.class, () -> new Aluno(1).concluirCurso(null, true));
    }
}
