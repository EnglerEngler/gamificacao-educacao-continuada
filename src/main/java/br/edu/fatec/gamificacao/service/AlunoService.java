package br.edu.fatec.gamificacao.service;

import br.edu.fatec.gamificacao.domain.Aluno;
import br.edu.fatec.gamificacao.dto.*;
import br.edu.fatec.gamificacao.entity.AlunoEntity;
import br.edu.fatec.gamificacao.repository.AlunoRepository;
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
        Aluno aluno = new Aluno(entity.getCursosDisponiveis(), entity.getCursosConcluidos(), entity.getPlano(), entity.getMoedas());
        aluno.concluirCurso(request.media(), request.concluido());
        entity.setCursosDisponiveis(aluno.getCursosDisponiveis()); entity.setCursosConcluidos(aluno.getCursosConcluidos());
        entity.setPlano(aluno.getPlano()); entity.setMoedas(aluno.getMoedas());
        return AlunoResponse.of(entity);
    }
    @Transactional
    public AlunoResponse premiarForum(Long id) {
        AlunoEntity entity = buscar(id);
        Aluno aluno = new Aluno(entity.getCursosDisponiveis(), entity.getCursosConcluidos(), entity.getPlano(), entity.getMoedas());
        aluno.premiarParticipacaoForum(); entity.setCursosDisponiveis(aluno.getCursosDisponiveis());
        return AlunoResponse.of(entity);
    }
    private AlunoEntity buscar(Long id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado")); }
}
