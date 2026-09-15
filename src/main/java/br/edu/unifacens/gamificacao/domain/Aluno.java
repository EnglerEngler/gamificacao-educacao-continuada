package br.edu.unifacens.gamificacao.domain;

import java.math.BigDecimal;
import java.util.Objects;

/** Regras das US01 e US03, independentes de banco, API ou interface. */
public class Aluno {
    private int cursosDisponiveis;
    private int cursosConcluidos;
    private String plano;
    private int moedas;

    public Aluno(int cursosDisponiveis) {
        this(cursosDisponiveis, 0, "BASICO", 0);
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

        if (media.compareTo(new BigDecimal("7.0")) > 0) {
            this.cursosDisponiveis += 3;
        }

        if (this.cursosConcluidos >= 12 && !"PREMIUM".equals(this.plano)) {
            this.plano = "PREMIUM";
            this.moedas += 3;
        }
    }

    public int getCursosDisponiveis() { return cursosDisponiveis; }
    public int getCursosConcluidos() { return cursosConcluidos; }
    public String getPlano() { return plano; }
    public int getMoedas() { return moedas; }
}
