package br.edu.unifacens.gamificacao.domain;

import java.math.BigDecimal;
import java.util.Objects;

/** Regras de gamificação das US01 e US03, independentes de banco, API ou interface. */
public class Aluno {
    private static final BigDecimal MEDIA_MINIMA_DESBLOQUEIO = new BigDecimal("7.0");
    private static final int CURSOS_LIBERADOS_POR_BOA_MEDIA = 3;
    private static final int CURSOS_PARA_PREMIUM = 12;
    private static final int MOEDAS_PREMIUM = 3;
    private static final String PLANO_BASICO = "BASICO";
    private static final String PLANO_PREMIUM = "PREMIUM";

    private int cursosDisponiveis;
    private int cursosConcluidos;
    private String plano;
    private int moedas;

    public Aluno(int cursosDisponiveis) {
        this(cursosDisponiveis, 0, PLANO_BASICO, 0);
    }

    public Aluno(int cursosDisponiveis, int cursosConcluidos, String plano, int moedas) {
        if (cursosDisponiveis < 0) throw new IllegalArgumentException("Cursos disponíveis não podem ser negativos");
        if (cursosConcluidos < 0) throw new IllegalArgumentException("Cursos concluídos não podem ser negativos");
        if (moedas < 0) throw new IllegalArgumentException("Moedas não podem ser negativas");

        this.cursosDisponiveis = cursosDisponiveis;
        this.cursosConcluidos = cursosConcluidos;
        this.plano = Objects.requireNonNull(plano, "O plano é obrigatório");
        this.moedas = moedas;
    }

    public void concluirCurso(BigDecimal media, boolean cursoConcluido) {
        Objects.requireNonNull(media, "A média é obrigatória");
        if (!cursoConcluido) {
            return;
        }

        this.cursosConcluidos++;
        liberarCursosPorBoaMedia(media);
        atualizarPlanoPremium();
    }

    private void liberarCursosPorBoaMedia(BigDecimal media) {
        if (media.compareTo(MEDIA_MINIMA_DESBLOQUEIO) > 0) {
            this.cursosDisponiveis += CURSOS_LIBERADOS_POR_BOA_MEDIA;
        }
    }

    private void atualizarPlanoPremium() {
        if (this.cursosConcluidos >= CURSOS_PARA_PREMIUM && !PLANO_PREMIUM.equals(this.plano)) {
            this.plano = PLANO_PREMIUM;
            this.moedas += MOEDAS_PREMIUM;
        }
    }

    public int getCursosDisponiveis() { return cursosDisponiveis; }
    public int getCursosConcluidos() { return cursosConcluidos; }
    public String getPlano() { return plano; }
    public int getMoedas() { return moedas; }
}
