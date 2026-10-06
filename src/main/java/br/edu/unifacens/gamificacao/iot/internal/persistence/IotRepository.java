package br.edu.unifacens.gamificacao.iot.internal.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface IotRepository extends JpaRepository<IotReceipt, Long> {
    boolean existsByInstituicaoAndEventoId(String instituicao, UUID eventoId);
}

