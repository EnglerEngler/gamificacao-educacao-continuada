import hmac
import os
import sqlite3
from pathlib import Path
from uuid import UUID
from decimal import Decimal
from fastapi import FastAPI, Header, HTTPException
from pydantic import BaseModel, Field

app = FastAPI(title="B1 — consumidor idempotente de notificações")


class Evento(BaseModel):
    eventoId: UUID
    instituicao: str = Field(pattern=r"^[a-z0-9][a-z0-9-]{0,63}$")
    alunoId: int = Field(gt=0)
    cursoId: str = Field(min_length=1, max_length=120)
    media: Decimal = Field(ge=0, le=10)


def conectar():
    path = Path(os.getenv("NOTIFICACOES_DB", ".runtime/notificacoes.db"))
    path.parent.mkdir(parents=True, exist_ok=True)
    connection = sqlite3.connect(path, timeout=5)
    connection.execute("PRAGMA journal_mode=WAL")
    connection.execute("""CREATE TABLE IF NOT EXISTS notificacoes (
        instituicao TEXT NOT NULL, evento_id TEXT NOT NULL, payload TEXT NOT NULL,
        recebido_em TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (instituicao, evento_id))""")
    return connection


def autorizar(token: str):
    if not hmac.compare_digest(token, os.getenv("INTEGRATION_TOKEN", "lab-b1-local")):
        raise HTTPException(401, "Token de integração inválido")


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/eventos")
def receber(evento: Evento, x_integration_token: str = Header(default="")):
    autorizar(x_integration_token)
    with conectar() as connection:
        cursor = connection.execute(
            "INSERT OR IGNORE INTO notificacoes(instituicao,evento_id,payload) VALUES (?,?,?)",
            (evento.instituicao, str(evento.eventoId), evento.model_dump_json()),
        )
        duplicado = cursor.rowcount == 0
    # Esta tabela é o efeito observável do lab, sem enviar e-mail a pessoas reais.
    return {"status": "duplicado" if duplicado else "notificado", "eventoId": str(evento.eventoId)}


@app.get("/eventos")
def listar(x_integration_token: str = Header(default="")):
    autorizar(x_integration_token)
    with conectar() as connection:
        rows = connection.execute("SELECT instituicao,evento_id FROM notificacoes ORDER BY recebido_em").fetchall()
    return [{"instituicao": r[0], "eventoId": r[1]} for r in rows]

