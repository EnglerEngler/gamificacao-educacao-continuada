package br.edu.unifacens.gamificacao.aluno.domain;

import java.math.BigDecimal;
import java.util.Objects;

/** Reutiliza US01 e US03 da AC1: conclusão e aprovação têm contadores distintos. */
public class Aluno {
    private static final BigDecimal MEDIA_MINIMA = new BigDecimal("7.0");
    private int cursosDisponiveis;
    private int cursosConcluidos;
    private int cursosAprovados;
    private String plano;
    private int moedas;

    public Aluno(int cursosDisponiveis) { this(cursosDisponiveis, 0, "BASICO", 0, 0); }
    public Aluno(int cursosDisponiveis, int cursosConcluidos, String plano, int moedas) {
        this(cursosDisponiveis, cursosConcluidos, plano, moedas, 0);
    }
    public Aluno(int cursosDisponiveis, int cursosConcluidos, String plano, int moedas, int cursosAprovados) {
        if (cursosDisponiveis < 0 || cursosConcluidos < 0 || moedas < 0 || cursosAprovados < 0)
            throw new IllegalArgumentException("Saldos não podem ser negativos");
        this.cursosDisponiveis = cursosDisponiveis;
        this.cursosConcluidos = cursosConcluidos;
        this.plano = Objects.requireNonNull(plano, "O plano é obrigatório");
        this.moedas = moedas;
        this.cursosAprovados = cursosAprovados;
    }

    public void concluirCurso(BigDecimal media, boolean concluido) {
        Objects.requireNonNull(media, "A média é obrigatória");
        if (media.signum() < 0 || media.compareTo(BigDecimal.TEN) > 0)
            throw new IllegalArgumentException("Média deve estar entre 0 e 10");
        if (!concluido) return;
        cursosConcluidos++;
        if (media.compareTo(MEDIA_MINIMA) > 0) {
            cursosDisponiveis += 3;
            cursosAprovados++;
        }
        // US03: todas as conclusões contam para Premium, independentemente da média.
        if (cursosConcluidos >= 12 && !"PREMIUM".equals(plano)) {
            plano = "PREMIUM";
            moedas += 3;
        }
    }

    public int getCursosDisponiveis() { return cursosDisponiveis; }
    public int getCursosConcluidos() { return cursosConcluidos; }
    public int getCursosAprovados() { return cursosAprovados; }
    public String getPlano() { return plano; }
    public int getMoedas() { return moedas; }
    public boolean isPremium() { return "PREMIUM".equals(plano); }
    public boolean possuiBadge() { return cursosAprovados > 0; }
    public int getPontos() { return cursosAprovados * 100 + moedas; }
}
