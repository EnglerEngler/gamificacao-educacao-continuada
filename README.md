# Aprende+ — B1 Architecture Lab

Entrega B1: seis decisões evolutivas, Core modular, domínio com Ports & Adapters, integração Python com fallback, eventos duráveis, MQTT e observabilidade.

**Documento oficial: [registro arquitetural cumulativo](docs/arquitetura/README.md)**: identificação da equipe, RF/RNF/RPC, matriz de 18 ASRs (três por desafio), seis ADRs, C4 e síntese para a PoC da AF.

## Entrega principal, desafios e evidências

| Parte da atividade | Abra este arquivo | Conteúdo |
| --- | --- | --- |
| **Entrega principal** | [docs/arquitetura/README.md](docs/arquitetura/README.md) | O único registro cumulativo exigido pelo DOCX, com as seis decisões e a síntese para AF. |
| **Desafios 1 a 6** | [labs/README.md](labs/README.md) | Índice dos desafios; cada pasta explica RF → RNF → ASR/RPC → alternativas → ADR → C4 → microsolução. |
| **Evidências** | [docs/evidencias/b1/README.md](docs/evidencias/b1/README.md) | Resultados verificáveis, separados por desafio e por tipo de arquivo. |

- [ADRs](docs/adr/README.md), com alternativas e consequências.
- [Diagramas C4 e renderizações](docs/arquitetura/diagramas/README.md).

## Onde encontrar cada arquivo

| Local | Finalidade |
| --- | --- |
| `src/` e `frontend/` | Core modular Java, testes e interface Vue da aplicação. |
| [services](services/README.md) | IA, notificações, gateway e simulador Python. |
| `docs/arquitetura/` | Registro cumulativo, matriz de ASRs e diagramas C4 com SVG. |
| `docs/adr/` | As seis decisões arquiteturais. |
| `docs/evidencias/b1/desafio1..6/` | Resultados reais, relatórios, logs e capturas por desafio. |
| `docs/fontes/` | Os três arquivos originais do exercício. |
| `labs/desafio1..6/` | Página de cada desafio e referências à sua microsolução; baseline AC1 preservado no desafio 1. |
| `docs/historico/ac1/` | BDD, planilha, guias e evidências anteriores da AC1. |
| [scripts](scripts/README.md) | Preparação, execução, validação e coleta de evidências. |
| [ops](ops/README.md) | MQTT, monitoramento, plugins Jenkins e seleção de imagens. |

`target/`, `frontend/node_modules/`, `.venv/` e `.runtime/` são gerados localmente e ficam fora do Git e do ZIP de entrega.

Para executar os componentes B1 por containers:

```bash
docker compose -f docker-compose.yml -f docker-compose.b1.yml up --build -d
```

Para reproduzir o laboratório nativo no Ubuntu 24.04 sem Docker:

```bash
bash scripts/bootstrap-lab.sh
.venv/bin/python scripts/lab.py evidence
bash scripts/verify.sh
.venv/bin/python scripts/collect-test-evidence.py
```

Aplicação/Swagger: localhost:8080; Prometheus: localhost:9090; Grafana: localhost:3000 (admin / lab-b1-grafana). O Core continua com os endpoints da AC1 e aceita X-Instituicao (padrão ac1). Para idempotência de conclusão, envie um cursoId estável; payload legado sem identidade representa nova conclusão a cada chamada.

Os serviços Python, gateway, broker e banco têm dados/artefatos separados. Credenciais versionadas são demonstrativas de localhost. O laboratório foi executado com processos reais e H2 persistente; empacotamento/deploy Docker está configurado, mas não foi apresentado como execução realizada quando Docker Desktop estava indisponível.

## Continuidade com a AC1

O [histórico da AC1](docs/historico/ac1/README.md) reúne BDD, planilha, guias individuais e evidências anteriores. A contribuição US03 da equipe foi integrada; Premium conta todas as conclusões e a recompensa por nota exige média > 7,0.
