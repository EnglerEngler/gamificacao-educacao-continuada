package br.edu.unifacens.gamificacao.iot.internal.persistence;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="iot_receipts",uniqueConstraints=@UniqueConstraint(name="uk_iot_evento",columnNames={"instituicao","evento_id"}))
public class IotReceipt {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=64) private String instituicao;
    @Column(name="evento_id",nullable=false) private UUID eventoId;
    @Column(nullable=false, length=120) private String dispositivoId;
    @Column(nullable=false) private String tipo;
    @Column(nullable=false) private Instant recebidoEm;
    protected IotReceipt() {}
    public IotReceipt(String instituicao, UUID eventoId, String dispositivoId, String tipo) {
        this.instituicao=instituicao; this.eventoId=eventoId; this.dispositivoId=dispositivoId;
        this.tipo=tipo; this.recebidoEm=Instant.now();
    }
}

