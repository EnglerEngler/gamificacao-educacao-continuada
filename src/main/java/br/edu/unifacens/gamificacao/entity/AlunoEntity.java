package br.edu.unifacens.gamificacao.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "alunos")
public class AlunoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private int cursosDisponiveis;
    private int cursosConcluidos;

    @Column(nullable = false)
    private String plano = "BASICO";

    private int moedas;

    protected AlunoEntity() {}

    public AlunoEntity(String nome, int cursosDisponiveis) {
        this.nome = nome;
        this.cursosDisponiveis = cursosDisponiveis;
        this.cursosConcluidos = 0;
        this.plano = "BASICO";
        this.moedas = 0;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }

    public int getCursosDisponiveis() { return cursosDisponiveis; }
    public void setCursosDisponiveis(int cursosDisponiveis) { this.cursosDisponiveis = cursosDisponiveis; }

    public int getCursosConcluidos() { return cursosConcluidos; }
    public void setCursosConcluidos(int cursosConcluidos) { this.cursosConcluidos = cursosConcluidos; }

    public String getPlano() { return plano; }
    public void setPlano(String plano) { this.plano = plano; }

    public int getMoedas() { return moedas; }
    public void setMoedas(int moedas) { this.moedas = moedas; }
}
