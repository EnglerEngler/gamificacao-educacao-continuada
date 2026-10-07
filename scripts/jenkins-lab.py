"""Inicia Jenkins local isolado, configura job a partir do Git e coleta build real."""
import argparse
import json
import os
from pathlib import Path
import subprocess
import sys
import time
from xml.sax.saxutils import escape

import httpx

ROOT = Path(__file__).resolve().parents[1]
TOOLS = Path(os.getenv("B1_TOOLS_DIR", "/tmp/b1-tools"))
JHOME = TOOLS / "jenkins-home"
URL = "http://127.0.0.1:8090"
AUTH = ("b1", "lab-b1-jenkins")
OUT = ROOT / "docs/evidencias/b1/desafio6/jenkins"


def start():
    try:
        if httpx.get(URL + '/api/json', auth=AUTH, timeout=2).is_success:
            print('Jenkins local já ativo em :8090')
            return
    except httpx.RequestError:
        pass
    JHOME.mkdir(parents=True, exist_ok=True)
    init = JHOME / "init.groovy.d"
    init.mkdir(exist_ok=True)
    (init / "b1.groovy").write_text("""import jenkins.model.Jenkins
import hudson.security.HudsonPrivateSecurityRealm
import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
def j = Jenkins.get()
def realm = new HudsonPrivateSecurityRealm(false)
realm.createAccount('b1', 'lab-b1-jenkins')
j.setSecurityRealm(realm)
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
j.setAuthorizationStrategy(strategy)
j.setNumExecutors(1)
j.save()
""")
    env = dict(os.environ, JENKINS_HOME=str(JHOME), PATH=str(TOOLS / "uv-x86_64-unknown-linux-gnu") + ":" + os.environ["PATH"])
    log = (ROOT / ".runtime/jenkins.log").open("a")
    proc = subprocess.Popen(["java", "-Xms64m", "-Xmx384m", "-Djenkins.install.runSetupWizard=false",
        "-Dhudson.plugins.git.GitSCM.ALLOW_LOCAL_CHECKOUT=true",
        "-jar", str(TOOLS / "jenkins.war"), "--httpListenAddress=127.0.0.1", "--httpPort=8090"],
        env=env, stdout=log, stderr=subprocess.STDOUT, start_new_session=True)
    (ROOT / ".runtime/jenkins-pid").write_text(str(proc.pid))
    print("Jenkins iniciando em :8090 (b1 / lab-b1-jenkins)")


def build():
    OUT.mkdir(parents=True, exist_ok=True)
    with httpx.Client(base_url=URL, auth=AUTH, timeout=30) as client:
        for _ in range(180):
            try:
                if client.get("/api/json").is_success:
                    break
            except httpx.RequestError:
                pass
            time.sleep(1)
        else:
            raise RuntimeError("Jenkins não iniciou")
        crumb = client.get("/crumbIssuer/api/json").json()
        client.headers[crumb["crumbRequestField"]] = crumb["crumb"]
        branch = subprocess.check_output(["git", "branch", "--show-current"], cwd=ROOT).decode().strip()
        revision = subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=ROOT).decode().strip()
        config = f"""<?xml version="1.0" encoding="UTF-8"?>
<flow-definition plugin="workflow-job">
  <description>B1 — execução real dos gates no laboratório nativo.</description>
  <keepDependencies>false</keepDependencies>
  <properties>
    <hudson.model.ParametersDefinitionProperty>
      <parameterDefinitions>
        <hudson.model.BooleanParameterDefinition><name>BUILD_IMAGES</name><description>Empacotar imagens</description><defaultValue>false</defaultValue></hudson.model.BooleanParameterDefinition>
        <hudson.model.BooleanParameterDefinition><name>DEPLOY_LAB</name><description>Implantar laboratório</description><defaultValue>false</defaultValue></hudson.model.BooleanParameterDefinition>
      </parameterDefinitions>
    </hudson.model.ParametersDefinitionProperty>
  </properties>
  <definition class="org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition" plugin="workflow-cps">
    <scm class="hudson.plugins.git.GitSCM" plugin="git">
      <configVersion>2</configVersion>
      <userRemoteConfigs><hudson.plugins.git.UserRemoteConfig><url>{escape(str(ROOT))}</url></hudson.plugins.git.UserRemoteConfig></userRemoteConfigs>
      <branches><hudson.plugins.git.BranchSpec><name>*/{escape(branch)}</name></hudson.plugins.git.BranchSpec></branches>
      <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
      <submoduleCfg class="empty-list"/><extensions/>
    </scm>
    <scriptPath>Jenkinsfile</scriptPath><lightweight>true</lightweight>
  </definition>
  <triggers/><disabled>false</disabled>
</flow-definition>"""
        exists = client.get("/job/b1-architecture/api/json").is_success
        path = "/job/b1-architecture/config.xml" if exists else "/createItem?name=b1-architecture"
        response = client.post(path, content=config.encode(), headers={"Content-Type": "application/xml"})
        response.raise_for_status()
        scheduled = client.post("/job/b1-architecture/buildWithParameters", data={"BUILD_IMAGES": "false", "DEPLOY_LAB": "false"})
        scheduled.raise_for_status()
        queue_url = scheduled.headers['Location']
        for _ in range(180):
            queue_item = client.get(queue_url + 'api/json').json()
            if queue_item.get('cancelled'):
                raise RuntimeError('Build cancelado na fila')
            if queue_item.get('executable'):
                build_number = queue_item['executable']['number']
                break
            time.sleep(1)
        else:
            raise RuntimeError('Build não saiu da fila')
        for _ in range(1200):
            response = client.get(f"/job/b1-architecture/{build_number}/api/json")
            if response.is_success and not response.json()["building"]:
                result = response.json()
                break
            time.sleep(1)
        else:
            raise RuntimeError("Pipeline excedeu tempo")
        console = client.get(f"/job/b1-architecture/{result['number']}/consoleText").text
        (OUT / "jenkins-console.log").write_text(console)
        test_report = client.get(f"/job/b1-architecture/{result['number']}/testReport/api/json").json()
        stages = client.get(f"/job/b1-architecture/{result['number']}/wfapi/describe")
        built_revisions = [a['lastBuiltRevision']['SHA1'] for a in result.get('actions', [])
                           if a.get('lastBuiltRevision')]
        assert revision in built_revisions, f'Commit esperado {revision}, executado {built_revisions}'
        output = {"status": result["result"], "numero": result["number"],
                  "url_local": result["url"], "commit_validado": revision,
                  "jenkins_version": response.headers.get("x-jenkins"),
                  "duracao_ms": result["duration"], "timestamp": result["timestamp"],
                  "relatorio_testes": test_report, "build_images": False, "deploy_lab": False,
                  "motivo_stages_opcionais": "Docker Desktop indisponível no WSL",
                  "stages": stages.json() if stages.is_success else None}
        (OUT / "jenkins.json").write_text(json.dumps(output, ensure_ascii=False, indent=2) + "\n")
        print("Build Jenkins", result["number"], result["result"])
        if result["result"] != "SUCCESS":
            raise SystemExit("Pipeline falhou; confira jenkins-console.log")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("action", choices=["start", "build"])
    args = parser.parse_args()
    start() if args.action == "start" else build()
