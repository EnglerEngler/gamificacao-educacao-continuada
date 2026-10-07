package br.edu.unifacens.gamificacao.ranking.application;
import br.edu.unifacens.gamificacao.ranking.api.RankingEntry;
import java.util.List;
public interface RankingPort { List<RankingEntry> listar(String instituicao, int limite, int offset); }

