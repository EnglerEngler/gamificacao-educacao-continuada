from fastapi import FastAPI
from pydantic import BaseModel, Field
from services.ia.recommender import recomendar
from services.ia.rag import responder

app = FastAPI(title="B1 — fronteira Python de recomendação e RAG")


class Pergunta(BaseModel):
    pergunta: str = Field(min_length=1, max_length=1000)


@app.get("/health")
def health():
    return {"status": "ok"}


@app.get("/recomendacoes")
def recomendacoes(interesse: str = "programacao"):
    return {"degradado": False, "cursos": recomendar(interesse), "origem": "python-lab"}


@app.post("/assistente")
def assistente(pergunta: Pergunta):
    return {"degradado": False, "origem": "python-lab", **responder(pergunta.pergunta)}

