# ADR-005 — MQTT com gateway durável e entrada idempotente

Status: aceito para B1. Desafio 5. RF06. ASR-13/14/15.

## Contexto

Dispositivos publicam presença, início e conclusão com recursos/conexão limitados. RNFs analisados: interoperabilidade, eficiência, disponibilidade, conectividade intermitente, escala e segurança. Selecionados: interoperabilidade/eficiência de comunicação (protocolo), conectividade intermitente (sessão/spool) e segurança de integração (fronteira).

RPCs: dispositivos limitados, conexão instável, protocolos/brokers aumentam componentes operacionais.

## Alternativas

| Alternativa | Benefício | Custo / limite |
| --- | --- | --- |
| A — REST dispositivo→Core | Menos componentes e protocolo conhecido | Cada dispositivo conhece API/disponibilidade; precisa implementar buffer e retry HTTP próprio. |
| B — MQTT→gateway→Core | Pub/sub, QoS e sessão persistente; contrato de dispositivo separado | Broker, credenciais/ACL e monitoramento do gateway. |
| C — MQTT→gateway→broker interno→Core | Fan-out/replay interno separado da entrada IoT | Segundo broker, dupla operação e mais pontos de recuperação sem ASR atual que o exija. |

## Decisão

B: Mosquitto, MQTT 3.1.1/QoS 1, sessão persistente `b1-gateway`, tópicos por instituição/dispositivo. Simulador publica como lab01, com ACL limitada ao tópico ac1/lab01; gateway apenas lê tópicos de eventos. Payload validado inclui eventoId, instituição, dispositivo, tipo, aluno, curso e média na conclusão.

Gateway valida tópico=payload e instituição permitida, grava SQLite durável, então envia PUBACK manual. Worker entrega ao Core por HTTP com token e tenant. Core rejeita token/tenant inválidos e grava recibo único. Conclusão usa a mesma API pública do caso de uso e a mesma outbox; presença/início são registrados sem conceder recompensas.

## Perda, duplicação e segurança

QoS 1 permite duplicatas; identificação estável e constraints eliminam dupla recompensa. Sessão MQTT cobre consumidor offline **após sua primeira conexão/subscription**. Não retém evento para assinante que nunca teve sessão. Spool cobre Core offline depois do commit local. Crash entre envio HTTP e atualização do spool causa reenvio idempotente.

Mensagens inválidas ficam em tabela de rejeitados e recebem ACK após persistência, evitando poison message infinito. Erros HTTP 4xx permanentes ficam marcados como rejeitados; 5xx/conexão mantêm pendência. Retentativa do gateway é de 1 s no lab; aplicar backoff/jitter e limite operacional na AF.

Broker/storage local falhando antes de persistir e desconexão do dispositivo antes do broker confirmar continuam exigindo retry/buffer no dispositivo. Não afirmamos ausência absoluta de perda. Não há segundo broker sem fan-out/retenção/throughput que o justifique.

Credenciais demonstrativas, ACL e token protegem o lab em localhost. Implantação fora do host exige TLS e credenciais por dispositivo; cabeçalho tenant não substitui identidade/autorização do aluno. Esses limites foram mantidos explícitos.

## Evidência e C4

Broker real recebe duas publicações do mesmo evento com gateway desligado; sessão entrega após reconexão. Outro evento chega com Core offline, fica no spool e é aplicado após reinício. Presença/início não alteram recompensa. [Resultados](../evidencias/b1/desafio5/mqtt-recuperacao.json).

Testes de schema/tópico, spool após reinício, replay, 401/403 e concorrência complementam a evidência. [Container D5](../arquitetura/diagramas/d5-container.mmd).

