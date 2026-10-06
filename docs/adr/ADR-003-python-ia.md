# ADR-003 — Serviço Python com fallback no Core

Status: aceito para B1. Desafio 3. RF03/RF04. ASR-07/08/09.

## Contexto e drivers

Recomendação e assistente têm ciclo de modelos próprio, carga variável e podem falhar sem impedir fluxo essencial. RNFs analisados: deploy independente, escala, tolerância a falhas, interoperabilidade, desempenho e manutenibilidade. Selecionados: deploy independente, tolerância a falhas e interoperabilidade.

RPCs: Core Java/Spring; IA Python; IA indisponível não pode bloquear operações essenciais. Python sozinho não basta como justificativa; autonomia de ciclo, fronteira de falha e contrato entre runtimes sustentam o serviço.

## Alternativas

| Alternativa | Benefício | Trade-off |
| --- | --- | --- |
| A — IA embutida no Core | Sem chamada remota | Runtime Python no processo/empacotamento do Core; falha/recursos compartilhados; atualização de modelo exige deploy Core. |
| B — Módulo/processo acoplado ao deploy Core | Fronteira de código mais clara | Ciclos continuam vinculados; dependência de disponibilidade se chamada não for limitada. |
| C — Serviço Python independente | Recompõe/atualiza modelo sem recompilar Java, timeout e isolamento | Contrato HTTP, rede, monitoramento e implantação extra. |

## Decisão

C, em um serviço FastAPI. Recomendador e retrieval são módulos internos separados, no mesmo container enquanto não houver evidência de perfil de recursos/ciclo operacional que justifique dois serviços.

HTTP/JSON síncrono para resposta interativa. Connect timeout de 250 ms e read timeout de 700 ms; estes são timeouts de fases, não orçamento total rígido end-to-end. Falha de conexão, timeout ou HTTP inválido resulta em resposta `degradado=true`: recomendação do catálogo local e mensagem segura do assistente. O Core não consulta IA para concluir curso nem calcular ranking.

## Microsolução e limites

Recomendação por similaridade de tags e retrieval extrativo com fontes locais. Não há chave/API de provedor externo nem alegação de modelo treinado/LLM em produção. A hipótese testada é integração e autonomia/falha da fronteira, não qualidade de resposta generativa.

[Python interrompido realmente](../evidencias/b1/desafio3/falha-ia.json): endpoints degradam, ranking continua disponível e Python retorna após reinício. Testes Java cobrem fallback em recomendador/assistente e testes Python validam contrato/fontes.

## Consequências

Um container a mais e dependência do contrato; catálogo fallback preserva disponibilidade com qualidade inferior à recomendação. Não há circuit breaker neste lab, então cada requisição durante indisponibilidade ainda tenta a chamada com timeout; considerar circuito quando medições de falha/carga mostrarem pressão no Core.

Crescimento independente é possível por implantação, mas não foi provado teste de autoscaling. Separar ML/RAG exige carga/recursos/deploy divergentes observados, não apenas o fato de serem funcionalidades diferentes.

## C4 e revisão

[Container D3](../arquitetura/diagramas/d3-container.mmd). Retomar na AF com embeddings/LLM real, autenticação do serviço, privacidade de dados e avaliação de respostas. Medir latência/erro por operação sem expor perguntas em labels de métricas.

