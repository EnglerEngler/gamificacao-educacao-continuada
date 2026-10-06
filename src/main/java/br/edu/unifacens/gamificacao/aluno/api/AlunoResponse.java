package br.edu.unifacens.gamificacao.aluno.api;

import br.edu.unifacens.gamificacao.aluno.application.AlunoStore;
import java.util.List;

public record AlunoResponse(Long id, String nome, int cursosDisponiveis,
                            int cursosConcluidos, int moedas, String plano,
                            List<String> badges, int pontos) {
    public static AlunoResponse of(AlunoStore.Registro r) {
        var a = r.aluno();
        return new AlunoResponse(r.id(), r.nome(), a.getCursosDisponiveis(),
            a.getCursosConcluidos(), a.getMoedas(), a.isPremium() ? "PREMIUM" : "BASICO",
            a.possuiBadge() ? List.of("primeira-conclusao") : List.of(), a.getPontos());
    }
}

