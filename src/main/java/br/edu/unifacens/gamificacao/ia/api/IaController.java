package br.edu.unifacens.gamificacao.ia.api;
import br.edu.unifacens.gamificacao.ia.application.IaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/ia")
public class IaController {
    public record Pergunta(@NotBlank @Size(max=1000) String pergunta) {}
    private final IaService service;
    public IaController(IaService service) { this.service=service; }
    @GetMapping("/recomendacoes")
    public Map<String,Object> recomendar(@RequestParam(defaultValue="programacao") String interesse) {
        if (interesse.length()>200) throw new IllegalArgumentException("Interesse muito longo");
        return service.recomendar(interesse);
    }
    @PostMapping("/assistente")
    public Map<String,Object> perguntar(@Valid @RequestBody Pergunta pergunta) {
        return service.perguntar(pergunta.pergunta());
    }
}

