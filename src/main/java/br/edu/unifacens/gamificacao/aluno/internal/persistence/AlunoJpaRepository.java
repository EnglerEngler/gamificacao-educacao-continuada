package br.edu.unifacens.gamificacao.aluno.internal.persistence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AlunoJpaRepository extends JpaRepository<AlunoEntity, Long> {
    List<AlunoEntity> findByInstituicaoOrderById(String instituicao);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AlunoEntity a where a.instituicao=:instituicao and a.id=:id")
    Optional<AlunoEntity> buscarComBloqueio(@Param("instituicao") String instituicao, @Param("id") Long id);
}

