package br.edu.unifacens.gamificacao.aluno.internal.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ConclusaoJpaRepository extends JpaRepository<ConclusaoEntity, Long> {
    boolean existsByInstituicaoAndAlunoIdAndCursoId(String instituicao, Long alunoId, String cursoId);
}

