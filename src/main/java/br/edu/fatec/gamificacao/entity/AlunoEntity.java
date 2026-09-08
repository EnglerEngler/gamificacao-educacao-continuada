package br.edu.fatec.gamificacao.entity;

import br.edu.fatec.gamificacao.domain.Plano;
import jakarta.persistence.*;

@Entity @Table(name = "alunos")
public class AlunoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String nome;
    private int cursosDisponiveis;
    private int cursosConcluidos;
    @Enumerated(EnumType.STRING) private Plano plano = Plano.BASICO;
    private int moedas;
    protected AlunoEntity() {}
    public AlunoEntity(String nome, int cursosDisponiveis) { this.nome = nome; this.cursosDisponiveis = cursosDisponiveis; }
    public Long getId() { return id; } public String getNome() { return nome; }
    public int getCursosDisponiveis() { return cursosDisponiveis; } public void setCursosDisponiveis(int v) { cursosDisponiveis = v; }
    public int getCursosConcluidos() { return cursosConcluidos; } public void setCursosConcluidos(int v) { cursosConcluidos = v; }
    public Plano getPlano() { return plano; } public void setPlano(Plano v) { plano = v; }
    public int getMoedas() { return moedas; } public void setMoedas(int v) { moedas = v; }
}
