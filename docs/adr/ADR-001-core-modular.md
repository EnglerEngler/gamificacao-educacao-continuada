# ADR-001 — Evoluir o Core da AC1 para monólito modular

Status: aceito para B1. Desafio 1. RF01/RF02. ASR-01/02/03.

## Contexto

O cenário evolui de mil para até cem mil usuários e o ranking pode receber carga superior às demais funções. A AC1 tem um único caso de uso de recompensa organizado em camadas horizontais. Não há evidência de pico concorrente, gargalo de CPU ou necessidade de deploy exclusivo do ranking.

RNFs analisados: manutenibilidade, desempenho, escalabilidade, testabilidade e complexidade operacional. Escolhemos três: manutenibilidade (fronteiras por capacidade), desempenho (read model/limite da consulta) e complexidade operacional (evitar distribuição prematura).

RPCs: Java/Spring Boot e banco relacional existentes; reaproveitar AC1; seis desenvolvedores no cenário. Java 21 é mantido da AC1; o ZIP Java 17 é referência, não constraint global do enunciado.

## Alternativas e trade-offs

| Alternativa | Benefício | Custo / limite |
| --- | --- | --- |
| A — Camadas horizontais | Menor mudança inicial e operação simples | Capacidade espalhada; service diretamente ligado à entidade/repository de persistência. |
| B — Monólito modular | Capacidade localizada, fronteiras verificáveis, sem RPC interno | Precisa disciplina e testes de arquitetura; ainda escala/deploy do processo inteiro. |
| C — Ranking em serviço próprio | Escala/deploy independentes caso necessários | Rede, consistência do read model, monitoramento e operação adicionais sem evidência que os exija. |

## Decisão

Adotar B: módulos aluno, ranking, ia, eventos e iot, com shared apenas para suporte transversal. Um processo Spring Boot, um artefato Maven e um banco relacional para o Core. Usar APIs públicas entre capacidades e proteger os internos com ArchUnit.

O ranking é uma leitura JDBC limitada a até 100 registros e ordenada por pontuação/id. Compartilha o schema de alunos por meio de SQL explícito; portanto permanece acoplado ao schema, mesmo sem importar entidade JPA. Esse trade-off está aceito no monólito e será revisto antes de extração.

## Conferência com o exemplo orientativo

O [exemplo de solução fornecido](../fontes/Exemplo_Solucao_Desafio_1_Architecture_Lab_B1.docx) adota monólito modular e prioriza manutenibilidade, escalabilidade e complexidade operacional. Concordamos com a decisão macro, o custo de manter um único deploy e o ranking como candidato a extração futura. O próprio exemplo admite outras seleções fundamentadas.

Aqui, desempenho foi selecionado para orientar a estrutura da leitura do ranking: consulta específica e resposta limitada, sem carregar todos os alunos para calcular a classificação na API. As 30 consultas sobre 100 mil registros medem esse recorte; não demonstram gargalo de produção nem necessidade de escala independente. Escalabilidade continua analisada e será reavaliada com concorrência, throughput, PostgreSQL e frequência de deploy. A medição não é usada para justificar distribuição.

Os nomes de pacotes do exemplo também não são um contrato obrigatório. A AC1 tem conclusão, aprovação e recompensa no mesmo agregado `Aluno`; separar cursos, conquistas e recompensas em módulos artificiais fragmentaria suas invariantes e ampliaria a refatoração sem hipótese que o exigisse. Mantemos essas regras coesas no módulo `aluno`, com `ranking` separado para leitura. IA, eventos e IoT são acréscimos cumulativos dos desafios seguintes. A fronteira pública, os testes e a direção das dependências sustentam a modularização.

## Consequências

Maior coesão e redução de dependência técnica no caso de uso; nenhuma promessa de escala independente. Crescimento em número de usuários não é equivalente a simultaneidade. A leitura limitada melhora tamanho da resposta, mas ainda pode exigir sort; PostgreSQL recebe índice de expressão no script de migração.

## Hipótese e evidência

Hipótese: reorganizar a conclusão e introduzir uma porta elimina acesso direto do caso de uso à persistência, mantendo o comportamento AC1 e custo operacional de um Core.

Comparação reproduzível: `scripts/architecture-evidence.py`, baseline em `labs/desafio1/baseline`, [dependências](../evidencias/b1/desafio1/dependencias.json), testes ArchUnit e contratos.

Experimento de ranking: [100 mil registros/30 requisições sequenciais](../evidencias/b1/desafio1/ranking-carga.json). Objetivo local p95 <= 1 s, resposta de 20 itens. Resultado não representa 100 mil usuários concorrentes nem benchmark PostgreSQL.

## C4 e revisão

[Container D1](../arquitetura/diagramas/d1-container.mmd). Mudança interna ao Core; Component detalhado no D2.

Reabrir a decisão se houver necessidade comprovada de deploy exclusivo ou limite de latência/throughput persistir após consulta/índice/caching medidos. Observabilidade será acrescentada no D6.

