package br.edu.unifacens.gamificacao.aluno.api;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
/** cursoId estável garante idempotência. Campos novos são opcionais para o cliente AC1. */
public record ConcluirCursoRequest(@NotNull @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal media,
                                  boolean concluido, @Size(max=120) String cursoId, UUID eventoId) {}

