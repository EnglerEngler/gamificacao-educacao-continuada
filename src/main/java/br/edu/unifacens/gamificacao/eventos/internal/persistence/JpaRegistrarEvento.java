package br.edu.unifacens.gamificacao.eventos.internal.persistence;
import br.edu.unifacens.gamificacao.eventos.api.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JpaRegistrarEvento implements RegistrarEvento {
    private static final Logger LOG=LoggerFactory.getLogger(JpaRegistrarEvento.class);
    private final OutboxRepository repository;
    private final ObjectMapper mapper;
    public JpaRegistrarEvento(OutboxRepository repository, ObjectMapper mapper) {
        this.repository=repository; this.mapper=mapper;
    }
    @Transactional(propagation=org.springframework.transaction.annotation.Propagation.MANDATORY)
    public void registrar(CursoConcluido evento) {
        try {
            repository.save(new OutboxEntity(evento.eventoId(), evento.instituicao(), mapper.writeValueAsString(evento)));
            LOG.info("curso_concluido eventId={} instituicao={} alunoId={}", evento.eventoId(), evento.instituicao(), evento.alunoId());
        } catch (JsonProcessingException e) { throw new IllegalStateException("Não foi possível registrar o evento", e); }
    }
}

