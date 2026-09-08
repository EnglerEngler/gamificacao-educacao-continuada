package br.edu.fatec.gamificacao.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class AlunoTest {
    @Test void desbloqueiaTresCursosQuandoConcluiComMediaMaiorQueSete() {
        Aluno aluno = new Aluno(5);
        aluno.concluirCurso(new BigDecimal("8.0"), true);
        assertAll(() -> assertEquals(8, aluno.getCursosDisponiveis()), () -> assertEquals(1, aluno.getCursosConcluidos()));
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
        assertAll(() -> assertEquals(5, aluno.getCursosDisponiveis()), () -> assertEquals(0, aluno.getCursosConcluidos()));
    }
    @Test void premiaAlunoMaisAtivoNoForumComUmCurso() {
        Aluno aluno = new Aluno(5); aluno.premiarParticipacaoForum();
        assertEquals(6, aluno.getCursosDisponiveis());
    }
    @Test void tornaPlanoPremiumEAtribuiMoedasAoCompletarDozeCursos() {
        Aluno aluno = new Aluno(5, 11, Plano.BASICO, 0); aluno.concluirCurso(new BigDecimal("7.0"), true);
        assertAll(() -> assertEquals(Plano.PREMIUM, aluno.getPlano()), () -> assertEquals(3, aluno.getMoedas()));
    }
    @Test void naoDuplicaMoedasParaAlunoJaPremium() {
        Aluno aluno = new Aluno(5, 12, Plano.PREMIUM, 3); aluno.concluirCurso(new BigDecimal("8.0"), true);
        assertEquals(3, aluno.getMoedas());
    }
    @Test void rejeitaValoresNegativosEMediaNula() {
        assertThrows(IllegalArgumentException.class, () -> new Aluno(-1));
        assertThrows(NullPointerException.class, () -> new Aluno(1).concluirCurso(null, true));
    }
}
