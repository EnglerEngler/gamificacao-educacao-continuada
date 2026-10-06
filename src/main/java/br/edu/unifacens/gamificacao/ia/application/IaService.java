package br.edu.unifacens.gamificacao.ia.application;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.Supplier;

@Service
public class IaService {
    private final IaPort port;
    private final MeterRegistry metrics;
    public IaService(IaPort port, MeterRegistry metrics) { this.port=port; this.metrics=metrics; }
    public Map<String,Object> recomendar(String interesse) {
        return executar(() -> port.recomendar(interesse), Map.of("degradado",true,
            "cursos",List.of("Fundamentos de programação"), "origem","catalogo-local"));
    }
    public Map<String,Object> perguntar(String pergunta) {
        return executar(() -> port.perguntar(pergunta), Map.of("degradado",true,
            "resposta","Assistente temporariamente indisponível. Consulte o material do curso.",
            "fontes",List.of(), "origem","fallback"));
    }
    private Map<String,Object> executar(Supplier<Map<String,Object>> chamada, Map<String,Object> fallback) {
        return metrics.timer("b1.ia.latency").record(() -> {
            try {
                var resposta=chamada.get();
                if (resposta == null) throw new IllegalStateException("Resposta vazia");
                metrics.counter("b1.ia.requests","resultado","sucesso").increment();
                return resposta;
            } catch (RuntimeException e) {
                metrics.counter("b1.ia.requests","resultado","fallback").increment();
                return fallback;
            }
        });
    }
}

