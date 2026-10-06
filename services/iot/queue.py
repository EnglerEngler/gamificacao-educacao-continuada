import sqlite3
from pathlib import Path


class Queue:
    """ACK ao MQTT somente após commit local; reenvio HTTP usa o mesmo eventoId."""

    def __init__(self, path: str):
        self.path = path
        Path(path).parent.mkdir(parents=True, exist_ok=True)
        with self.connect() as db:
            db.execute("PRAGMA journal_mode=WAL")
            db.execute("""CREATE TABLE IF NOT EXISTS eventos (
                instituicao TEXT, evento_id TEXT, payload TEXT NOT NULL,
                status TEXT NOT NULL DEFAULT 'pendente', tentativas INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(instituicao,evento_id))""")
            db.execute("""CREATE TABLE IF NOT EXISTS rejeitados (
                id INTEGER PRIMARY KEY, topico TEXT, payload TEXT, motivo TEXT)""")

    def connect(self):
        return sqlite3.connect(self.path, timeout=10)

    def inserir(self, instituicao: str, evento_id: str, payload: str):
        with self.connect() as db:
            db.execute("INSERT OR IGNORE INTO eventos(instituicao,evento_id,payload) VALUES(?,?,?)",
                       (instituicao, evento_id, payload))

    def rejeitar(self, topico: str, payload: str, motivo: str):
        with self.connect() as db:
            db.execute("INSERT INTO rejeitados(topico,payload,motivo) VALUES(?,?,?)", (topico, payload, motivo))

    def pendentes(self):
        with self.connect() as db:
            return db.execute("SELECT instituicao,evento_id,payload FROM eventos WHERE status='pendente' LIMIT 20").fetchall()

    def atualizar(self, instituicao, evento_id, status):
        with self.connect() as db:
            db.execute("UPDATE eventos SET status=?,tentativas=tentativas+1 WHERE instituicao=? AND evento_id=?",
                       (status, instituicao, evento_id))

