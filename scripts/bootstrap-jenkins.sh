#!/usr/bin/env bash
# Jenkins local isolado para reproduzir a execução registrada; não modifica Jenkins existente.
set -euo pipefail
cd "$(dirname "$0")/.."
task_tools="${B1_TOOLS_DIR:-/tmp/b1-tools}"
mkdir -p "$task_tools"
if [ ! -f "$task_tools/jenkins.war" ]; then
    curl -fL https://get.jenkins.io/war-stable/2.580.1/jenkins.war -o "$task_tools/jenkins.war"
fi
if [ ! -f "$task_tools/jenkins-plugin-manager.jar" ]; then
    curl -fL https://github.com/jenkinsci/plugin-installation-manager-tool/releases/download/2.13.2/jenkins-plugin-manager-2.13.2.jar -o "$task_tools/jenkins-plugin-manager.jar"
fi
java -jar "$task_tools/jenkins-plugin-manager.jar" --war "$task_tools/jenkins.war" \
    --plugin-download-directory "$task_tools/jenkins-home/plugins" \
    --plugin-file ops/jenkins/plugins.txt --latest=false
.venv/bin/python scripts/jenkins-lab.py start
.venv/bin/python scripts/jenkins-lab.py build
