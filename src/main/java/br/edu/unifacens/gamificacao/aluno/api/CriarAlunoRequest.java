package br.edu.unifacens.gamificacao.aluno.api;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record CriarAlunoRequest(@NotBlank @Size(max=120) String nome, @Min(0) int cursosDisponiveis) {}

