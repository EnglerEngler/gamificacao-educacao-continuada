# Operação do B1

| Pasta/arquivo | Conteúdo |
| --- | --- |
| `mqtt/` | Mosquitto: persistência, autenticação e ACL por tópico/dispositivo. |
| `prometheus/` | Scrape do Core/gateway a cada 2 s e regras de alerta. |
| `grafana/` | Datasource, provisionamento e dashboard com oito painéis ligados aos ASRs. |
| `jenkins/plugins.txt` | Manifesto completo dos plugins e versões do Jenkins usado nas evidências. |
| `compose.release.yml` | Seleção de imagens `b1-core`/`b1-python` identificadas pelo número do build. |

A esteira está no [Jenkinsfile](../Jenkinsfile). A decisão, os riscos e a estratégia de rollback estão no [ADR-006](../docs/adr/ADR-006-operacao.md); a [evidência do desafio 6](../docs/evidencias/b1/README.md) contém o resultado real e os stages opcionais pulados.

## Execução

```bash
docker compose -f docker-compose.yml -f docker-compose.b1.yml up --build -d
```

Para implantar imagens construídas pelo Jenkins, o próprio pipeline passa `B1_IMAGE_TAG=$BUILD_NUMBER` e acrescenta `-f ops/compose.release.yml --no-build`. Para voltar a uma imagem anterior, selecione seu número em `B1_IMAGE_TAG` e execute o mesmo Compose; preserve os volumes e verifique compatibilidade do schema antes de reverter código.

As portas publicadas ficam no localhost. Grafana: `admin / lab-b1-grafana`. Broker: dispositivo `lab01 / lab-device`; gateway `gateway / lab-gateway`. O token HTTP local é `lab-b1-local`. Os valores podem ser substituídos por variáveis do ambiente antes de implantar fora do laboratório.

Dados do Core, broker, notificações, gateway, Prometheus e Grafana têm volumes separados. A execução nativa equivalente guarda esses dados em `.runtime/run-*`; o comando de parada é `.venv/bin/python scripts/lab.py stop`.
