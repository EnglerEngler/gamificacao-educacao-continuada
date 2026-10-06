# Desafio 6 — Jenkins, Prometheus e Grafana

Decisão: [ADR-006-operacao.md](../../docs/adr/ADR-006-operacao.md).

Jenkinsfile executa gates de Core/Python/frontend/configurações; imagens e deploy são opcionais em agente com Docker. `scripts/lab.py evidence` provoca fallbacks/fila e consulta Prometheus :9090, validando dashboard provisionado no Grafana :3000. Exportações e capturas em evidências documentam condições reais. `bash scripts/verify.sh` repete os gates localmente; execução real do Jenkins está em `jenkins.json`.

Para executar o laboratório nativo:

```bash
bash scripts/bootstrap-lab.sh
.venv/bin/python scripts/lab.py evidence
```

Execute na raiz do repositório. [Evidência](../../docs/evidencias/b1/desafio6/prometheus-grafana.json) e [matriz cumulativa de ASRs](../../docs/arquitetura/README.md).

Para repetir o Jenkins local depois de preparar Python/uv com bootstrap-lab.sh e versionar as alterações, execute `bash scripts/bootstrap-jenkins.sh`. O job lê o Jenkinsfile do commit da branch atual; não executa alterações não commitadas. O controller é isolado em /tmp/b1-tools/jenkins-home e escuta localhost:8090 (b1 / lab-b1-jenkins). Um agente Jenkins já instalado pode usar diretamente o Jenkinsfile do repositório, com JDK 21, Maven, Node 20 e uv no PATH. Plugins do controller são resolvidos pela versão do core; o manifesto da execução é capturado na evidência.

Hipótese, alternativas, consequências e limites estão no ADR vinculado. Cada lab utiliza o mesmo produto cumulativo; não são seis arquiteturas independentes.
