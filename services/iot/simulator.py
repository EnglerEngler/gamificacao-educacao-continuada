import argparse
import json
import os
from uuid import uuid4
import paho.mqtt.client as mqtt


def publicar(evento, repeticoes=1):
    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2, client_id="b1-simulador-" + str(uuid4()))
    client.username_pw_set(os.getenv("MQTT_DEVICE_USER", "lab01"), os.getenv("MQTT_DEVICE_PASSWORD", "lab-device"))
    client.connect(os.getenv("MQTT_HOST", "127.0.0.1"), int(os.getenv("MQTT_PORT", "1883")), 30)
    client.loop_start()
    topic = f"instituicoes/{evento['instituicao']}/dispositivos/{evento['dispositivoId']}/eventos"
    for _ in range(repeticoes):
        result = client.publish(topic, json.dumps(evento), qos=1, retain=False)
        result.wait_for_publish(timeout=10)
        if not result.is_published():
            raise RuntimeError("Broker não confirmou publicação")
    client.disconnect()
    client.loop_stop()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--aluno-id", type=int, required=True)
    parser.add_argument("--curso-id", default="curso-iot")
    parser.add_argument("--tipo", choices=["PRESENCA", "INICIO", "CONCLUSAO"], default="CONCLUSAO")
    parser.add_argument("--repeticoes", type=int, default=1)
    args = parser.parse_args()
    evento = {"eventoId": str(uuid4()), "instituicao": "ac1", "dispositivoId": "lab01",
              "tipo": args.tipo, "alunoId": args.aluno_id, "cursoId": args.curso_id, "media": 8}
    publicar(evento, args.repeticoes)
    print(json.dumps(evento))

