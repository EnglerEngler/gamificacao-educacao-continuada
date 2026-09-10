package br.edu.unifacens.gamificacao.controller;

import br.edu.unifacens.gamificacao.dto.*;
import br.edu.unifacens.gamificacao.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/alunos") @CrossOrigin(origins = "http://localhost:5173")
public class AlunoController {
    private final AlunoService service;
    public AlunoController(AlunoService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary = "Cadastra um aluno")
    public AlunoResponse criar(@Valid @RequestBody CriarAlunoRequest request) { return service.criar(request); }
    @GetMapping @Operation(summary = "Lista os alunos")
    public List<AlunoResponse> listar() { return service.listar(); }
    @PostMapping("/{id}/cursos/conclusao") @Operation(summary = "Registra a conclusão de curso e aplica a recompensa")
    public AlunoResponse concluir(@PathVariable Long id, @Valid @RequestBody ConcluirCursoRequest request) { return service.concluirCurso(id, request); }
    @PostMapping("/{id}/forum/premiacao") @Operation(summary = "Premia participação de destaque no fórum")
    public AlunoResponse premiarForum(@PathVariable Long id) { return service.premiarForum(id); }
}
