package br.edu.unifacens.gamificacao.aluno.api;

import br.edu.unifacens.gamificacao.aluno.application.AlunoService;
import br.edu.unifacens.gamificacao.shared.web.Instituicao;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@CrossOrigin(origins="http://localhost:5173")
public class AlunoController {
    private final AlunoService service;
    public AlunoController(AlunoService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public AlunoResponse criar(@RequestHeader(value="X-Instituicao", defaultValue="ac1") String tenant,
                               @Valid @RequestBody CriarAlunoRequest request) {
        return service.criar(Instituicao.validar(tenant), request);
    }
    @GetMapping
    public List<AlunoResponse> listar(@RequestHeader(value="X-Instituicao", defaultValue="ac1") String tenant) {
        return service.listar(Instituicao.validar(tenant));
    }
    @PostMapping("/{id}/cursos/conclusao")
    public AlunoResponse concluir(@PathVariable Long id,
            @RequestHeader(value="X-Instituicao", defaultValue="ac1") String tenant,
            @Valid @RequestBody ConcluirCursoRequest request) {
        return service.concluirCurso(Instituicao.validar(tenant), id, request);
    }
}

