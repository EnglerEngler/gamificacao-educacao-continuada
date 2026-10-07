# Evidências do desafio 2 — Domínio, contratos e cobertura

[Desafio e microsolução](../../../../labs/desafio2/README.md) · [Entrega principal](../../../arquitetura/README.md) · [Índice de evidências](../README.md)

## O que esta pasta comprova

Sustenta ASR-04/05/06, selecionados na matriz cumulativa. As saídas pertencem a testes/serviços executados e foram preservadas na reorganização; os horários, commits e limites estão nos registros originais.

| Arquivo/pasta | Como usar |
| --- | --- |
| [testes.json](testes.json) | Resumo dos 25 testes Java, quatro Python e contadores de cobertura. |
| [cobertura/index.html](cobertura/index.html) | Relatório JaCoCo navegável: 100% de linhas e ramificações do domínio. |
| [relatorios/](relatorios/) | XMLs Surefire, pytest e JaCoCo que sustentam o resumo. |

## Como interpretar

Confira os casos herdados por FakeAdapterContractTest e JpaAdapterContractTest, os limites de nota, Premium, concorrência e rollback. A cobertura de 100% refere-se ao domínio; não é uma afirmação sobre todo o produto.

## Reproduzir

Siga os comandos do [desafio correspondente](../../../../labs/desafio2/README.md). O [manifesto SHA-256](../manifesto-sha256.json) permite conferir a integridade dos arquivos.
