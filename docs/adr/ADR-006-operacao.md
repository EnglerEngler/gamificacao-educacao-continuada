# ADR-006 — Gates integrados e observabilidade dirigida pelos ASRs

Status: aceito para B1. Desafio 6. Todos os RFs. ASR-16/17/18.

## Contexto

Core, Python, MQTT e gateway só são sustentáveis se regras/contratos puderem ser testados e falhas recuperadas e observadas. RNFs: observabilidade, deploy independente, confiabilidade, testabilidade, recuperabilidade e desempenho. Selecionados: testabilidade, observabilidade e recuperabilidade; deploy independente é retomado do D3, desempenho do D1.

RPCs: Jenkins como base da esteira; Prometheus/Grafana no laboratório; configurações/artefatos versionados.

## Alternativas

| Alternativa | Benefício | Trade-off |
| --- | --- | --- |
| Pipeline integrado | Compatibilidade dos contratos e gates em um lugar; menos administração no monorepo | Validação mais ampla; não confere autonomia de pipeline a cada componente. |
| Pipelines por componente | Menor tempo de feedback/deploy quando equipes/ciclos divergem | Seleção de mudanças e coordenação/versionamento de contrato extras. |

## Decisão

Pipeline integrado inicialmente, com stages de Core, Python, frontend, configurações e rastreabilidade. Independência de serviço não exige obrigatoriamente pipeline distinto; artefatos e comandos de execução já são separados. Reabrir para pipelines por componente quando frequências/equipes tornarem o pipeline único um gargalo.

Jenkinsfile: checkout, `mvn verify` (domínio/adapters/fronteiras/cobertura), pytest (contrato/idempotência/spool), frontend build e validação documental. Falha em gate bloqueia os stages seguintes. Imagens tagueadas pelo BUILD_NUMBER e deploy local são opcionais no agente com Docker. Deploy utiliza imagens já testadas e smoke; não recompila silenciosamente uma imagem diferente. Etapas opcionais não foram tratadas como executadas quando Docker estava indisponível.

Depois dos testes e do build Vue, a esteira incorpora `frontend/dist` ao JAR e o empacota com `-DskipTests`, sem repetir testes já aprovados para o mesmo código. Arquiva o JAR executável com a interface, fontes Python, lock de dependências e relatórios. Assim, o artefato do Core pode ser baixado e executado mesmo quando os stages Docker ficam desabilitados.

## Métrica técnica versus evidência arquitetural

| Métrica | Condição provocada / conclusão permitida |
| --- | --- |
| b1_ia_requests_total por resultado | Python desligado aumenta fallback enquanto Core responde: sustenta ASR-08. |
| b1_outbox_pending e deliveries por resultado | Consumidor offline gera pendência/falha; recuperação gera entrega: sustenta ASR-10/12. |
| b1_gateway_pending / deliveries | Core offline acumula spool que é consumido depois: sustenta ASR-14. |
| b1_ranking_latency_seconds | Consultas limitadas sobre 100 mil registros medem hipótese de leitura: apoia ASR-02 no lab. |
| up{job="core"} | Confirma scrape/disponibilidade naquele instante; sozinho não prova SLA mensal. |
| HTTP/JVM/processo | Ajuda diagnóstico; não é, por si, justificativa da arquitetura. |

Labels têm cardinalidade limitada; nomes de aluno, tenant, pergunta e UUID não são labels. Logs de eventos usam eventoId/tenant para correlação. Histogramas permitem p95, sem mascarar limites da carga de laboratório.

## Recuperação, deploy e rollback

Volumes guardam Postgres, outbox, broker, spool e consumidor, separados de imagens. Core não depende da disponibilidade de IA/notificação para iniciar. Reverter código: reimplantar tag anterior conhecida e executar smoke. Antes de mudança de schema, backup e compatibilidade reversa devem ser avaliados; rollback de imagem não desfaz migration/dados automaticamente.

O script V1 de migração documenta adoção da tabela AC1/índice do ranking. `ddl-auto=update` é configuração didática, não estratégia final de produção. Promover migrations completas e restore testado na AF.

## Evidência

Jenkins real executa o pipeline de validação; console/status/relatórios ficam em [Jenkins](../evidencias/b1/desafio6/jenkins/jenkins.json). [Prometheus/Grafana](../evidencias/b1/desafio6/monitoramento/prometheus-grafana.json), capturas e métricas exportadas registram a condição real e a recuperação. Ambiente nativo WSL com H2 persistente; container packaging/deploy permanece configuração versionada a validar com Docker, não evidência fictícia.

## C4 e revisão

[Deployment](../arquitetura/diagramas/d6-deployment.mmd), [Container final](../arquitetura/diagramas/container-final.mmd). O dashboard/alertas são provisionados pelo Git. Definir retenção/backup, registry, ambientes e SLOs da PoC antes de operação real.
