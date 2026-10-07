package br.edu.unifacens.gamificacao.aluno.application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties={"spring.profiles.active=h2","b1.outbox.interval-ms=3600000"})
@Transactional
class JpaAdapterContractTest extends AlunoUseCaseContract {
    @Autowired private AlunoService service;
    AlunoService service() { return service; }
}

