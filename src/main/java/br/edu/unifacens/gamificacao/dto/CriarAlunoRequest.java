package br.edu.unifacens.gamificacao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CriarAlunoRequest(@NotBlank String nome, @Min(0) int cursosDisponiveis) { }
