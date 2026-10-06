package br.edu.unifacens.gamificacao.service;

import br.edu.unifacens.gamificacao.domain.Aluno;
import br.edu.unifacens.gamificacao.dto.*;
import br.edu.unifacens.gamificacao.entity.AlunoEntity;
import br.edu.unifacens.gamificacao.repository.AlunoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AlunoService {
    private final AlunoRepository repository;
    public AlunoService(AlunoRepository repository) { this.repository = repository; }
    public AlunoResponse criar(CriarAlunoRequest request) { return AlunoResponse.of(repository.save(new AlunoEntity(request.nome(), request.cursosDisponiveis()))); }
    public List<AlunoResponse> listar() { return repository.findAll().stream().map(AlunoResponse::of).toList(); }
    @Transactional
    public AlunoResponse concluirCurso(Long id, ConcluirCursoRequest request) {
        AlunoEntity entity = buscar(id);
        Aluno aluno = new Aluno(entity.getCursosDisponiveis());
        aluno.concluirCurso(request.media(), request.concluido());
        entity.setCursosDisponiveis(aluno.getCursosDisponiveis());
        return AlunoResponse.of(entity);
    }
    private AlunoEntity buscar(Long id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado")); }
}
