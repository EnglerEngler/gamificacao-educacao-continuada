package br.edu.unifacens.gamificacao.eventos.internal.persistence;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="outbox", indexes=@Index(name="idx_outbox_pendentes", columnList="entregue,proxima_tentativa"),
    uniqueConstraints=@UniqueConstraint(name="uk_outbox_evento", columnNames={"instituicao","evento_id"}))
public class OutboxEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="evento_id", nullable=false) private UUID eventoId;
    @Column(nullable=false, length=64) private String instituicao;
    @Column(nullable=false, length=4000) private String payload;
    @Column(nullable=false) private boolean entregue;
    @Column(nullable=false) private int tentativas;
    @Column(name="proxima_tentativa", nullable=false) private Instant proximaTentativa;
    protected OutboxEntity() {}
    public OutboxEntity(UUID eventoId, String instituicao, String payload) {
        this.eventoId=eventoId; this.instituicao=instituicao; this.payload=payload;
        this.proximaTentativa=Instant.now();
    }
    public String getPayload() { return payload; }
    public UUID getEventoId() { return eventoId; }
    public int getTentativas() { return tentativas; }
    public void sucesso() { entregue=true; }
    public void falha() {
        tentativas++;
        proximaTentativa=Instant.now().plusSeconds(Math.min(60, 1L << Math.min(tentativas, 6)));
    }
}

