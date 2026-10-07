package br.edu.unifacens.gamificacao.aluno.application;

import br.edu.unifacens.gamificacao.aluno.api.*;
import br.edu.unifacens.gamificacao.eventos.api.RegistrarEvento;
import br.edu.unifacens.gamificacao.eventos.api.CursoConcluido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class AlunoService implements ConcluirCurso {
    private final AlunoStore store;
    private final RegistrarEvento eventos;

    public AlunoService(AlunoStore store, RegistrarEvento eventos) {
        this.store = store;
        this.eventos = eventos;
    }

    @Transactional
    public AlunoResponse criar(String instituicao, CriarAlunoRequest request) {
        return AlunoResponse.of(store.criar(instituicao, request.nome().trim(), request.cursosDisponiveis()));
    }

    @Transactional(readOnly=true)
    public List<AlunoResponse> listar(String instituicao) {
        return store.listar(instituicao).stream().map(AlunoResponse::of).toList();
    }

    @Transactional
    public AlunoResponse concluirCurso(String instituicao, Long id, ConcluirCursoRequest request) {
        var registro = store.buscarComBloqueio(instituicao, id)
            .orElseThrow(() -> new AlunoNaoEncontrado());
        if (!request.concluido()) return AlunoResponse.of(registro);
        UUID eventoId = request.eventoId() == null ? UUID.randomUUID() : request.eventoId();
        String cursoId = request.cursoId() == null ? eventoId.toString() : request.cursoId();
        if (cursoId.isBlank()) throw new IllegalArgumentException("cursoId não pode ser vazio");
        if (store.jaConcluido(instituicao, id, cursoId)) return AlunoResponse.of(registro);
        registro.aluno().concluirCurso(request.media(), true);
        store.salvarConclusao(registro, cursoId, eventoId);
        eventos.registrar(new CursoConcluido(eventoId, instituicao, id, cursoId, request.media()));
        return AlunoResponse.of(registro);
    }

    public static class AlunoNaoEncontrado extends RuntimeException {
        public AlunoNaoEncontrado() { super("Aluno não encontrado nesta instituição"); }
    }
}
