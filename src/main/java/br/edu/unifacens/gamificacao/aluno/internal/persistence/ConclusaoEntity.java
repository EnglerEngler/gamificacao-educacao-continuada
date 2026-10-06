package br.edu.unifacens.gamificacao.aluno.internal.persistence;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name="conclusoes", uniqueConstraints={
    @UniqueConstraint(name="uk_conclusao_curso", columnNames={"instituicao","aluno_id","curso_id"}),
    @UniqueConstraint(name="uk_conclusao_evento", columnNames={"instituicao","evento_id"})
})
public class ConclusaoEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=64) private String instituicao;
    @Column(name="aluno_id", nullable=false) private Long alunoId;
    @Column(name="curso_id", nullable=false, length=120) private String cursoId;
    @Column(name="evento_id", nullable=false) private UUID eventoId;
    protected ConclusaoEntity() {}
    ConclusaoEntity(String instituicao, Long alunoId, String cursoId, UUID eventoId) {
        this.instituicao=instituicao; this.alunoId=alunoId; this.cursoId=cursoId; this.eventoId=eventoId;
    }
}

