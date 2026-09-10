package br.edu.unifacens.gamificacao.dto;

import br.edu.unifacens.gamificacao.entity.AlunoEntity;

public record AlunoResponse(Long id, String nome, int cursosDisponiveis) {
    public static AlunoResponse of(AlunoEntity aluno) {
        return new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getCursosDisponiveis());
    }
}
