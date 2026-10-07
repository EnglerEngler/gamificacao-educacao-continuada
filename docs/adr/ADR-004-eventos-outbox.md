# ADR-004 — Consistência no Core e notificações eventuais por outbox

Status: aceito para B1. Desafio 4. RF02/RF05. ASR-10/11/12.

## Contexto

A resposta imediata precisa informar conclusão/recompensa gravadas. Notificação e analytics podem ser eventuais. RNFs analisados: falha, disponibilidade, latência, consistência, desacoplamento e recuperabilidade. Selecionados: tolerância a falhas, consistência e recuperabilidade.

RPC: Core conclui mesmo com consumidor externo offline; broker exige custo operacional.

## Alternativas

| Alternativa | Benefício | Trade-off |
| --- | --- | --- |
| REST síncrono na conclusão | Fluxo simples e resultado externo imediato | Falha externa afeta latência/sucesso da conclusão; rollback externo não é transação de banco. |
| Async apenas em memória | Pouco código e resposta rápida | Perde trabalho no crash; executor não prova recuperação. |
| Outbox relacional + worker HTTP | Intenção durável junto com negócio; sem segundo broker | Polling/retentativas, controle de duplicatas, throughput/fan-out limitados. |
| Outbox + RabbitMQ | Filas/ack/routing próprios para trabalho assíncrono | Ainda precisa outbox/idempotência; broker adiciona operação. |
| Outbox + Kafka | Log/replay, retenção e múltiplos consumidores de analytics | Partições, consumer groups, storage e operação maiores que o fluxo demonstrado. |

## Decisão

Outbox + worker HTTP. A **comunicação lógica é assíncrona** porque uma requisição da UI não espera a notificação; o transporte posterior worker→consumidor usa HTTP síncrono. A transação do Core atualiza aluno, grava conclusão única e grava outbox. Somente após commit há intenção visível para o worker.

Worker processa lotes pequenos, usa timeout e retentativa com backoff exponencial limitado a 60 s. Falhas permanecem pendentes sem descarte automático. Consumidor deduplica instituição/eventoId em SQLite e registra o efeito do laboratório; nenhum e-mail real é enviado.

RabbitMQ seria candidato para filas/routing/fan-out de trabalho; Kafka para replay/analytics/alto throughput. Nenhum deles é obrigatório pelo caso observado. MQTT do D5 resolve outra fronteira e não torna Kafka/RabbitMQ automaticamente necessários.

## Consequências e garantias

Consistência imediata para negócio e intenção; consistência eventual para notificação. Entrega **pelo menos uma vez**, não exactly-once: crash após consumidor confirmar e antes de marcar outbox entregue causa reenvio, tratado pela deduplicação.

HTTP ocorre enquanto o worker mantém locks de outbox; aceitável para o pequeno lote/timeout do lab, mas ocupa transação/conexão e não deve ser usado como prova de throughput. Para muitos workers, projetar claim/lease e envio fora da transação, particionamento/skip locked, política de dead letter e retenção.

`/api/lab/.../conclusao-sincrona` é controle experimental habilitado somente por propriedade de lab, não fluxo recomendado do produto.

## Hipótese e evidência

Com consumidor desligado, controle síncrono responde 503 e a API real responde 200/recompensa gravada. Core é reiniciado durante a falha; evento e recompensa sobrevivem, e consumidor recebe após recuperar. [Resultado](../evidencias/b1/desafio4/falha-recuperacao.json).

Teste de integração confirma efeito de consumidor offline; constraints e teste concorrente verificam uma recompensa por curso. O identificador precisa ser estável; operação legada sem identidade não ganha deduplicação de curso por mágica.

## C4

[Container D4](../arquitetura/diagramas/d4-container.mmd) e [sequência do fluxo crítico](../arquitetura/diagramas/d4-sequencia.mmd).

