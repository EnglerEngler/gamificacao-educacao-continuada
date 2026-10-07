# Evidências do desafio 5 — MQTT, sessão persistente e spool

[Desafio e microsolução](../../../../labs/desafio5/README.md) · [Entrega principal](../../../arquitetura/README.md) · [Índice de evidências](../README.md)

## O que esta pasta comprova

Sustenta ASR-13/14/15, selecionados na matriz cumulativa. As saídas pertencem a testes/serviços executados e foram preservadas na reorganização; os horários, commits e limites estão nos registros originais.

| Arquivo/pasta | Como usar |
| --- | --- |
| [mqtt-recuperacao.json](mqtt-recuperacao.json) | Duas publicações com gateway offline; evento com Core offline, spool e estado recuperado. |

## Como interpretar

Confira QoS 1, sessão persistente, UUIDs, estados do spool e a resposta final com 14 cursos disponíveis e três conclusões/aprovações. Presença/início não concedem recompensa. O desafio 6 gera uma conclusão adicional com média 7 para provocar outra falha observável; por isso sua captura da aplicação mostra quatro conclusões e os mesmos 300 pontos.

## Reproduzir

Siga os comandos do [desafio correspondente](../../../../labs/desafio5/README.md). O [manifesto SHA-256](../manifesto-sha256.json) permite conferir a integridade dos arquivos.
