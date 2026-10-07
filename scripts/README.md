# Comandos do laboratório

Execute os comandos na raiz do repositório. Comece pelo [guia de entrega](../docs/ENTREGA-B1.md) para conhecer os artefatos.

| Arquivo | Finalidade |
| --- | --- |
| `bootstrap-lab.sh` | Instala ferramentas isoladas em `/tmp/b1-tools`, prepara `.venv` e compila frontend/Core. Ubuntu 24.04, Java 21, Maven, Node e acesso à rede. |
| `lab.py` | Inicia componentes reais, executa interrupções/recuperações e registra evidências; `stop` encerra somente processos registrados pelo laboratório. |
| `verify.sh` | Executa testes Java/Python, cobertura, build Vue e comparação de dependências. |
| `collect-test-evidence.py` | Copia relatórios de testes/cobertura já executados para o desafio 2 e verifica os resultados. |
| `architecture-evidence.py` | Compara o caso de uso AC1 preservado com o módulo atual e verifica o domínio puro. |
| `validate-delivery.py` | Confere seis ADRs, 18 ASRs, links e configurações. |
| `bootstrap-jenkins.sh` | Prepara Jenkins local e plugins com versões fixadas; inicia a esteira. |
| `jenkins-lab.py` | Gerencia o Jenkins do laboratório e coleta o build específico e seu commit. |
| `screenshots.cjs` | Captura aplicação, Jenkins, Prometheus e Grafana em execução usando Playwright opcional. |
| `smoke.py` | Verifica saúde, ranking e métricas depois de uma implantação. |

## Reproduzir as evidências

```bash
bash scripts/bootstrap-lab.sh
.venv/bin/python scripts/lab.py evidence
bash scripts/verify.sh
.venv/bin/python scripts/collect-test-evidence.py
.venv/bin/python scripts/validate-delivery.py
```

Para iniciar sem executar falhas, use `.venv/bin/python scripts/lab.py start`. Uma execução ativa pode receber `evidence`. Os dados e PIDs ficam em `.runtime/`, fora do Git e do pacote de entrega.

```bash
bash scripts/bootstrap-jenkins.sh
# Para nova execução com o Jenkins já ativo:
.venv/bin/python scripts/jenkins-lab.py build
```

O Jenkins usa a branch atual e exige que as alterações de código estejam commitadas para validar a revisão esperada. As credenciais locais são `b1 / lab-b1-jenkins`, na porta 8090. Os stages de imagens e deploy exigem Docker funcional.

Playwright pode ser instalado fora do produto: `npm install --prefix /tmp/b1-tools/browser playwright@1.51.1` e `/tmp/b1-tools/browser/node_modules/.bin/playwright install chromium`. Depois:

```bash
B1_PLAYWRIGHT_MODULE=/tmp/b1-tools/browser/node_modules/playwright node scripts/screenshots.cjs
```

## Encerrar

```bash
.venv/bin/python scripts/lab.py stop
```

O Jenkins é um processo separado, identificado por `.runtime/jenkins-pid`. Ferramentas em `/tmp/b1-tools` são cache local; os arquivos necessários para avaliar o exercício estão no repositório.
