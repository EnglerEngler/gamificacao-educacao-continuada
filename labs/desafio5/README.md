# Desafio 5 — IoT e MQTT

[Entrega principal](../../docs/arquitetura/README.md) · [Índice dos desafios](../README.md) · [Evidências deste desafio](../../docs/evidencias/b1/desafio5/README.md)

## Cenário e hipótese

Dispositivos limitados e conectividade intermitente produzem presença, início e conclusão sem acesso direto de cada dispositivo ao Core.

**Hipótese testada:** Evento publicado com gateway offline e evento retido com Core offline são aplicados depois; replay não concede recompensa extra.

## Cadeia de decisão exigida pelo DOCX

| Etapa | Aplicação neste desafio |
| --- | --- |
| 1. RF | RF06 Receber eventos IoT e encaminhá-los à plataforma. |
| 2. RNF | Interoperabilidade, eficiência de comunicação, disponibilidade, conectividade intermitente, escalabilidade e segurança de integração. |
| 3. ASR | ASR-13 Interoperabilidade; ASR-14 Conectividade intermitente; ASR-15 Segurança de integração. Impactos estruturais e critérios estão na matriz cumulativa da entrega principal. |
| 4. RPC | Recursos limitados, conexão instável e custo operacional de protocolo/broker. |
| 5. Alternativas/trade-offs | REST direto versus MQTT com consumidor/gateway versus MQTT com mensageria interna adicional. A última alternativa exige ASR que justifique outro broker. |
| 6. ADR | [ADR-005-iot-mqtt.md](../../docs/adr/ADR-005-iot-mqtt.md). Decisão: Mosquitto com QoS 1 e sessão persistente; gateway persiste spool antes do ACK, valida origem e envia HTTP idempotente ao Core. |
| 7. C4 | [d5-container](../../docs/arquitetura/diagramas/d5-container.mmd); SVGs no [índice dos diagramas](../../docs/arquitetura/diagramas/README.md). |
| 8. Evidência | [mqtt-recuperacao.json](../../docs/evidencias/b1/desafio5/mqtt-recuperacao.json). [Guia de leitura do resultado](../../docs/evidencias/b1/desafio5/README.md). |

## Onde está a microsolução

| Arquivo/pasta do produto | Papel no experimento |
| --- | --- |
| [simulator.py](../../services/iot/simulator.py) | Dispositivo simulado |
| [gateway.py](../../services/iot/gateway.py) | Consumidor/gateway |
| [queue.py](../../services/iot/queue.py) | Spool SQLite |
| [schema.py](../../services/iot/schema.py) | Contrato e validação |
| [acl](../../ops/mqtt/acl) | ACL de tópicos |
| [IotController.java](../../src/main/java/br/edu/unifacens/gamificacao/iot/api/IotController.java) | Entrada no Core |

## Executar e conferir

Execute na raiz do repositório. Prepare o ambiente com `bash scripts/bootstrap-lab.sh`. O comando `lab.py evidence` executa os cenários cumulativos e encerra/reinicia somente processos registrados pelo laboratório.

```bash
.venv/bin/python scripts/lab.py evidence
.venv/bin/python -m services.iot.simulator --help
```

Broker real retém entrega para a sessão; spool retoma após Core recuperar; duas publicações não duplicam recompensa. Presença/início são registrados sem recompensa. TLS e escala de produção são evolução para AF.

A avaliação usa o mesmo padrão: RF/RNF/ASR (0,25), RPC/alternativas (0,20), ADR (0,20), C4 (0,15) e microsolução/evidência/Git (0,20). Justificativa e consequências completas estão no ADR; os resultados brutos permanecem na pasta de evidências.
