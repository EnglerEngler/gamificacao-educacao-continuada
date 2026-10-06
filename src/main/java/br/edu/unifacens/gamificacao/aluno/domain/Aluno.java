package br.edu.unifacens.gamificacao.aluno.domain;

import java.math.BigDecimal;
import java.util.Objects;

/** Agregado puro: mantém a regra > 7,0 da AC1 e concentra suas conquistas. */
public class Aluno {
    private static final BigDecimal MEDIA_MINIMA = new BigDecimal("7.0");
    private int cursosDisponiveis;
    private int cursosConcluidos;
    private int moedas;

    public Aluno(int cursosDisponiveis) { this(cursosDisponiveis, 0, 0); }

    public Aluno(int cursosDisponiveis, int cursosConcluidos, int moedas) {
        if (cursosDisponiveis < 0 || cursosConcluidos < 0 || moedas < 0)
            throw new IllegalArgumentException("Saldos não podem ser negativos");
        this.cursosDisponiveis = cursosDisponiveis;
        this.cursosConcluidos = cursosConcluidos;
        this.moedas = moedas;
    }

    public void concluirCurso(BigDecimal media, boolean concluido) {
        Objects.requireNonNull(media, "A média é obrigatória");
        if (media.signum() < 0 || media.compareTo(BigDecimal.TEN) > 0)
            throw new IllegalArgumentException("Média deve estar entre 0 e 10");
        if (!concluido) return;
        if (media.compareTo(MEDIA_MINIMA) > 0) {
            cursosDisponiveis += 3;
            cursosConcluidos++;
            if (cursosConcluidos == 12) moedas += 3;
        }
    }

    public int getCursosDisponiveis() { return cursosDisponiveis; }
    public int getCursosConcluidos() { return cursosConcluidos; }
    public int getMoedas() { return moedas; }
    public boolean isPremium() { return cursosConcluidos >= 12; }
    public boolean possuiBadge() { return cursosConcluidos > 0; }
    public int getPontos() { return cursosConcluidos * 100 + moedas; }
}

