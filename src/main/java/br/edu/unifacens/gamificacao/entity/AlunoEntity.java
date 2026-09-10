package br.edu.unifacens.gamificacao.entity;

import jakarta.persistence.*;

@Entity @Table(name = "alunos")
public class AlunoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String nome;
    private int cursosDisponiveis;
    protected AlunoEntity() {}
    public AlunoEntity(String nome, int cursosDisponiveis) { this.nome = nome; this.cursosDisponiveis = cursosDisponiveis; }
    public Long getId() { return id; } public String getNome() { return nome; }
    public int getCursosDisponiveis() { return cursosDisponiveis; } public void setCursosDisponiveis(int v) { cursosDisponiveis = v; }
}
