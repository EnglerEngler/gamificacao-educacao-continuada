# Como entregar e apresentar o B1

## Entrega principal exigida pelo DOCX

O documento a avaliar é [arquitetura/README.md](arquitetura/README.md), mantido no caminho `/docs/arquitetura/README.md` indicado pelo item 6. Os seis desafios compõem esse mesmo registro e o mesmo produto evolutivo.

| Item 8 — entrega final do DOCX | Onde conferir |
| --- | --- |
| Link do repositório e identificação da equipe | Seção abaixo e abertura do registro principal. |
| README arquitetural com seis decisões e matriz cumulativa | [Registro principal](arquitetura/README.md). |
| ADRs essenciais e C4 final coerente | [Seis ADRs](adr/README.md), [índice C4](arquitetura/diagramas/README.md) e visão final no registro. |
| Links/pastas das microsoluções e evidências | [Desafios 1 a 6](../labs/README.md) e [evidências por desafio](evidencias/b1/README.md). |
| Síntese de decisões para a PoC da AF | Seção Síntese para a PoC da AF no registro principal. |

Este arquivo organiza envio/apresentação. Cada [desafio](../labs/README.md) liga a decisão aos seus artefatos; cada [pasta de evidência](evidencias/b1/README.md) explica o resultado que sustenta a hipótese.

## Link e equipe

Repositório: https://github.com/EnglerEngler/gamificacao-educacao-continuada  
Equipe: Eduardo Bismara Nastri (211466), Khevyn Henrique Guedes T. Alves (223761), João Victor Cardoso Engler Rizzi de Araujo (236602).

A entrega B1 está na `main`. Use o link principal do repositório para enviar:

https://github.com/EnglerEngler/gamificacao-educacao-continuada

A integração e seu histórico estão no [PR #2](https://github.com/EnglerEngler/gamificacao-educacao-continuada/pull/2).

## Artefatos a apresentar

O [índice da documentação](README.md) distingue a entrega B1 dos registros históricos da AC1. O [mapa do repositório](../README.md) localiza código, serviços, scripts e operação.

| Exigência do enunciado | Localização |
| --- | --- |
| Registro cumulativo e RF/RNF/RPC | [docs/arquitetura/README.md](arquitetura/README.md) |
| Matriz de ASRs, máximo três por desafio | Seção RNFs e seleção de ASRs no registro |
| Seis decisões / alternativas / trade-offs | [docs/adr](adr/README.md) até ADR-006 |
| C4 coerente e visão final | [diagramas](arquitetura/diagramas/README.md), fontes Mermaid e SVG |
| Microsoluções | [Índice dos seis desafios](../labs/README.md); código compartilhado src/services/ops/scripts |
| Evidências e reprodução | [índice de evidências](evidencias/b1/README.md) |
| Jenkins/Prometheus/Grafana | Jenkinsfile, ops e evidências do desafio6 |
| Síntese para AF | Seção Síntese para a PoC da AF no registro |
| Fontes originais | [docs/fontes](fontes/README.md) |

## Roteiro sugerido de demonstração (10–15 minutos)

1. Mostrar a matriz: requisitos explicam decisões, não a popularidade de ferramentas.
2. D1: comparar baseline AC1 e AlunoService atual; mostrar agrupamento por capacidade, porta e resultado do ranking com 100 mil registros.
3. D2: mostrar Aluno puro, AlunoStore, fake/JPA e mesmo contrato; explicar > 7,0 e cursoId estável.
4. D3: mostrar JSON com Python ativo e interrompido; Core usa fallback sem bloquear ranking.
5. D4: mostrar 503 do controle síncrono versus 200 real e a recuperação da outbox após reiniciar Core.
6. D5: mostrar broker/session/spool e o mesmo evento duplicado gerando uma recompensa; explicar QoS 1 e fronteira de segurança.
7. D6: mostrar execução SUCCESS do Jenkins, dashboard Grafana e consultas Prometheus; ligar cada métrica ao ASR que testa.
8. Encerrar com o C4 final e a seleção de decisões para AF.

Execute as falhas com `scripts/lab.py evidence`, que encerra somente processos do lab registrados em `.runtime/lab-state.json`. Não desligue serviços de outras atividades manualmente.

## Limites que devem ser declarados

- IA é microsolução de fronteira, recomendação por tags/retrieval extrativo; não treinamento/LLM real.
- Carga usa 100 mil registros H2 e 30 requisições sequenciais; não 100 mil usuários simultâneos nem benchmark PostgreSQL.
- Pipeline de validação foi executado no Jenkins. Stages opcionais de imagens/deploy dependem de Docker no agente e têm seu status explicitamente registrado.
- Multi-instituição foi demonstrada pelo particionamento e teste de isolamento. Login/identidade por instituição e TLS fora do localhost serão definidos para AF.
- Git não contém autoria individual fabricada; a equipe deve revisar a entrega antes da apresentação.

## Encerramento do laboratório

```bash
.venv/bin/python scripts/lab.py stop
# Jenkins local é separado; o PID está em .runtime/jenkins-pid.
```

No Compose: `docker compose -f docker-compose.yml -f docker-compose.b1.yml down`. Evite remover volumes se quiser preservar dados.
