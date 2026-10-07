package br.edu.unifacens.gamificacao.aluno.application;
import java.util.*;
import br.edu.unifacens.gamificacao.aluno.domain.Aluno;

final class FakeAlunoStore implements AlunoStore {
    private final Map<Long,Registro> registros=new HashMap<>();
    private final Set<String> conclusoes=new HashSet<>();
    private long sequence;
    public Registro criar(String tenant,String nome,int cursos) {
        var r=new Registro(++sequence,tenant,nome,new Aluno(cursos));
        registros.put(r.id(),r); return r;
    }
    public List<Registro> listar(String tenant) {
        return registros.values().stream().filter(r->r.instituicao().equals(tenant)).toList();
    }
    public Optional<Registro> buscarComBloqueio(String tenant,Long id) {
        return Optional.ofNullable(registros.get(id)).filter(r->r.instituicao().equals(tenant));
    }
    public boolean jaConcluido(String tenant,Long id,String curso) { return conclusoes.contains(tenant+":"+id+":"+curso); }
    public void salvarConclusao(Registro r,String curso,UUID event) {
        registros.put(r.id(),r); conclusoes.add(r.instituicao()+":"+r.id()+":"+curso);
    }
}

