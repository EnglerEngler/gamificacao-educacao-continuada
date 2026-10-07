package br.edu.unifacens.gamificacao.eventos.api;
import java.math.BigDecimal;
import java.util.UUID;
public record CursoConcluido(UUID eventoId, String instituicao, Long alunoId, String cursoId, BigDecimal media) {}

