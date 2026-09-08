package br.edu.fatec.gamificacao.domain;

import java.math.BigDecimal;
import java.util.Objects;

/** Regras de gamificação independentes de banco, API ou interface. */
public class Aluno {
    private int cursosDisponiveis;
    private int cursosConcluidos;
    private Plano plano;
    private int moedas;

    public Aluno(int cursosDisponiveis) { this(cursosDisponiveis, 0, Plano.BASICO, 0); }

    public Aluno(int cursosDisponiveis, int cursosConcluidos, Plano plano, int moedas) {
        if (cursosDisponiveis < 0 || cursosConcluidos < 0 || moedas < 0) throw new IllegalArgumentException("Valores não podem ser negativos");
        this.cursosDisponiveis = cursosDisponiveis;
        this.cursosConcluidos = cursosConcluidos;
        this.plano = Objects.requireNonNull(plano);
        this.moedas = moedas;
    }

    public void concluirCurso(BigDecimal media, boolean cursoConcluido) {
        Objects.requireNonNull(media, "A média é obrigatória");
        if (!cursoConcluido) return;
        cursosConcluidos++;
        if (media.compareTo(new BigDecimal("7.0")) > 0) cursosDisponiveis += 3;
        atualizarPremium();
    }

    public void premiarParticipacaoForum() { cursosDisponiveis++; }

    private void atualizarPremium() {
        if (cursosConcluidos >= 12 && plano == Plano.BASICO) { plano = Plano.PREMIUM; moedas += 3; }
    }
    public int getCursosDisponiveis() { return cursosDisponiveis; }
    public int getCursosConcluidos() { return cursosConcluidos; }
    public Plano getPlano() { return plano; }
    public int getMoedas() { return moedas; }
}
