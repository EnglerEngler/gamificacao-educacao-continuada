package br.edu.fatec.gamificacao.dto;

import br.edu.fatec.gamificacao.domain.Plano;
import br.edu.fatec.gamificacao.entity.AlunoEntity;

public record AlunoResponse(Long id, String nome, int cursosDisponiveis, int cursosConcluidos, Plano plano, int moedas) {
    public static AlunoResponse of(AlunoEntity aluno) {
        return new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getCursosDisponiveis(), aluno.getCursosConcluidos(), aluno.getPlano(), aluno.getMoedas());
    }
}
