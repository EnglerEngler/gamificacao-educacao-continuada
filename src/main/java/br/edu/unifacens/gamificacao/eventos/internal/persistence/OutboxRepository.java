package br.edu.unifacens.gamificacao.eventos.internal.persistence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from OutboxEntity e where e.entregue=false and e.proximaTentativa<=:agora order by e.id")
    List<OutboxEntity> pendentes(@Param("agora") Instant agora, Pageable page);
    long countByEntregueFalse();
}

