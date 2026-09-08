package br.edu.fatec.gamificacao.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ConcluirCursoRequest(@NotNull @DecimalMin("0.0") BigDecimal media, boolean concluido) { }
