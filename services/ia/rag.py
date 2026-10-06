"""Retrieval + resposta extrativa local. LLM externo é uma evolução documentada para AF."""
DOCUMENTOS = [
    {"id": "ac1-us01", "texto": "Ao concluir um curso com média maior que 7,0, o aluno recebe três novos cursos. Média igual a 7,0 não recebe recompensa."},
    {"id": "b1-conquistas", "texto": "A primeira conclusão aprovada concede o badge primeira-conclusao. Ao atingir 12 conclusões aprovadas, o plano torna-se Premium e são concedidas três moedas uma única vez."},
    {"id": "b1-iot", "texto": "Eventos IoT usam MQTT QoS 1, sessão persistente e identificadores estáveis para impedir recompensas duplicadas."},
]


def responder(pergunta: str) -> dict:
    import re
    palavras = set(re.findall(r"\w+", pergunta.lower()))
    documentos = sorted(DOCUMENTOS, key=lambda d: -len(palavras & set(re.findall(r"\w+", d["texto"].lower()))))
    encontrados = [d for d in documentos if palavras & set(re.findall(r"\w+", d["texto"].lower()))][:2]
    return {
        "resposta": " ".join(d["texto"] for d in encontrados) if encontrados else "Não encontrei essa informação no material disponível.",
        "fontes": [d["id"] for d in encontrados],
        "modo": "retrieval-extrativo-lab",
    }

