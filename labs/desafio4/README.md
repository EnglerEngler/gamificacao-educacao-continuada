# Desafio 4 — Síncrono versus assíncrono e recuperação

Decisão: [ADR-004-eventos-outbox.md](../../docs/adr/ADR-004-eventos-outbox.md).

Execute `scripts/lab.py evidence`. O consumidor :8002 é desligado; controle experimental `/api/lab/alunos/{id}/conclusao-sincrona` responde 503, enquanto conclusão real `/api/alunos/{id}/cursos/conclusao` grava recompensa/outbox e responde 200. Core reinicia, consumidor volta e recebe a intenção persistida. A tabela de notificações é o efeito do lab, sem envio de mensagens externas.

Para executar o laboratório nativo:

```bash
bash scripts/bootstrap-lab.sh
.venv/bin/python scripts/lab.py evidence
```

Execute na raiz do repositório. [Evidência](../../docs/evidencias/b1/desafio4/falha-recuperacao.json) e [matriz cumulativa de ASRs](../../docs/arquitetura/README.md).

Hipótese, alternativas, consequências e limites estão no ADR vinculado. Cada lab utiliza o mesmo produto cumulativo; não são seis arquiteturas independentes.

