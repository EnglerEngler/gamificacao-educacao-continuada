package br.edu.unifacens.gamificacao.aluno.api;
public interface ConcluirCurso {
    AlunoResponse concluirCurso(String instituicao, Long id, ConcluirCursoRequest request);
}

