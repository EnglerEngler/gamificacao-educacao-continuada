# Desafio 1 — Comparar dependências da funcionalidade AC1

Decisão: [ADR-001-core-modular.md](../../docs/adr/ADR-001-core-modular.md).

Desafio 1 usa snapshot real da AC1, commit b31abd3, e o caso de uso atual. Execute `python scripts/architecture-evidence.py` para comparar imports/direção; `mvn verify` valida fronteiras com ArchUnit. O resultado não confunde agrupamento de pastas com isolamento de dependências. O cenário de 100 mil registros/30 consultas sequenciais é executado por `scripts/lab.py evidence`, sem afirmar simultaneidade.

Para executar o laboratório nativo:

```bash
bash scripts/bootstrap-lab.sh
.venv/bin/python scripts/lab.py evidence
```

Execute na raiz do repositório. [Evidência](../../docs/evidencias/b1/desafio1/dependencias.json) e [matriz cumulativa de ASRs](../../docs/arquitetura/README.md).

Hipótese, alternativas, consequências e limites estão no ADR vinculado. Cada lab utiliza o mesmo produto cumulativo; não são seis arquiteturas independentes.

