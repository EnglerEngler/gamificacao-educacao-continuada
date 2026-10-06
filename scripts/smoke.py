"""Bloqueia o sucesso do deploy quando o Core ou contrato básico não responde."""
import httpx
import time
for _ in range(60):
    try:
        response = httpx.get("http://127.0.0.1:8080/actuator/health", timeout=2)
        if response.is_success and response.json()["status"] == "UP":
            break
    except httpx.RequestError:
        pass
    time.sleep(1)
else:
    raise SystemExit("Core não ficou saudável após deploy")
assert httpx.get("http://127.0.0.1:8080/api/ranking", timeout=3).is_success
assert "b1_outbox_pending" in httpx.get("http://127.0.0.1:8080/actuator/prometheus").text
print("Smoke de deploy aprovado: saúde, ranking e métricas")

