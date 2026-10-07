"""Microsolução determinística: valida contrato e fronteira, sem alegar modelo treinado."""
CATALOGO = [
    {"id": "python", "titulo": "Python para educação", "tags": {"python", "programacao", "dados"}},
    {"id": "arquitetura", "titulo": "Arquitetura de software", "tags": {"arquitetura", "java", "spring"}},
    {"id": "iot", "titulo": "IoT e MQTT", "tags": {"iot", "mqtt", "dispositivos"}},
]


def recomendar(interesse: str) -> list[str]:
    tokens = set(interesse.lower().split())
    ordenados = sorted(CATALOGO, key=lambda c: (-len(c["tags"] & tokens), c["id"]))
    return [c["titulo"] for c in ordenados[:3]]

