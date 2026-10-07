# Evidências do desafio 3 — Python disponível e interrompido

[Desafio e microsolução](../../../../labs/desafio3/README.md) · [Entrega principal](../../../arquitetura/README.md) · [Índice de evidências](../README.md)

## O que esta pasta comprova

Sustenta ASR-07/08/09, selecionados na matriz cumulativa. As saídas pertencem a testes/serviços executados e foram preservadas na reorganização; os horários, commits e limites estão nos registros originais.

| Arquivo/pasta | Como usar |
| --- | --- |
| [falha-ia.json](falha-ia.json) | Respostas com Python ativo, recomendação/assistente degradados e latência medida de fallback. |

## Como interpretar

Compare servico_disponivel com servico_interrompido e assistente_interrompido; ranking_continua_operando é verdadeiro. O teste usa FastAPI real e interrupção de processo. Recomendações por tags e retrieval local demonstram a fronteira; não demonstram qualidade de um modelo de produção.

## Reproduzir

Siga os comandos do [desafio correspondente](../../../../labs/desafio3/README.md). O [manifesto SHA-256](../manifesto-sha256.json) permite conferir a integridade dos arquivos.
