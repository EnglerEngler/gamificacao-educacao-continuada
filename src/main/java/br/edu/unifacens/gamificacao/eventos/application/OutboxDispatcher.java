package br.edu.unifacens.gamificacao.eventos.application;
import br.edu.unifacens.gamificacao.eventos.internal.persistence.OutboxRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;

@Component
public class OutboxDispatcher {
    private static final Logger LOG=LoggerFactory.getLogger(OutboxDispatcher.class);
    private final OutboxRepository repository;
    private final NotificacaoPort notificacao;
    private final MeterRegistry metrics;
    public OutboxDispatcher(OutboxRepository repository, NotificacaoPort notificacao, MeterRegistry metrics) {
        this.repository=repository; this.notificacao=notificacao; this.metrics=metrics;
        metrics.gauge("b1.outbox.pending", repository, OutboxRepository::countByEntregueFalse);
    }
    @Scheduled(fixedDelayString="${b1.outbox.interval-ms:1000}")
    @Transactional
    public void despachar() {
        for (var evento: repository.pendentes(Instant.now(), PageRequest.of(0,10))) {
            try {
                notificacao.enviar(evento.getPayload());
                evento.sucesso();
                metrics.counter("b1.outbox.deliveries", "resultado", "sucesso").increment();
            } catch (RuntimeException e) {
                evento.falha();
                metrics.counter("b1.outbox.deliveries", "resultado", "falha").increment();
                LOG.warn("outbox_retry eventId={} tentativa={}", evento.getEventoId(), evento.getTentativas());
            }
        }
    }
}

