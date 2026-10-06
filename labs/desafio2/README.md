# Desafio 2 — Trocar adapter sem trocar regra

Decisão: [ADR-002-ports-adapters.md](../../docs/adr/ADR-002-ports-adapters.md).

Execute `mvn verify`. `AlunoUseCaseContract` é herdado por `FakeAdapterContractTest` e `JpaAdapterContractTest`. O domínio mantém > 7,0 da AC1; `ConquistasTest` verifica badge/Premium. `CoreIntegrationTest` testa atualização concorrente do mesmo curso. Veja C4 Component e porta `AlunoStore`; fake não simula garantias de lock/transação do banco.

Para executar o laboratório nativo:

```bash
bash scripts/bootstrap-lab.sh
.venv/bin/python scripts/lab.py evidence
```

Execute na raiz do repositório. [Evidência](../../docs/evidencias/b1/desafio2/testes.json) e [matriz cumulativa de ASRs](../../docs/arquitetura/README.md).

Hipótese, alternativas, consequências e limites estão no ADR vinculado. Cada lab utiliza o mesmo produto cumulativo; não são seis arquiteturas independentes.

