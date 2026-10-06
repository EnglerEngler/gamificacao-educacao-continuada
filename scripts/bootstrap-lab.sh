#!/usr/bin/env bash
# Ubuntu 24.04 x86_64, sem sudo. Downloads ficam fora do Git.
set -euo pipefail
cd "$(dirname "$0")/.."
task_tools="${B1_TOOLS_DIR:-/tmp/b1-tools}"
mkdir -p "$task_tools"
if [ ! -x "$task_tools/uv-x86_64-unknown-linux-gnu/uv" ]; then
    curl -fL https://github.com/astral-sh/uv/releases/download/0.6.14/uv-x86_64-unknown-linux-gnu.tar.gz -o "$task_tools/uv.tar.gz"
    tar -xzf "$task_tools/uv.tar.gz" -C "$task_tools"
fi
if [ ! -x .venv/bin/python ]; then
    "$task_tools/uv-x86_64-unknown-linux-gnu/uv" venv .venv
fi
"$task_tools/uv-x86_64-unknown-linux-gnu/uv" pip install --python .venv/bin/python -r requirements.lock
if [ ! -x "$task_tools/prometheus-3.2.1.linux-amd64/prometheus" ]; then
    curl -fL https://github.com/prometheus/prometheus/releases/download/v3.2.1/prometheus-3.2.1.linux-amd64.tar.gz -o "$task_tools/prometheus.tar.gz"
    tar -xzf "$task_tools/prometheus.tar.gz" -C "$task_tools"
fi
if [ ! -x "$task_tools/grafana-v11.5.2/bin/grafana" ]; then
    curl -fL https://dl.grafana.com/oss/release/grafana-v11.5.2.linux-amd64.tar.gz -o "$task_tools/grafana.tar.gz"
    tar -xzf "$task_tools/grafana.tar.gz" -C "$task_tools"
fi
if [ ! -x "$task_tools/root/usr/sbin/mosquitto" ] || LD_LIBRARY_PATH="$task_tools/root/usr/lib/x86_64-linux-gnu" ldd "$task_tools/root/usr/sbin/mosquitto" | rg -q 'not found'; then
    (cd "$task_tools"
     apt-get download mosquitto libmosquitto1 libdlt2 libwrap0 libwebsockets19t64
     for package in ./*.deb; do dpkg-deb -x "$package" root; done)
fi
mvn -B verify
npm --prefix frontend ci
npm --prefix frontend run build
mkdir -p src/main/resources/static
cp -r frontend/dist/. src/main/resources/static/
mvn -B package -DskipTests
printf 'Laboratório preparado. Execute: .venv/bin/python scripts/lab.py evidence\n'
