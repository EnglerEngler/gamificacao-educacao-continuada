package br.edu.unifacens.gamificacao.ranking.internal.persistence;
import br.edu.unifacens.gamificacao.ranking.application.RankingPort;
import br.edu.unifacens.gamificacao.ranking.api.RankingEntry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

/** Read model SQL; não importa entidades nem repositories internos de outro módulo. */
@Repository
public class JdbcRankingAdapter implements RankingPort {
    private final JdbcTemplate jdbc;
    public JdbcRankingAdapter(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    public List<RankingEntry> listar(String instituicao, int limite, int offset) {
        return jdbc.query("""
            select id, nome, cursos_concluidos, moedas, cursos_concluidos*100+moedas as pontos
            from alunos where instituicao=?
            order by pontos desc, id asc limit ? offset ?
            """, (r,n) -> new RankingEntry(r.getLong("id"), r.getString("nome"),
                r.getInt("cursos_concluidos"), r.getInt("moedas"), r.getInt("pontos")),
            instituicao, limite, offset);
    }
}

