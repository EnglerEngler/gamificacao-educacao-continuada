# Evidências do desafio 4 — Falha do consumidor e recuperação da outbox

[Desafio e microsolução](../../../../labs/desafio4/README.md) · [Entrega principal](../../../arquitetura/README.md) · [Índice de evidências](../README.md)

## O que esta pasta comprova

Sustenta ASR-10/11/12, selecionados na matriz cumulativa. As saídas pertencem a testes/serviços executados e foram preservadas na reorganização; os horários, commits e limites estão nos registros originais.

| Arquivo/pasta | Como usar |
| --- | --- |
| [falha-recuperacao.json](falha-recuperacao.json) | Controle síncrono 503; resposta assíncrona bem-sucedida em 53,52 ms, reenvio e retomada após reinício. |

## Como interpretar

Confira que resposta_assincrona e reenvio mantêm oito cursos disponíveis e uma conclusão/aprovação. core_reiniciado_sem_perder_recompensa é verdadeiro; o UUID entregue depois identifica a mesma intenção persistida. Logs de execução estão na pasta logs do desafio 6.

## Reproduzir

Siga os comandos do [desafio correspondente](../../../../labs/desafio4/README.md). O [manifesto SHA-256](../manifesto-sha256.json) permite conferir a integridade dos arquivos.
