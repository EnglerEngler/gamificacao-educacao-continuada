#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mvn -B verify
.venv/bin/python -m pytest -q tests --junitxml=.runtime/python-tests.xml
npm --prefix frontend ci
npm --prefix frontend run build
.venv/bin/python scripts/architecture-evidence.py

