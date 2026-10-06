package br.edu.unifacens.gamificacao.aluno.application;

import br.edu.unifacens.gamificacao.aluno.domain.Aluno;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Porta de saída. Nenhum tipo JPA faz parte do contrato. */
public interface AlunoStore {
    record Registro(Long id, String instituicao, String nome, Aluno aluno) {}
    Registro criar(String instituicao, String nome, int cursos);
    List<Registro> listar(String instituicao);
    Optional<Registro> buscarComBloqueio(String instituicao, Long id);
    boolean jaConcluido(String instituicao, Long alunoId, String cursoId);
    void salvarConclusao(Registro registro, String cursoId, UUID eventoId);
}

