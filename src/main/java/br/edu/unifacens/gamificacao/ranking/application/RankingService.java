package br.edu.unifacens.gamificacao.ranking.application;
import br.edu.unifacens.gamificacao.ranking.api.RankingEntry;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RankingService {
    private final RankingPort port;
    private final MeterRegistry metrics;
    public RankingService(RankingPort port, MeterRegistry metrics) { this.port=port; this.metrics=metrics; }
    public List<RankingEntry> listar(String instituicao, int limite, int pagina) {
        return metrics.timer("b1.ranking.latency").record(() -> port.listar(instituicao, limite, pagina*limite));
    }
}

