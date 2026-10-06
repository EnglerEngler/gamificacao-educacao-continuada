# Desafio 3 — Spring Boot consumindo Python e tratando falha

Decisão: [ADR-003-python-ia.md](../../docs/adr/ADR-003-python-ia.md).

Execute `scripts/lab.py evidence`. O script consulta recomendações/assistente, encerra de verdade o processo FastAPI, consulta fallback e verifica ranking, depois reinicia Python. Recomendador por tags e retrieval extrativo são microsoluções de contrato/fronteira, sem modelo/LLM externo. Pode verificar manualmente endpoints `:8001/recomendacoes` e `:8080/api/ia/recomendacoes`.

Para executar o laboratório nativo:

```bash
bash scripts/bootstrap-lab.sh
.venv/bin/python scripts/lab.py evidence
```

Execute na raiz do repositório. [Evidência](../../docs/evidencias/b1/desafio3/falha-ia.json) e [matriz cumulativa de ASRs](../../docs/arquitetura/README.md).

Hipótese, alternativas, consequências e limites estão no ADR vinculado. Cada lab utiliza o mesmo produto cumulativo; não são seis arquiteturas independentes.

