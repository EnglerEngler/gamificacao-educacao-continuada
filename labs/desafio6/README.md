# Desafio 6 — CI/CD e observabilidade

[Entrega principal](../../docs/arquitetura/README.md) · [Índice dos desafios](../README.md) · [Evidências deste desafio](../../docs/evidencias/b1/desafio6/README.md)

## Cenário e hipótese

Os componentes precisam de gates de construção e métricas que demonstrem falhas/recuperações ligadas às decisões anteriores.

**Hipótese testada:** Pipeline detecta regressão e métricas de fallback, fila, falha/entrega e ranking registram as condições provocadas.

## Cadeia de decisão exigida pelo DOCX

| Etapa | Aplicação neste desafio |
| --- | --- |
| 1. RF | Todos os RFs afetados pela operação e evolução. |
| 2. RNF | Observabilidade, deploy independente, confiabilidade, testabilidade, recuperabilidade e desempenho. |
| 3. ASR | ASR-16 Testabilidade; ASR-17 Observabilidade; ASR-18 Recuperabilidade. Impactos estruturais e critérios estão na matriz cumulativa da entrega principal. |
| 4. RPC | Jenkins como esteira; Prometheus/Grafana como ferramentas; configurações e artefatos versionados. |
| 5. Alternativas/trade-offs | Pipeline integrado versus pipelines por componente. Autonomia de execução não exige esteira separada sem evidência de gargalo; gates e métricas são escolhidos por ASR. |
| 6. ADR | [ADR-006-operacao.md](../../docs/adr/ADR-006-operacao.md). Decisão: Pipeline integrado, stages por componente, imagens identificadas por build e dashboard provisionado com oito painéis. |
| 7. C4 | [d6-deployment](../../docs/arquitetura/diagramas/d6-deployment.mmd); [container-final](../../docs/arquitetura/diagramas/container-final.mmd); SVGs no [índice dos diagramas](../../docs/arquitetura/diagramas/README.md). |
| 8. Evidência | [jenkins.json](../../docs/evidencias/b1/desafio6/jenkins/jenkins.json); [prometheus-grafana.json](../../docs/evidencias/b1/desafio6/monitoramento/prometheus-grafana.json); [grafana-dashboard.png](../../docs/evidencias/b1/desafio6/capturas/grafana-dashboard.png). [Guia de leitura do resultado](../../docs/evidencias/b1/desafio6/README.md). |

## Onde está a microsolução

| Arquivo/pasta do produto | Papel no experimento |
| --- | --- |
| [Jenkinsfile](../../Jenkinsfile) | Esteira, gates e JAR executável com interface |
| [prometheus.yml](../../ops/prometheus/prometheus.yml) | Scrape |
| [alerts.yml](../../ops/prometheus/alerts.yml) | Alertas |
| [b1.json](../../ops/grafana/dashboards/b1.json) | Oito painéis |
| [compose.release.yml](../../ops/compose.release.yml) | Imagens versionadas |
| [jenkins-lab.py](../../scripts/jenkins-lab.py) | Execução e coleta de Jenkins |

## Executar e conferir

Execute na raiz do repositório. Prepare o ambiente com `bash scripts/bootstrap-lab.sh`. O comando `lab.py evidence` executa os cenários cumulativos e encerra/reinicia somente processos registrados pelo laboratório.

```bash
.venv/bin/python scripts/validate-delivery.py
.venv/bin/python scripts/lab.py evidence
bash scripts/bootstrap-jenkins.sh
```

Jenkins real com resultado SUCCESS e 29 testes; Prometheus/Grafana registram falha e recuperação. Imagens/deploy Docker são stages opcionais não executados no WSL; processos nativos comprovaram o mini-lab.

A avaliação usa o mesmo padrão: RF/RNF/ASR (0,25), RPC/alternativas (0,20), ADR (0,20), C4 (0,15) e microsolução/evidência/Git (0,20). Justificativa e consequências completas estão no ADR; os resultados brutos permanecem na pasta de evidências.

O Jenkins local usa o commit da branch atual. Faça commit antes de executar sua esteira. Controller em `/tmp/b1-tools/jenkins-home`, localhost:8090, `b1 / lab-b1-jenkins`; agente com Java 21, Maven, Node e uv. Stages Docker só podem ser habilitados com Docker funcional.
