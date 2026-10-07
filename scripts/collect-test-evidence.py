import json
from pathlib import Path
import shutil
from datetime import datetime, timezone
from xml.etree import ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
out = ROOT / "docs/evidencias/b1/desafio2"
out.mkdir(parents=True, exist_ok=True)
reports = out / "relatorios"
reports.mkdir(exist_ok=True)
suites = []
for path in sorted((ROOT / "target/surefire-reports").glob("TEST-*.xml")):
    root = ET.parse(path).getroot()
    suites.append({"nome": root.attrib["name"], "testes": int(root.attrib["tests"]),
                   "falhas": int(root.attrib["failures"]), "erros": int(root.attrib["errors"]),
                   "ignorados": int(root.attrib["skipped"]),
                   "casos": [c.attrib["name"] for c in root.findall("testcase")]})
    shutil.copyfile(path, reports / path.name)
assert suites and not any(s["falhas"] or s["erros"] for s in suites)
jacoco = ET.parse(ROOT / "target/site/jacoco/jacoco.xml").getroot()
counters = {c.attrib["type"]: {"cobertos": int(c.attrib["covered"]), "perdidos": int(c.attrib["missed"])}
            for c in jacoco.findall("counter")}
assert counters["LINE"]["perdidos"] == counters["BRANCH"]["perdidos"] == 0
shutil.copyfile(ROOT / "target/site/jacoco/jacoco.xml", reports / "jacoco.xml")
shutil.copytree(ROOT / "target/site/jacoco", out / "cobertura", dirs_exist_ok=True)
python = ET.parse(ROOT / ".runtime/python-tests.xml").getroot()
pysuites = list(python) if python.tag == "testsuites" else [python]
result = {"coletado_utc": datetime.now(timezone.utc).isoformat(), "comando_java": "mvn -B verify",
          "java_testes": sum(s["testes"] for s in suites), "suites": suites,
          "cobertura_dominio": counters, "python_testes": sum(int(s.attrib["tests"]) for s in pysuites),
          "python_falhas": sum(int(s.attrib["failures"]) + int(s.attrib["errors"]) for s in pysuites)}
assert result["python_falhas"] == 0
shutil.copyfile(ROOT / ".runtime/python-tests.xml", reports / "python-tests.xml")
(out / "testes.json").write_text(json.dumps(result, ensure_ascii=False, indent=2) + "\n")
print(f"Evidência: {result['java_testes']} testes Java e {result['python_testes']} Python; domínio 100% linhas/branches")
