# Evidências reais do B1

Os arquivos são saídas dos testes e serviços executados, não exemplos de resultados. Cada microsolução tem uma hipótese descrita no ADR.

Validação final: **25 testes Java + 4 testes Python**, sem falhas; domínio com **100% de linhas e ramificações** cobertas. A execução real do Jenkins valida os mesmos 29 testes; número do build e revisão completa estão registrados no resultado do desafio 6. Os resultados detalhados e o commit completo estão nos JSONs dos desafios 2 e 6.

| Desafio | Evidência | Resultado |
| --- | --- | --- |
| [Desafio 1](desafio1/README.md) | [Dependências antes/depois](desafio1/dependencias.json), [ranking/carga](desafio1/ranking-carga.json) | Caso de uso deixa de importar JPA; resposta limitada sobre 100 mil registros H2. |
| [Desafio 2](desafio2/README.md) | [Testes e cobertura](desafio2/testes.json), [relatório HTML](desafio2/cobertura/index.html) | Contrato fake/JPA, domínio puro, concorrência e rollback; domínio com 100% linhas/branches. |
| [Desafio 3](desafio3/README.md) | [Interrupção real da IA](desafio3/falha-ia.json) | Core retorna fallback e mantém ranking; Python recupera. |
| [Desafio 4](desafio4/README.md) | [Síncrono/assíncrono e retomada](desafio4/falha-recuperacao.json) | Síncrono 503; real 200; evento/recompensa persistem após reiniciar Core e consumidor recebe depois. |
| [Desafio 5](desafio5/README.md) | [MQTT com gateway/Core offline](desafio5/mqtt-recuperacao.json) | Sessão/spool entregam após recuperação; reenvio não duplica recompensa. |
| [Desafio 6](desafio6/README.md) | [Consultas Prometheus/Grafana](desafio6/monitoramento/prometheus-grafana.json), [Jenkins](desafio6/jenkins/jenkins.json) | Falha gera métricas e pendência; recuperação zera fila; pipeline valida código/configurações. |

## Organização das pastas

```text
desafio1/  README + comparação de dependências + carga do ranking
desafio2/  README + resumo de testes + relatorios/ + cobertura/
desafio3/  README + interrupção real da IA
desafio4/  README + comparação síncrona/assíncrona e recuperação
desafio5/  README + recuperação de MQTT/spool
desafio6/  README + jenkins/ + monitoramento/ + capturas/ + logs/
```

Cada pasta tem um README que explica **o que foi testado, qual arquivo abrir e o que o resultado permite concluir**. As [capturas e seus resultados](desafio6/README.md) ficam junto à operação do desafio 6.

[Documento de entrega](../../arquitetura/README.md) · [Índice dos desafios](../../../labs/README.md).

## Reproduzir

Na raiz: `bash scripts/bootstrap-lab.sh`, `.venv/bin/python scripts/lab.py evidence`, `bash scripts/verify.sh`, `.venv/bin/python scripts/collect-test-evidence.py`.

As evidências capturadas registram horário/ambiente. Timings variam com host, cold start e ferramentas concorrentes. H2 não é benchmark PostgreSQL. O cenário tem 100 mil **registros** e 30 requisições sequenciais, não 100 mil usuários simultâneos.

Exportações `core-prometheus.txt` e `gateway-prometheus.txt`, logs e JSON permitem conferir capturas sem depender apenas de imagem. A execução do Jenkins declara explicitamente quais stages opcionais foram executados ou pulados.

O [manifesto SHA-256](manifesto-sha256.json) identifica todos os arquivos desta pasta no fechamento da entrega, excluindo o próprio manifesto. Para conferir um arquivo, compare `sha256sum caminho/do/arquivo` com a entrada correspondente.
