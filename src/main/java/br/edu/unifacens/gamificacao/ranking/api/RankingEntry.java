package br.edu.unifacens.gamificacao.ranking.api;
public record RankingEntry(Long alunoId, String nome, int cursosConcluidos, int cursosAprovados, int moedas, int pontos) {}
