# Desafio 4 — Comunicação e eventos

[Entrega principal](../../docs/arquitetura/README.md) · [Índice dos desafios](../README.md) · [Evidências deste desafio](../../docs/evidencias/b1/desafio4/README.md)

## Cenário e hipótese

Concluir curso exige resposta imediata, enquanto uma falha de notificação deve permitir recuperação posterior.

**Hipótese testada:** Consumidor offline causa falha no controle síncrono, mas preserva a conclusão assíncrona e sua intenção após reiniciar Core.

## Cadeia de decisão exigida pelo DOCX

| Etapa | Aplicação neste desafio |
| --- | --- |
| 1. RF | RF02 Conquistas; RF05 Notificações; evento de conclusão. |
| 2. RNF | Tolerância a falhas, disponibilidade, latência, consistência, desacoplamento e recuperabilidade. |
| 3. ASR | ASR-10 Tolerância a falhas; ASR-11 Consistência; ASR-12 Recuperabilidade. Impactos estruturais e critérios estão na matriz cumulativa da entrega principal. |
| 4. RPC | Operações essenciais concluem com consumidor externo indisponível; broker acrescenta custo operacional. |
| 5. Alternativas/trade-offs | REST síncrono versus efeito assíncrono por outbox/HTTP; mensageria interna só quando fan-out, retenção ou volume justificarem. |
| 6. ADR | [ADR-004-eventos-outbox.md](../../docs/adr/ADR-004-eventos-outbox.md). Decisão: Conclusão/recompensa/outbox na mesma transação; worker entrega notificações eventualmente, com retries e consumidor idempotente. |
| 7. C4 | [d4-container](../../docs/arquitetura/diagramas/d4-container.mmd); [d4-sequencia](../../docs/arquitetura/diagramas/d4-sequencia.mmd); SVGs no [índice dos diagramas](../../docs/arquitetura/diagramas/README.md). |
| 8. Evidência | [falha-recuperacao.json](../../docs/evidencias/b1/desafio4/falha-recuperacao.json). [Guia de leitura do resultado](../../docs/evidencias/b1/desafio4/README.md). |

## Onde está a microsolução

| Arquivo/pasta do produto | Papel no experimento |
| --- | --- |
| [LabSincronoController.java](../../src/main/java/br/edu/unifacens/gamificacao/eventos/api/LabSincronoController.java) | Controle síncrono experimental |
| [JpaRegistrarEvento.java](../../src/main/java/br/edu/unifacens/gamificacao/eventos/internal/persistence/JpaRegistrarEvento.java) | Outbox na transação |
| [OutboxDispatcher.java](../../src/main/java/br/edu/unifacens/gamificacao/eventos/application/OutboxDispatcher.java) | Retentativas assíncronas |
| [main.py](../../services/notificacoes/main.py) | Consumidor idempotente |
| [TransactionRollbackTest.java](../../src/test/java/br/edu/unifacens/gamificacao/integration/TransactionRollbackTest.java) | Rollback da intenção e recompensa |

## Executar e conferir

Execute na raiz do repositório. Prepare o ambiente com `bash scripts/bootstrap-lab.sh`. O comando `lab.py evidence` executa os cenários cumulativos e encerra/reinicia somente processos registrados pelo laboratório.

```bash
.venv/bin/python scripts/lab.py evidence
```

Controle síncrono responde 503; API real responde 200. Recompensa/evento sobrevivem ao reinício e o consumidor recebe após recuperar. A entrega é at-least-once; o consumidor deduplica. Nenhuma mensagem é enviada a pessoas.

A avaliação usa o mesmo padrão: RF/RNF/ASR (0,25), RPC/alternativas (0,20), ADR (0,20), C4 (0,15) e microsolução/evidência/Git (0,20). Justificativa e consequências completas estão no ADR; os resultados brutos permanecem na pasta de evidências.
