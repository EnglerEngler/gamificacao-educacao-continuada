import logging
import os
import threading
import time
import httpx
import paho.mqtt.client as mqtt
from prometheus_client import Counter, Gauge, start_http_server
from services.iot.queue import Queue
from services.iot.schema import Evento, validar_topico

LOG = logging.getLogger("b1.iot")
RECEBIDOS = Counter("b1_gateway_received", "Eventos MQTT gravados", ["resultado"])
ENTREGAS = Counter("b1_gateway_deliveries", "Resultado de entrega HTTP", ["resultado"])
PENDENTES = Gauge("b1_gateway_pending", "Eventos persistidos aguardando Core")
queue = None


def entregar(queue: Queue):
    with httpx.Client(timeout=2) as client:
        while True:
            rows = queue.pendentes()
            PENDENTES.set(len(rows))
            for instituicao, event_id, payload in rows:
                try:
                    response = client.post(os.getenv("CORE_URL", "http://127.0.0.1:8080") + "/api/iot/eventos",
                        content=payload, headers={"Content-Type": "application/json", "X-Instituicao": instituicao,
                        "X-Integration-Token": os.getenv("INTEGRATION_TOKEN", "lab-b1-local")})
                    if response.is_success:
                        queue.atualizar(instituicao, event_id, "entregue")
                        ENTREGAS.labels("sucesso").inc()
                        LOG.info("iot_entregue eventId=%s", event_id)
                    elif 400 <= response.status_code < 500 and response.status_code != 429:
                        queue.atualizar(instituicao, event_id, "rejeitado")
                        ENTREGAS.labels("rejeitado").inc()
                        LOG.error("iot_rejeitado eventId=%s http=%s", event_id, response.status_code)
                    else:
                        queue.atualizar(instituicao, event_id, "pendente")
                        ENTREGAS.labels("falha").inc()
                except httpx.RequestError:
                    queue.atualizar(instituicao, event_id, "pendente")
                    ENTREGAS.labels("falha").inc()
            time.sleep(1)


def conectado(client, userdata, flags, reason_code, properties):
    if reason_code.is_failure:
        LOG.error("mqtt_conexao_recusada code=%s", reason_code)
        return
    client.subscribe("instituicoes/+/dispositivos/+/eventos", qos=1)
    LOG.info("mqtt_conectado sessao_presente=%s", flags.session_present)


def recebido(client, userdata, message):
    assert queue is not None
    try:
        event = Evento.model_validate_json(message.payload)
        validar_topico(message.topic, event, set(os.getenv("IOT_INSTITUICOES", "ac1,instituicao-b").split(",")))
    except (ValueError, UnicodeError) as exc:
        queue.rejeitar(message.topic, message.payload.decode(errors="replace"), str(exc))
        RECEBIDOS.labels("rejeitado").inc()
    else:
        queue.inserir(event.instituicao, str(event.eventoId), event.model_dump_json())
        RECEBIDOS.labels("aceito").inc()
    # commit durável anterior ao PUBACK; falha de disco impede ACK e exige redelivery.
    client.ack(message.mid, message.qos)


def main():
    global queue
    logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
    queue = Queue(os.getenv("IOT_DB", ".runtime/iot.db"))
    start_http_server(int(os.getenv("IOT_METRICS_PORT", "8003")), addr="0.0.0.0")
    threading.Thread(target=entregar, args=(queue,), daemon=True).start()
    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2, client_id="b1-gateway",
                         clean_session=False, protocol=mqtt.MQTTv311, manual_ack=True)
    client.username_pw_set(os.getenv("MQTT_USER", "gateway"), os.getenv("MQTT_PASSWORD", "lab-gateway"))
    client.on_connect = conectado
    client.on_message = recebido
    client.reconnect_delay_set(1, 10)
    client.connect(os.getenv("MQTT_HOST", "127.0.0.1"), int(os.getenv("MQTT_PORT", "1883")), keepalive=30)
    client.loop_forever(retry_first_connection=True)


if __name__ == "__main__":
    main()

