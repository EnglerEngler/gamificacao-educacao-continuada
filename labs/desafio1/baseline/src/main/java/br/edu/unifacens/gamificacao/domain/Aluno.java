package br.edu.unifacens.gamificacao.domain;

import java.math.BigDecimal;
import java.util.Objects;

/** Regras da US01, independentes de banco, API ou interface. */
public class Aluno {

    // Fase BLUE: Refatoração para extrair "Magic Numbers" para constantes
    private static final BigDecimal MEDIA_MINIMA_DESBLOQUEIO = new BigDecimal("7.0");
    private static final int CURSOS_GANHOS_POR_CONCLUSAO = 3;

    private int cursosDisponiveis;

    public Aluno(int cursosDisponiveis) {
        if (cursosDisponiveis < 0) {
            throw new IllegalArgumentException("Cursos disponíveis não podem ser negativos");
        }
        this.cursosDisponiveis = cursosDisponiveis;
    }

    public void concluirCurso(BigDecimal media, boolean cursoConcluido) {
        Objects.requireNonNull(media, "A média é obrigatória");

        if (!cursoConcluido) {
            return;
        }

        // Fase BLUE: Uso das constantes em vez de números soltos, melhorando a legibilidade
        if (media.compareTo(MEDIA_MINIMA_DESBLOQUEIO) > 0) {
            this.cursosDisponiveis += CURSOS_GANHOS_POR_CONCLUSAO;
        }
    }

    public int getCursosDisponiveis() {
        return cursosDisponiveis;
    }
}