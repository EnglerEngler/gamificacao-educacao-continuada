"""Executa serviços reais e falhas controladas, sem Docker e sem usar serviços externos."""
import argparse
import json
import os
from pathlib import Path
import shutil
import signal
import sqlite3
import statistics
import subprocess
import sys
import time
from datetime import datetime, timezone
from uuid import uuid4

import httpx

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT))
RUNTIME = ROOT / ".runtime"
STATE_FILE = RUNTIME / "lab-state.json"
EVIDENCE = ROOT / "docs/evidencias/b1"
TOOLS = Path(os.getenv("B1_TOOLS_DIR", "/tmp/b1-tools"))
PYTHON = ROOT / ".venv/bin/python"
CORE = "http://127.0.0.1:8080"
TOKEN = "lab-b1-local"


def wait_for(predicate, description, seconds=60):
    limit = time.monotonic() + seconds
    while time.monotonic() < limit:
        try:
            if predicate():
                return
        except (httpx.HTTPError, OSError, KeyError, ValueError):
            pass
        time.sleep(0.5)
    raise RuntimeError("Tempo esgotado: " + description)


def ready(url):
    return httpx.get(url, timeout=2).is_success


def save_state(state):
    STATE_FILE.write_text(json.dumps(state, indent=2))


def spawn(state, name, cmd, extra=None):
    previous = state.get("processes", {}).get(name)
    if previous and alive(previous["pid"]):
        raise RuntimeError(name + " já está em execução")
    env = os.environ.copy()
    env.update({
        "B1_DATA_DIR": state["data"], "NOTIFICACOES_DB": state["data"] + "/notificacoes.db",
        "IOT_DB": state["data"] + "/iot.db", "INTEGRATION_TOKEN": TOKEN,
        "PROMETHEUS_URL": "http://127.0.0.1:9090",
        "B1_DASHBOARDS_PATH": str(ROOT / "ops/grafana/dashboards"),
        "GF_PATHS_PROVISIONING": str(ROOT / "ops/grafana/provisioning"),
        "GF_PATHS_DATA": state["data"] + "/grafana",
        "GF_PATHS_LOGS": state["data"] + "/grafana-logs",
        "GF_PATHS_PLUGINS": state["data"] + "/grafana-plugins",
        "GF_SECURITY_ADMIN_USER": "admin", "GF_SECURITY_ADMIN_PASSWORD": "lab-b1-grafana",
        "GF_SERVER_HTTP_ADDR": "127.0.0.1",
        "GF_PLUGINS_PREINSTALL_DISABLED": "true",
        "LD_LIBRARY_PATH": str(TOOLS / "root/usr/lib/x86_64-linux-gnu"),
    })
    env.update(extra or {})
    with (RUNTIME / (name + ".log")).open("a") as log:
        proc = subprocess.Popen(cmd, cwd=ROOT, env=env, stdout=log, stderr=subprocess.STDOUT, start_new_session=True)
    state.setdefault("processes", {})[name] = {"pid": proc.pid, "cmd": list(map(str, cmd))}
    save_state(state)


def alive(pid):
    try:
        status = Path(f"/proc/{pid}/stat").read_text()
        return status.split(") ", 1)[1][0] != "Z"
    except OSError:
        return False


def stop_one(state, name):
    record = state.get("processes", {}).get(name)
    if not record:
        return
    if alive(record["pid"]):
        actual = Path(f"/proc/{record['pid']}/cmdline").read_bytes().replace(b"\0", b" ").decode()
        if not all(str(part) in actual for part in record["cmd"][:2]):
            raise RuntimeError("PID não pertence ao processo registrado: " + name)
        os.killpg(record["pid"], signal.SIGTERM)
        for _ in range(40):
            if not alive(record["pid"]):
                break
            time.sleep(0.1)
        if alive(record["pid"]):
            os.killpg(record["pid"], signal.SIGKILL)
    state["processes"].pop(name, None)
    save_state(state)


def start_ia(state):
    spawn(state, "ia", [str(PYTHON), "-m", "uvicorn", "services.ia.main:app", "--host", "127.0.0.1", "--port", "8001"])
    wait_for(lambda: ready("http://127.0.0.1:8001/health"), "IA")


def start_notificacoes(state):
    spawn(state, "notificacoes", [str(PYTHON), "-m", "uvicorn", "services.notificacoes.main:app", "--host", "127.0.0.1", "--port", "8002"])
    wait_for(lambda: ready("http://127.0.0.1:8002/health"), "notificações")


def start_core(state):
    spawn(state, "core", ["java", "-Xms64m", "-Xmx256m", "-jar", str(ROOT / "target/gamificacao-educacao-continuada-1.0.0.jar"),
                         "--spring.profiles.active=lab", "--server.address=127.0.0.1"])
    wait_for(lambda: ready(CORE + "/actuator/health"), "Core", 120)


def start_gateway(state):
    log_path = RUNTIME / "gateway.log"
    previous_size = log_path.stat().st_size if log_path.exists() else 0
    spawn(state, "gateway", [str(PYTHON), "-m", "services.iot.gateway"])
    wait_for(lambda: ready("http://127.0.0.1:8003/metrics"), "gateway")
    def connected_now():
        with log_path.open("rb") as log:
            log.seek(previous_size)
            return b"mqtt_conectado" in log.read()
    wait_for(connected_now, "sessão MQTT desta execução")


def start():
    RUNTIME.mkdir(exist_ok=True)
    if STATE_FILE.exists():
        old = json.loads(STATE_FILE.read_text())
        if any(alive(p["pid"]) for p in old.get("processes", {}).values()):
            raise RuntimeError("Laboratório já ativo. Use evidence ou stop.")
    # Preserva os logs anteriores e inicia um conjunto exclusivo para o novo cenário.
    previous_logs = [RUNTIME / (name + ".log") for name in
                     ("ia", "notificacoes", "core", "gateway", "mqtt", "grafana", "prometheus")]
    archive = RUNTIME / "logs-anteriores" / datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%S%fZ")
    for log in previous_logs:
        if log.exists():
            archive.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(log, archive / log.name)
            log.write_text("")
    data = RUNTIME / ("run-" + datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%SZ"))
    data.mkdir()
    state = {"data": str(data), "processes": {}, "inicio_utc": datetime.now(timezone.utc).isoformat()}
    save_state(state)
    passwd = TOOLS / "root/usr/bin/mosquitto_passwd"
    env = dict(os.environ, LD_LIBRARY_PATH=str(TOOLS / "root/usr/lib/x86_64-linux-gnu"))
    subprocess.run([str(passwd), "-b", "-c", str(data / "mqtt-password"), "lab01", "lab-device"], env=env, check=True)
    subprocess.run([str(passwd), "-b", str(data / "mqtt-password"), "gateway", "lab-gateway"], env=env, check=True)
    (data / "mqtt-data").mkdir()
    config = (ROOT / "ops/mqtt/mosquitto.conf").read_text().replace(
        "/tmp/mqtt/password", str(data / "mqtt-password")).replace(
        "/mosquitto/config/acl", str(ROOT / "ops/mqtt/acl")).replace(
        "/mosquitto/data/", str(data / "mqtt-data") + "/").replace(
        "listener 1883 0.0.0.0", "listener 1883 127.0.0.1")
    (data / "mosquitto.conf").write_text(config)
    prom = (ROOT / "ops/prometheus/prometheus.yml").read_text().replace(
        "app:8080", "127.0.0.1:8080").replace("iot-gateway:8003", "127.0.0.1:8003").replace(
        "/etc/prometheus/alerts.yml", str(ROOT / "ops/prometheus/alerts.yml"))
    (data / "prometheus.yml").write_text(prom)
    (data / "grafana.ini").write_text("[server]\nhttp_addr = 127.0.0.1\nhttp_port = 3000\n[analytics]\nreporting_enabled = false\ncheck_for_updates = false\n")
    try:
        start_ia(state)
        start_notificacoes(state)
        spawn(state, "mqtt", [str(TOOLS / "root/usr/sbin/mosquitto"), "-c", str(data / "mosquitto.conf")])
        time.sleep(1)
        start_core(state)
        start_gateway(state)
        spawn(state, "prometheus", [str(TOOLS / "prometheus-3.2.1.linux-amd64/prometheus"),
              "--config.file=" + str(data / "prometheus.yml"), "--storage.tsdb.path=" + str(data / "prometheus"),
              "--web.listen-address=127.0.0.1:9090"])
        spawn(state, "grafana", [str(TOOLS / "grafana-v11.5.2/bin/grafana"), "server",
              "--homepath=" + str(TOOLS / "grafana-v11.5.2"), "--config=" + str(data / "grafana.ini")])
        wait_for(lambda: ready("http://127.0.0.1:9090/-/ready"), "Prometheus")
        wait_for(lambda: ready("http://127.0.0.1:3000/api/health"), "Grafana", 180)
    except Exception:
        for name in list(state["processes"]):
            stop_one(state, name)
        raise
    print("Laboratório ativo: Core :8080, IA :8001, notificações :8002, MQTT :1883, Prometheus :9090, Grafana :3000")
    return state


def request(method, path, payload=None, tenant="ac1"):
    response = httpx.request(method, CORE + path, json=payload, headers={"X-Instituicao": tenant}, timeout=5)
    response.raise_for_status()
    return response.json()


def write(name, value):
    path = EVIDENCE / name
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n")


def query(expr):
    body = httpx.get("http://127.0.0.1:9090/api/v1/query", params={"query": expr}).json()
    assert body["status"] == "success"
    return body["data"]["result"]


def has_notification(event_id):
    response = httpx.get("http://127.0.0.1:8002/eventos", headers={"X-Integration-Token": TOKEN}).json()
    return any(e["eventoId"] == event_id for e in response)


def evidence(state):
    from services.iot.simulator import publicar
    subprocess.run([str(PYTHON), "scripts/architecture-evidence.py"], cwd=ROOT, check=True)
    aluno = request("POST", "/api/alunos", {"nome": "Evidência B1", "cursosDisponiveis": 5})
    base = aluno["id"]
    started = datetime.now(timezone.utc).isoformat()
    healthy = request("GET", "/api/ia/recomendacoes?interesse=mqtt")
    rag = request("POST", "/api/ia/assistente", {"pergunta": "média recompensa"})
    stop_one(state, "ia")
    t = time.perf_counter()
    fallback = request("GET", "/api/ia/recomendacoes?interesse=mqtt")
    elapsed = (time.perf_counter() - t) * 1000
    assistente_fallback = request("POST", "/api/ia/assistente", {"pergunta": "média recompensa"})
    assert healthy["degradado"] is False and fallback["degradado"] is True
    assert assistente_fallback["degradado"] is True
    assert request("GET", "/api/ranking")
    write("desafio3/falha-ia.json", {"inicio_utc": started, "servico_disponivel": healthy, "retrieval": rag,
          "servico_interrompido": fallback, "assistente_interrompido": assistente_fallback,
          "latencia_fallback_ms": round(elapsed, 2), "ranking_continua_operando": True})
    start_ia(state)

    stop_one(state, "notificacoes")
    sync = httpx.post(CORE + f"/api/lab/alunos/{base}/conclusao-sincrona",
                     json={"media": 8, "concluido": True, "cursoId": "sync-falha"}, timeout=5)
    assert sync.status_code == 503
    event_id = str(uuid4())
    payload = {"media": 8, "concluido": True, "cursoId": "curso-outbox", "eventoId": event_id}
    t = time.perf_counter()
    asynchronous = request("POST", f"/api/alunos/{base}/cursos/conclusao", payload)
    elapsed = (time.perf_counter() - t) * 1000
    duplicate = request("POST", f"/api/alunos/{base}/cursos/conclusao", payload)
    assert asynchronous["cursosDisponiveis"] == duplicate["cursosDisponiveis"] == 8
    wait_for(lambda: bool(query('b1_outbox_deliveries_total{resultado="falha"}')), "falha observada na outbox")
    # Reinicia o Core ainda com consumidor parado, provando durabilidade e retomada.
    stop_one(state, "core")
    start_core(state)
    retained = request("GET", "/api/alunos")
    assert next(a for a in retained if a["id"] == base)["cursosDisponiveis"] == 8
    start_notificacoes(state)
    wait_for(lambda: has_notification(event_id), "recuperação da notificação", 90)
    write("desafio4/falha-recuperacao.json", {"inicio_utc": started, "http_sincrono": sync.status_code,
          "resposta_assincrona": asynchronous, "reenvio": duplicate, "latencia_core_ms": round(elapsed, 2),
          "core_reiniciado_sem_perder_recompensa": True, "evento_entregue_apos_recuperacao": event_id})

    mqtt_event = {"eventoId": str(uuid4()), "instituicao": "ac1", "dispositivoId": "lab01",
                  "tipo": "CONCLUSAO", "alunoId": base, "cursoId": "curso-mqtt", "media": 8}
    # Consumidor MQTT offline: broker retém QoS 1 para a sessão persistente.
    stop_one(state, "gateway")
    publicar(mqtt_event, repeticoes=2)
    start_gateway(state)
    wait_for(lambda: next(a for a in request("GET", "/api/alunos") if a["id"] == base)["cursosDisponiveis"] == 11,
             "MQTT offline e idempotência")
    # Core offline: spool do gateway confirma MQTT após persistir e reenvia HTTP.
    stop_one(state, "core")
    spool_event = {**mqtt_event, "eventoId": str(uuid4()), "cursoId": "curso-spool"}
    publicar(spool_event)
    def in_spool():
        with sqlite3.connect(state["data"] + "/iot.db") as db:
            return db.execute("SELECT count(*) FROM eventos WHERE evento_id=? AND status='pendente'",
                              (spool_event["eventoId"],)).fetchone()[0] == 1
    wait_for(in_spool, "evento persistido no gateway")
    start_core(state)
    wait_for(lambda: next(a for a in request("GET", "/api/alunos") if a["id"] == base)["cursosDisponiveis"] == 14,
             "spool entregue após Core recuperar")
    # Eventos de presença e início são registrados sem conceder recompensa.
    for kind in ("PRESENCA", "INICIO"):
        publicar({**mqtt_event, "eventoId": str(uuid4()), "tipo": kind, "cursoId": "curso-observacao"})
    time.sleep(2)
    final = next(a for a in request("GET", "/api/alunos") if a["id"] == base)
    assert final["cursosDisponiveis"] == 14 and final["cursosConcluidos"] == 3
    with sqlite3.connect(state["data"] + "/iot.db") as db:
        spool_rows = db.execute("SELECT evento_id,status,tentativas FROM eventos").fetchall()
    write("desafio5/mqtt-recuperacao.json", {"qos": 1, "sessao_persistente": True,
          "evento_publicado_com_gateway_offline": mqtt_event,
          "publicacoes_duplicadas": 2, "evento_publicado_com_core_offline": spool_event,
          "spool": spool_rows, "aluno_apos_recuperacao": final,
          "presenca_inicio_sem_recompensa": True})

    # Falha adicional após os reinícios para preservar evidência nos counters atuais.
    stop_one(state, "ia")
    request("GET", "/api/ia/recomendacoes")
    request("POST", "/api/ia/assistente", {"pergunta": "média recompensa"})
    start_ia(state)
    stop_one(state, "notificacoes")
    last_event = str(uuid4())
    request("POST", f"/api/alunos/{base}/cursos/conclusao",
            {"media": 7, "concluido": True, "cursoId": "metrica-falha", "eventoId": last_event})
    wait_for(lambda: bool(query('b1_outbox_deliveries_total{resultado="falha"}')), "métrica de falha")
    pending = query("b1_outbox_pending")
    start_notificacoes(state)
    wait_for(lambda: has_notification(last_event), "recuperação final")
    # Cenário 100 mil registros em H2. Não equivale a 100 mil acessos simultâneos.
    h2jar = Path.home() / ".m2/repository/com/h2database/h2/2.3.232/h2-2.3.232.jar"
    carga_tenant = 'carga-' + uuid4().hex[:8]
    sql = f"""INSERT INTO alunos(instituicao,nome,cursos_disponiveis,cursos_concluidos,cursos_aprovados,plano,moedas,versao)
        SELECT '{carga_tenant}', CONCAT('Aluno ', X), 5, MOD(X,30), MOD(X,30),
        CASE WHEN MOD(X,30)>=12 THEN 'PREMIUM' ELSE 'BASICO' END,
        CASE WHEN MOD(X,30)>=12 THEN 3 ELSE 0 END, 0 FROM SYSTEM_RANGE(1,100000)"""
    subprocess.run(["java", "-Xmx128m", "-cp", str(h2jar), "org.h2.tools.Shell", "-url",
                    "jdbc:h2:file:" + state["data"] + "/core;MODE=PostgreSQL;AUTO_SERVER=TRUE",
                    "-user", "sa", "-password", "", "-sql", sql], check=True, capture_output=True)
    samples = []
    for _ in range(30):
        t = time.perf_counter()
        ranking = request("GET", "/api/ranking?limite=20", tenant=carga_tenant)
        samples.append((time.perf_counter() - t) * 1000)
        assert len(ranking) == 20
        assert all(ranking[i]["pontos"] >= ranking[i+1]["pontos"] for i in range(19))
    write("desafio1/ranking-carga.json", {"banco": "H2 2.3.232, modo PostgreSQL",
          "registros_semeados": 100000, "instituicao_carga": carga_tenant, "requisicoes_sequenciais": len(samples),
          "tamanho_resposta": 20, "p50_ms": round(statistics.median(samples), 2),
          "p95_ms": round(sorted(samples)[int(len(samples)*0.95)-1], 2),
          "amostras_ms": [round(v,2) for v in samples],
          "limite": "Mede tamanho de dados e resposta limitada no lab; não comprova 100 mil usuários concorrentes nem capacidade de produção PostgreSQL."})
    time.sleep(5)
    metrics = {expr: query(expr) for expr in [
        'up{job="core"}', 'b1_ia_requests_total{resultado="fallback"}',
        'b1_outbox_deliveries_total{resultado="falha"}',
        'b1_outbox_deliveries_total{resultado="sucesso"}', "b1_outbox_pending",
        "b1_gateway_deliveries_total", "b1_gateway_pending", "b1_ranking_latency_seconds_count"]}
    assert metrics['up{job="core"}'][0]["value"][1] == "1"
    assert metrics['b1_ia_requests_total{resultado="fallback"}']
    dashboard = httpx.get("http://127.0.0.1:3000/api/dashboards/uid/b1-architecture",
                         auth=("admin", "lab-b1-grafana")).json()
    assert dashboard["dashboard"]["uid"] == "b1-architecture"
    write("desafio6/monitoramento/prometheus-grafana.json", {"inicio_utc": started, "coletado_utc": datetime.now(timezone.utc).isoformat(),
          "queries": metrics, "pendentes_durante_falha": pending,
          "dashboard_uid": dashboard["dashboard"]["uid"], "painels": len(dashboard["dashboard"]["panels"]),
          "ambiente": "Processos nativos no WSL; configuração equivalente de containers versionada"})
    (EVIDENCE / "desafio6/monitoramento/core-prometheus.txt").write_text(httpx.get(CORE + "/actuator/prometheus").text)
    (EVIDENCE / "desafio6/monitoramento/gateway-prometheus.txt").write_text(httpx.get("http://127.0.0.1:8003/metrics").text)
    for name in state["processes"]:
        log_dir = EVIDENCE / "desafio6/logs"
        log_dir.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(RUNTIME / (name + ".log"), log_dir / (name + ".log"))
    print("Evidências reais gravadas em docs/evidencias/b1")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("action", choices=["start", "stop", "evidence"])
    args = parser.parse_args()
    if args.action == "start":
        start()
    elif args.action == "stop":
        if STATE_FILE.exists():
            state = json.loads(STATE_FILE.read_text())
            for name in list(state["processes"]):
                stop_one(state, name)
    else:
        state = json.loads(STATE_FILE.read_text()) if STATE_FILE.exists() else None
        if state is None or not alive(state.get("processes", {}).get("core", {}).get("pid", 0)):
            state = start()
        evidence(state)


if __name__ == "__main__":
    main()
