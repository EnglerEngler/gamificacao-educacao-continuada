package br.edu.unifacens.gamificacao.aluno.domain;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ConquistasTest {
    @Test void premioPremiumAconteceUmaUnicaVez() {
        var a=new Aluno(5);
        assertFalse(a.isPremium()); assertFalse(a.possuiBadge()); assertEquals(0,a.getPontos());
        for (int i=0;i<12;i++) a.concluirCurso(new BigDecimal("8"),true);
        assertTrue(a.isPremium()); assertTrue(a.possuiBadge());
        assertEquals(3,a.getMoedas()); assertEquals(1203,a.getPontos()); assertEquals(41,a.getCursosDisponiveis());
        a.concluirCurso(new BigDecimal("8"),true);
        assertEquals(3,a.getMoedas()); assertEquals(13,a.getCursosConcluidos());
    }
    @Test void rejeitaEstadoInvalidoEMediasForaDoIntervalo() {
        assertThrows(IllegalArgumentException.class,()->new Aluno(-1,0,0));
        assertThrows(IllegalArgumentException.class,()->new Aluno(0,-1,0));
        assertThrows(IllegalArgumentException.class,()->new Aluno(0,0,-1));
        var a=new Aluno(0,0,0);
        assertThrows(IllegalArgumentException.class,()->a.concluirCurso(new BigDecimal("-1"),true));
        assertThrows(IllegalArgumentException.class,()->a.concluirCurso(new BigDecimal("10.1"),true));
        a.concluirCurso(BigDecimal.ZERO,true);
        a.concluirCurso(BigDecimal.TEN,false);
        assertEquals(0,a.getCursosConcluidos());
    }
}

