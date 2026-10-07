# Desafio 1 — Drivers e evolução do Core

[Entrega principal](../../docs/arquitetura/README.md) · [Índice dos desafios](../README.md) · [Evidências deste desafio](../../docs/evidencias/b1/desafio1/README.md)

## Cenário e hipótese

O cenário cresce de mil para até 100 mil usuários; ranking e conquistas evoluem com perfis distintos. A decisão macro precede a decomposição.

**Hipótese testada:** Modularizar a conclusão elimina import de JPA no caso de uso; ranking limitado evita materializar toda a população.

## Cadeia de decisão exigida pelo DOCX

| Etapa | Aplicação neste desafio |
| --- | --- |
| 1. RF | RF01 Ranking; RF02 Conquistas. |
| 2. RNF | Manutenibilidade, desempenho, escalabilidade, testabilidade e complexidade operacional. |
| 3. ASR | ASR-01 Manutenibilidade; ASR-02 Desempenho; ASR-03 Complexidade operacional. Impactos estruturais e critérios estão na matriz cumulativa da entrega principal. |
| 4. RPC | Core Java/Spring Boot, banco relacional, legado AC1 e premissa acadêmica de seis desenvolvedores. |
| 5. Alternativas/trade-offs | Camadas versus monólito modular versus decomposição seletiva. Modular reduz acoplamento estrutural com um deploy; serviços acrescentam rede e coordenação de dados antes de haver escala independente comprovada. |
| 6. ADR | [ADR-001-core-modular.md](../../docs/adr/ADR-001-core-modular.md). Decisão: Core como monólito modular; ranking com leitura limitada, sem extrair serviço agora. |
| 7. C4 | [d1-container](../../docs/arquitetura/diagramas/d1-container.mmd); SVGs no [índice dos diagramas](../../docs/arquitetura/diagramas/README.md). |
| 8. Evidência | [dependencias.json](../../docs/evidencias/b1/desafio1/dependencias.json); [ranking-carga.json](../../docs/evidencias/b1/desafio1/ranking-carga.json). [Guia de leitura do resultado](../../docs/evidencias/b1/desafio1/README.md). |

## Onde está a microsolução

| Arquivo/pasta do produto | Papel no experimento |
| --- | --- |
| [AlunoService.java](../../src/main/java/br/edu/unifacens/gamificacao/aluno/application/AlunoService.java) | Caso de uso modularizado |
| [JdbcRankingAdapter.java](../../src/main/java/br/edu/unifacens/gamificacao/ranking/internal/persistence/JdbcRankingAdapter.java) | Read model limitado |
| [ArchitectureTest.java](../../src/test/java/br/edu/unifacens/gamificacao/architecture/ArchitectureTest.java) | Fronteiras verificadas |
| [baseline](../../labs/desafio1/baseline) | Snapshot AC1 b31abd3 |

## Executar e conferir

Execute na raiz do repositório. Prepare o ambiente com `bash scripts/bootstrap-lab.sh`. O comando `lab.py evidence` executa os cenários cumulativos e encerra/reinicia somente processos registrados pelo laboratório.

```bash
.venv/bin/python scripts/architecture-evidence.py
.venv/bin/python scripts/lab.py evidence
```

O caso de uso passa a depender de uma porta. Em 30 consultas sequenciais sobre 100 mil registros H2, a resposta tem 20 itens; os tempos medidos estão no JSON. Isso não representa 100 mil usuários simultâneos.

A avaliação usa o mesmo padrão: RF/RNF/ASR (0,25), RPC/alternativas (0,20), ADR (0,20), C4 (0,15) e microsolução/evidência/Git (0,20). Justificativa e consequências completas estão no ADR; os resultados brutos permanecem na pasta de evidências.
