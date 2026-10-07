# Desafios 1 a 6 — Architecture Lab B1

A **[entrega principal](../docs/arquitetura/README.md)** é o registro arquitetural único exigido pelo DOCX. Estas pastas organizam as microsoluções do mesmo produto evolutivo; cada README segue RF → RNF → ASR/RPC → alternativas → ADR → C4 → evidência.

| Desafio do DOCX | Microsolução | Decisão | Evidência |
| --- | --- | --- | --- |
| 1 — Drivers e evolução do Core | [Desafio 1](desafio1/README.md) | [ADR-001](../docs/adr/ADR-001-core-modular.md) | [Resultado D1](../docs/evidencias/b1/desafio1/README.md) |
| 2 — Organização interna do Core | [Desafio 2](desafio2/README.md) | [ADR-002](../docs/adr/ADR-002-ports-adapters.md) | [Resultado D2](../docs/evidencias/b1/desafio2/README.md) |
| 3 — Serviço Python e IA | [Desafio 3](desafio3/README.md) | [ADR-003](../docs/adr/ADR-003-python-ia.md) | [Resultado D3](../docs/evidencias/b1/desafio3/README.md) |
| 4 — Comunicação e eventos | [Desafio 4](desafio4/README.md) | [ADR-004](../docs/adr/ADR-004-eventos-outbox.md) | [Resultado D4](../docs/evidencias/b1/desafio4/README.md) |
| 5 — IoT e MQTT | [Desafio 5](desafio5/README.md) | [ADR-005](../docs/adr/ADR-005-iot-mqtt.md) | [Resultado D5](../docs/evidencias/b1/desafio5/README.md) |
| 6 — CI/CD e observabilidade | [Desafio 6](desafio6/README.md) | [ADR-006](../docs/adr/ADR-006-operacao.md) | [Resultado D6](../docs/evidencias/b1/desafio6/README.md) |

## Organização e execução

O código permanece em `src/`, `frontend/`, `services/` e `ops/`, com um único produto cumulativo. Cada desafio aponta para os arquivos que materializam sua decisão; copiar o produto seis vezes criaria versões divergentes. O desafio 1 conserva a [snapshot AC1 b31abd3](desafio1/baseline) para a comparação antes/depois.

[Documento de entrega](../docs/arquitetura/README.md) · [Todos os resultados](../docs/evidencias/b1/README.md) · [Comandos de reprodução](../scripts/README.md).
