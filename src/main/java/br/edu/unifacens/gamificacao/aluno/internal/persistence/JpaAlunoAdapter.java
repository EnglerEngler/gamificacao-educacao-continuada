package br.edu.unifacens.gamificacao.aluno.internal.persistence;

import br.edu.unifacens.gamificacao.aluno.application.AlunoStore;
import br.edu.unifacens.gamificacao.aluno.domain.Aluno;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAlunoAdapter implements AlunoStore {
    private final AlunoJpaRepository alunos;
    private final ConclusaoJpaRepository conclusoes;
    public JpaAlunoAdapter(AlunoJpaRepository alunos, ConclusaoJpaRepository conclusoes) {
        this.alunos=alunos; this.conclusoes=conclusoes;
    }
    public Registro criar(String instituicao, String nome, int cursos) {
        return map(alunos.save(new AlunoEntity(instituicao, nome, cursos)));
    }
    public List<Registro> listar(String instituicao) {
        return alunos.findByInstituicaoOrderById(instituicao).stream().map(this::map).toList();
    }
    public Optional<Registro> buscarComBloqueio(String instituicao, Long id) {
        return alunos.buscarComBloqueio(instituicao, id).map(this::map);
    }
    public boolean jaConcluido(String instituicao, Long alunoId, String cursoId) {
        return conclusoes.existsByInstituicaoAndAlunoIdAndCursoId(instituicao, alunoId, cursoId);
    }
    public void salvarConclusao(Registro r, String cursoId, UUID eventoId) {
        var e = alunos.buscarComBloqueio(r.instituicao(), r.id()).orElseThrow();
        e.atualizar(r.aluno().getCursosDisponiveis(), r.aluno().getCursosConcluidos(), r.aluno().getMoedas(),
            r.aluno().getCursosAprovados(), r.aluno().getPlano());
        alunos.save(e);
        conclusoes.saveAndFlush(new ConclusaoEntity(r.instituicao(), r.id(), cursoId, eventoId));
    }
    private Registro map(AlunoEntity e) {
        return new Registro(e.getId(), e.getInstituicao(), e.getNome(),
            new Aluno(e.getCursosDisponiveis(), e.getCursosConcluidos(), e.getPlano(), e.getMoedas(), e.getCursosAprovados()));
    }
}
