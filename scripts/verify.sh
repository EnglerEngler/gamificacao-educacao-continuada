#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p .runtime
mvn -B verify
.venv/bin/python -m pytest -q tests --junitxml=.runtime/python-tests.xml
npm --prefix frontend ci
npm --prefix frontend run build
mkdir -p src/main/resources/static
cp -r frontend/dist/. src/main/resources/static/
mvn -B package -DskipTests
.venv/bin/python scripts/architecture-evidence.py
.venv/bin/python scripts/validate-delivery.py
