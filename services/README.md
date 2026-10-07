# Componentes Python

Os serviços compõem as microsoluções dos desafios 3, 4 e 5. A [visão C4](../docs/arquitetura/README.md) explica suas fronteiras com o Core.

| Pasta | Responsabilidade | Porta nativa |
| --- | --- | --- |
| `ia/` | FastAPI com recomendação por tags e assistente por retrieval extrativo local; testa contrato Java/Python, timeout e fallback. | 8001 |
| `notificacoes/` | Consumidor HTTP idempotente com SQLite; registra o efeito de notificação do laboratório. | 8002 |
| `iot/` | Simulador MQTT, validação tópico/payload, spool SQLite e gateway com retentativa HTTP. | 8003 para métricas |

`Dockerfile` empacota o runtime compartilhado; cada serviço tem processo, configuração e dados próprios. `requirements.lock` na raiz fixa as dependências diretas e transitivas. Os quatro testes Python estão em [tests/test_services.py](../tests/test_services.py).

Use [scripts/lab.py](../scripts/lab.py) para orquestrar a execução nativa ou os dois arquivos Compose da raiz para a implantação prevista. O simulador pode ser executado com `.venv/bin/python -m services.iot.simulator --help`.

IA não usa modelo treinado ou LLM externo. Notificações são efeitos locais de demonstração; nenhuma mensagem é enviada a pessoas. Esses recortes e os critérios de evolução estão nos ADRs.
