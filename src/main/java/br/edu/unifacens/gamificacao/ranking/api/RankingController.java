package br.edu.unifacens.gamificacao.ranking.api;
import br.edu.unifacens.gamificacao.ranking.application.RankingService;
import br.edu.unifacens.gamificacao.shared.web.Instituicao;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {
    private final RankingService service;
    public RankingController(RankingService service) { this.service=service; }
    @GetMapping
    public List<RankingEntry> listar(
        @RequestHeader(value="X-Instituicao", defaultValue="ac1") String tenant,
        @RequestParam(defaultValue="20") int limite, @RequestParam(defaultValue="0") int pagina) {
        if (limite < 1 || limite > 100 || pagina < 0 || pagina > 10000)
            throw new IllegalArgumentException("Paginação inválida");
        return service.listar(Instituicao.validar(tenant), limite, pagina);
    }
}

