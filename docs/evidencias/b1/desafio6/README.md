# Evidências do desafio 6 — Pipeline e operação observável

[Desafio e microsolução](../../../../labs/desafio6/README.md) · [Entrega principal](../../../arquitetura/README.md) · [Índice de evidências](../README.md)

## O que esta pasta comprova

Sustenta ASR-16/17/18, selecionados na matriz cumulativa. As saídas pertencem a testes/serviços executados e foram preservadas na reorganização; os horários, commits e limites estão nos registros originais.

| Arquivo/pasta | Como usar |
| --- | --- |
| [jenkins/jenkins.json](jenkins/jenkins.json) | Status, build, revisão Git, 29 testes sem falhas e stages Docker desabilitados. |
| [jenkins/jenkins-console.log](jenkins/jenkins-console.log) | Saída completa do pipeline real. |
| [jenkins/jenkins-plugins.json](jenkins/jenkins-plugins.json) | Manifesto do controller utilizado. |
| [jenkins/npm-audit.json](jenkins/npm-audit.json) | Auditoria das dependências frontend após a atualização do Vite para 6.4.5 na revisão de 03/11/2026; zero vulnerabilidades reportadas. |
| [monitoramento/prometheus-grafana.json](monitoramento/prometheus-grafana.json) | Queries reais, uma pendência durante a falha, zero após recuperação e dashboard com oito painéis. |
| [monitoramento/core-prometheus.txt](monitoramento/core-prometheus.txt) | Exportação bruta de métricas do Core. |
| [monitoramento/gateway-prometheus.txt](monitoramento/gateway-prometheus.txt) | Exportação bruta de métricas do gateway. |
| [capturas/](capturas/) | Quatro capturas reais: aplicação, Jenkins, Prometheus e Grafana. |
| [logs/](logs/) | Logs dos serviços da execução nativa. |
| [e2e/interface.json](e2e/interface.json) | Fluxos completos no Chromium, incluindo Premium, isolamento entre instituições e formulário móvel. |
| [e2e/fallback.json](e2e/fallback.json) | Catálogo e assistente degradados no navegador com o processo Python parado. |
| [e2e/repositorio.json](e2e/repositorio.json) | Seis páginas da entrega acessadas por HTTP sem autenticação. |
| [e2e/](e2e/) | Relatórios e capturas desktop, móvel e de fallback da revisão E2E. |

## Como interpretar

Leia primeiro o resultado do Jenkins, depois as queries de fallback/falha/entrega/pendência e as capturas. Counters reiniciam com o processo; os números pertencem à execução registrada. A recuperação é comprovada pelo resultado combinado dos desafios 4/5 e das métricas. Dockerfiles/stages estão configurados; seu deploy não foi executado neste ambiente.

A revisão inicial de 03/11/2026 repetiu testes locais, empacotamento e auditoria frontend. A revisão E2E subsequente reinstalou as ferramentas, corrigiu o URL de download do Grafana e repetiu IA/outbox/MQTT/monitoramento, além dos fluxos da interface em Chromium. As três capturas de aplicação/Prometheus/Grafana foram renovadas; o build e a captura Jenkins permanecem da execução original. A [proveniência](../proveniencia.json) identifica os horários e escopos de cada etapa. As métricas são coletadas antes dos testes de navegador, que acrescentam alunos e eventos próprios.

## Reproduzir

Siga os comandos do [desafio correspondente](../../../../labs/desafio6/README.md). O [manifesto SHA-256](../manifesto-sha256.json) permite conferir a integridade dos arquivos.

## Capturas da execução

![Dashboard Grafana](capturas/grafana-dashboard.png)

![Consulta Prometheus](capturas/prometheus-query.png)

![Pipeline Jenkins](capturas/jenkins-build.png)

![Aplicação Aprende+](capturas/aprendemais.png)
