# Desafio 5 — MQTT, sessão persistente, spool e idempotência

Decisão: [ADR-005-iot-mqtt.md](../../docs/adr/ADR-005-iot-mqtt.md).

Execute `scripts/lab.py evidence`. Mosquitto real mantém sessão QoS 1; publicação dupla com gateway offline é recebida após reconexão, sem dupla recompensa. Core offline faz gateway acumular SQLite, e a retomada entrega HTTP com token/tenant. `python -m services.iot.simulator --aluno-id ID --repeticoes 2` publica manualmente. Presença/início usam `--tipo PRESENCA`/`INICIO` e não alteram recompensa.

Para executar o laboratório nativo:

```bash
bash scripts/bootstrap-lab.sh
.venv/bin/python scripts/lab.py evidence
```

Execute na raiz do repositório. [Evidência](../../docs/evidencias/b1/desafio5/mqtt-recuperacao.json) e [matriz cumulativa de ASRs](../../docs/arquitetura/README.md).

Hipótese, alternativas, consequências e limites estão no ADR vinculado. Cada lab utiliza o mesmo produto cumulativo; não são seis arquiteturas independentes.

