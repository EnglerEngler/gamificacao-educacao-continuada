package br.edu.unifacens.gamificacao.domain;

import java.math.BigDecimal;
import java.util.Objects;

/** Regras da US01, independentes de banco, API ou interface. */
public class Aluno {
    private int cursosDisponiveis;

    public Aluno(int cursosDisponiveis) {
        if (cursosDisponiveis < 0) throw new IllegalArgumentException("Cursos disponíveis não podem ser negativos");
        this.cursosDisponiveis = cursosDisponiveis;
    }

    public void concluirCurso(BigDecimal media, boolean cursoConcluido) {
        Objects.requireNonNull(media, "A média é obrigatória");
        if (!cursoConcluido) return;
        if (media.compareTo(new BigDecimal("7.0")) > 0) cursosDisponiveis += 3;
    }

    public int getCursosDisponiveis() { return cursosDisponiveis; }
}
