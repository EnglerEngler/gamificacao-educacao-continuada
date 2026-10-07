package br.edu.unifacens.gamificacao.aluno.internal.persistence;
import jakarta.persistence.*;

@Entity
@Table(name="alunos", indexes=@Index(name="idx_aluno_instituicao", columnList="instituicao"))
public class AlunoEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=64) private String instituicao;
    @Column(nullable=false, length=120) private String nome;
    @Column(nullable=false) private int cursosDisponiveis;
    @Column(nullable=false) private int cursosConcluidos;
    @Column(nullable=false) private int cursosAprovados;
    @Column(nullable=false) private String plano = "BASICO";
    @Column(nullable=false) private int moedas;
    @Version private Long versao;
    protected AlunoEntity() {}
    AlunoEntity(String instituicao, String nome, int cursos) {
        this.instituicao = instituicao; this.nome = nome; this.cursosDisponiveis = cursos;
    }
    public Long getId() { return id; }
    public String getInstituicao() { return instituicao; }
    public String getNome() { return nome; }
    public int getCursosDisponiveis() { return cursosDisponiveis; }
    public int getCursosConcluidos() { return cursosConcluidos; }
    public int getCursosAprovados() { return cursosAprovados; }
    public String getPlano() { return plano; }
    public int getMoedas() { return moedas; }
    public void atualizar(int cursos, int concluidos, int moedas, int aprovados, String plano) {
        this.cursosDisponiveis=cursos; this.cursosConcluidos=concluidos; this.moedas=moedas;
        this.cursosAprovados=aprovados; this.plano=plano;
    }
}
