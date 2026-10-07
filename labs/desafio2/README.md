# Desafio 2 — Organização interna do Core

[Entrega principal](../../docs/arquitetura/README.md) · [Índice dos desafios](../README.md) · [Evidências deste desafio](../../docs/evidencias/b1/desafio2/README.md)

## Cenário e hipótese

Regras de cursos, moedas, badges e recompensas precisam evoluir sem depender do banco e do framework.

**Hipótese testada:** O mesmo contrato de caso de uso funciona com fake e JPA mantendo regras de aprovação e Premium.

## Cadeia de decisão exigida pelo DOCX

| Etapa | Aplicação neste desafio |
| --- | --- |
| 1. RF | RF01 Ranking; RF02 Conquistas e regras de gamificação. |
| 2. RNF | Manutenibilidade, testabilidade, modificabilidade e facilidade de evolução. |
| 3. ASR | ASR-04 Testabilidade; ASR-05 Modificabilidade; ASR-06 Manutenibilidade. Impactos estruturais e critérios estão na matriz cumulativa da entrega principal. |
| 4. RPC | Preservar Core Spring Boot e as regras AC1; evitar reescrever o produto para aplicar um padrão. |
| 5. Alternativas/trade-offs | Estrutura AC1 com aplicação ligada a JPA versus Ports & Adapters. A porta acrescenta interfaces/mapeamento, mas permite trocar persistência e testar a mesma regra. DDD orienta limites e linguagem; não obriga nova infraestrutura. |
| 6. ADR | [ADR-002-ports-adapters.md](../../docs/adr/ADR-002-ports-adapters.md). Decisão: Domínio puro e porta AlunoStore, com adapter fake nos testes e JPA na aplicação. |
| 7. C4 | [d2-component](../../docs/arquitetura/diagramas/d2-component.mmd); SVGs no [índice dos diagramas](../../docs/arquitetura/diagramas/README.md). |
| 8. Evidência | [testes.json](../../docs/evidencias/b1/desafio2/testes.json); [index.html](../../docs/evidencias/b1/desafio2/cobertura/index.html). [Guia de leitura do resultado](../../docs/evidencias/b1/desafio2/README.md). |

## Onde está a microsolução

| Arquivo/pasta do produto | Papel no experimento |
| --- | --- |
| [Aluno.java](../../src/main/java/br/edu/unifacens/gamificacao/aluno/domain/Aluno.java) | Domínio puro |
| [AlunoStore.java](../../src/main/java/br/edu/unifacens/gamificacao/aluno/application/AlunoStore.java) | Porta de persistência |
| [JpaAlunoAdapter.java](../../src/main/java/br/edu/unifacens/gamificacao/aluno/internal/persistence/JpaAlunoAdapter.java) | Adapter JPA |
| [AlunoUseCaseContract.java](../../src/test/java/br/edu/unifacens/gamificacao/aluno/application/AlunoUseCaseContract.java) | Contrato compartilhado |
| [FakeAdapterContractTest.java](../../src/test/java/br/edu/unifacens/gamificacao/aluno/application/FakeAdapterContractTest.java) | Execução com fake |
| [JpaAdapterContractTest.java](../../src/test/java/br/edu/unifacens/gamificacao/aluno/application/JpaAdapterContractTest.java) | Execução com JPA |

## Executar e conferir

Execute na raiz do repositório. Prepare o ambiente com `bash scripts/bootstrap-lab.sh`. O comando `lab.py evidence` executa os cenários cumulativos e encerra/reinicia somente processos registrados pelo laboratório.

```bash
mvn -B verify
.venv/bin/python scripts/collect-test-evidence.py
```

Contratos fake/JPA passam; 25 testes Java e quatro Python registrados, domínio com 100% de linhas/branches. Média > 7,0 libera cursos; todas as conclusões contam para Premium. Fake não comprova locks/transações do banco.

A avaliação usa o mesmo padrão: RF/RNF/ASR (0,25), RPC/alternativas (0,20), ADR (0,20), C4 (0,15) e microsolução/evidência/Git (0,20). Justificativa e consequências completas estão no ADR; os resultados brutos permanecem na pasta de evidências.

Para renovar os quatro testes Python antes da coleta, execute `.venv/bin/python -m pytest -q tests --junitxml=.runtime/python-tests.xml`.
