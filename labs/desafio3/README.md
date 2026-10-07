# Desafio 3 — Serviço Python e IA

[Entrega principal](../../docs/arquitetura/README.md) · [Índice dos desafios](../README.md) · [Evidências deste desafio](../../docs/evidencias/b1/desafio3/README.md)

## Cenário e hipótese

Recomendação e assistente têm runtime e ciclo de atualização próprios; sua falha não pode impedir o fluxo essencial.

**Hipótese testada:** Interromper Python produz resposta degradada rápida e mantém ranking e Core operantes.

## Cadeia de decisão exigida pelo DOCX

| Etapa | Aplicação neste desafio |
| --- | --- |
| 1. RF | RF03 Recomendação inteligente; RF04 Assistente educacional. |
| 2. RNF | Deploy independente, escalabilidade, tolerância a falhas, interoperabilidade, desempenho e manutenibilidade. |
| 3. ASR | ASR-07 Deploy independente; ASR-08 Tolerância a falhas; ASR-09 Interoperabilidade. Impactos estruturais e critérios estão na matriz cumulativa da entrega principal. |
| 4. RPC | Core Java/Spring Boot, IA em Python e fluxo essencial disponível sem IA. |
| 5. Alternativas/trade-offs | IA embutida versus módulo acoplado versus serviço Python independente. Separar ML/RAG em dois serviços só se recursos, carga ou frequência de deploy justificarem o custo. |
| 6. ADR | [ADR-003-python-ia.md](../../docs/adr/ADR-003-python-ia.md). Decisão: Um processo FastAPI com módulos internos de recomendação e retrieval, HTTP/JSON, timeout e fallback no Core. |
| 7. C4 | [d3-container](../../docs/arquitetura/diagramas/d3-container.mmd); SVGs no [índice dos diagramas](../../docs/arquitetura/diagramas/README.md). |
| 8. Evidência | [falha-ia.json](../../docs/evidencias/b1/desafio3/falha-ia.json). [Guia de leitura do resultado](../../docs/evidencias/b1/desafio3/README.md). |

## Onde está a microsolução

| Arquivo/pasta do produto | Papel no experimento |
| --- | --- |
| [FastApiAdapter.java](../../src/main/java/br/edu/unifacens/gamificacao/ia/internal/http/FastApiAdapter.java) | Integração Java/Python e timeout |
| [IaService.java](../../src/main/java/br/edu/unifacens/gamificacao/ia/application/IaService.java) | Fallback |
| [main.py](../../services/ia/main.py) | Contrato FastAPI |
| [recommender.py](../../services/ia/recommender.py) | Recomendação por tags |
| [rag.py](../../services/ia/rag.py) | Retrieval e fontes locais |
| [TimeoutTest.java](../../src/test/java/br/edu/unifacens/gamificacao/ia/TimeoutTest.java) | Servidor conectado sem resposta |

## Executar e conferir

Execute na raiz do repositório. Prepare o ambiente com `bash scripts/bootstrap-lab.sh`. O comando `lab.py evidence` executa os cenários cumulativos e encerra/reinicia somente processos registrados pelo laboratório.

```bash
.venv/bin/python scripts/lab.py evidence
```

O JSON compara Python ativo/interrompido, recomendações, assistente e latência do fallback. O recorte é uma microsolução de fronteira por tags/retrieval extrativo; não há modelo treinado ou LLM externo.

A avaliação usa o mesmo padrão: RF/RNF/ASR (0,25), RPC/alternativas (0,20), ADR (0,20), C4 (0,15) e microsolução/evidência/Git (0,20). Justificativa e consequências completas estão no ADR; os resultados brutos permanecem na pasta de evidências.
