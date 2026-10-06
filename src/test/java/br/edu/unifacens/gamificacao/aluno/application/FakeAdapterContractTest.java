package br.edu.unifacens.gamificacao.aluno.application;
class FakeAdapterContractTest extends AlunoUseCaseContract {
    private final AlunoService service=new AlunoService(new FakeAlunoStore(),evento->{});
    AlunoService service() { return service; }
}

