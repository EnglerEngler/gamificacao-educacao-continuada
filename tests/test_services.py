import json
from uuid import uuid4
import pytest
from fastapi.testclient import TestClient
from services.ia.main import app as ia
from services.notificacoes.main import app as notificacoes
from services.iot.queue import Queue
from services.iot.schema import Evento, validar_topico


def evento():
    return {"eventoId": str(uuid4()), "instituicao": "ac1", "dispositivoId": "lab01",
            "tipo": "CONCLUSAO", "alunoId": 1, "cursoId": "c1", "media": 8}


def test_recomendacao_e_rag_retornam_contrato():
    with TestClient(ia) as client:
        body = client.get("/recomendacoes?interesse=mqtt").json()
        assert body["degradado"] is False
        assert body["cursos"][0] == "IoT e MQTT"
        body = client.post("/assistente", json={"pergunta": "média recompensa"}).json()
        assert "ac1-us01" in body["fontes"]
        assert "maior que 7,0" in body["resposta"]
        assert client.post("/assistente", json={"pergunta": ""}).status_code == 422


def test_notificacao_idempotente_e_token(tmp_path, monkeypatch):
    monkeypatch.setenv("NOTIFICACOES_DB", str(tmp_path / "notificacoes.db"))
    payload = {k: v for k, v in evento().items() if k not in {"tipo", "dispositivoId"}}
    with TestClient(notificacoes) as client:
        assert client.post("/eventos", json=payload).status_code == 401
        headers = {"X-Integration-Token": "lab-b1-local"}
        assert client.post("/eventos", json=payload, headers=headers).json()["status"] == "notificado"
        assert client.post("/eventos", json=payload, headers=headers).json()["status"] == "duplicado"
        assert len(client.get("/eventos", headers=headers).json()) == 1


def test_spool_sobrevive_reinicio_e_reenvio(tmp_path):
    path = str(tmp_path / "iot.db")
    first = Queue(path)
    e = evento()
    first.inserir("ac1", e["eventoId"], json.dumps(e))
    first.inserir("ac1", e["eventoId"], json.dumps(e))
    second = Queue(path)
    assert len(second.pendentes()) == 1
    second.atualizar("ac1", e["eventoId"], "entregue")
    assert second.pendentes() == []


def test_topico_e_payload_precisam_corresponder():
    e = Evento.model_validate(evento())
    validar_topico("instituicoes/ac1/dispositivos/lab01/eventos", e, {"ac1"})
    with pytest.raises(ValueError):
        validar_topico("instituicoes/outra/dispositivos/lab01/eventos", e, {"ac1"})
    with pytest.raises(ValueError):
        validar_topico("instituicoes/ac1/dispositivos/lab01/eventos", e, {"outra"})
    with pytest.raises(ValueError):
        validar_topico("invalido", e, {"ac1"})
    with pytest.raises(ValueError):
        Evento.model_validate({**evento(), "media": None})

