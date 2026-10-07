# Evidências do desafio 1 — Dependências do Core e ranking

[Desafio e microsolução](../../../../labs/desafio1/README.md) · [Entrega principal](../../../arquitetura/README.md) · [Índice de evidências](../README.md)

## O que esta pasta comprova

Sustenta ASR-01/02/03, selecionados na matriz cumulativa. As saídas pertencem a testes/serviços executados e foram preservadas na reorganização; os horários, commits e limites estão nos registros originais.

| Arquivo/pasta | Como usar |
| --- | --- |
| [dependencias.json](dependencias.json) | Imports do mesmo caso de uso antes/depois e ausência de dependências técnicas no domínio. |
| [ranking-carga.json](ranking-carga.json) | 100 mil registros H2, 30 consultas sequenciais, resposta de 20 itens, p50 21,53 ms e p95 32,70 ms. |

## Como interpretar

Abra o comparativo e confira que o caso de uso atual usa a porta AlunoStore. Depois confira população, tamanho da resposta e todas as amostras do ranking. O resultado não comprova 100 mil usuários simultâneos nem capacidade PostgreSQL em produção.

## Reproduzir

Siga os comandos do [desafio correspondente](../../../../labs/desafio1/README.md). O [manifesto SHA-256](../manifesto-sha256.json) permite conferir a integridade dos arquivos.
