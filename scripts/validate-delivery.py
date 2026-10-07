"""Valida rastreabilidade e arquivos operacionais. Não substitui execução dos serviços."""
from pathlib import Path
from urllib.parse import unquote
import json
import re
import yaml

ROOT = Path(__file__).resolve().parents[1]
docs = [ROOT / "README.md", *sorted((ROOT / "docs/arquitetura").rglob("*.md")),
        *sorted((ROOT / "docs/adr").glob("*.md")), *sorted((ROOT / "labs").glob("*/README.md"))]
missing = []
for path in docs:
    for link in re.findall(r"\[[^\]]*\]\(([^)]+)\)", path.read_text()):
        if "://" in link or link.startswith("#"):
            continue
        target = unquote(link.split("#", 1)[0])
        if not (path.parent / target).exists():
            missing.append(f"{path.relative_to(ROOT)} -> {target}")
assert not missing, "\n".join(missing)
for directory in ['src', 'frontend/src', 'docs']:
    for path in (ROOT / directory).rglob('*'):
        if path.suffix in {'.java', '.vue', '.md', '.feature', '.yml'}:
            assert not re.search(r'^(<<<<<<<|=======|>>>>>>>)', path.read_text(), re.M), f'Marcador de conflito em {path}'
adrs = sorted((ROOT / "docs/adr").glob("ADR-*.md"))
assert len(adrs) == 6, "Exatamente seis ADRs essenciais"
matrix = (ROOT / "docs/arquitetura/README.md").read_text()
rows = [line for line in matrix.splitlines() if line.startswith("| ASR-")]
assert len(rows) == 18
for i in range(1, 7):
    assert sum(f"D{i} /" in row for row in rows) == 3, f"D{i}: máximo três ASRs"
    assert (ROOT / f"labs/desafio{i}/README.md").exists()
for path in (ROOT / "ops").rglob("*.yml"):
    yaml.safe_load(path.read_text())
for name in ["docker-compose.yml", "docker-compose.b1.yml"]:
    compose = yaml.safe_load((ROOT / name).read_text())
    assert "services" in compose
    for service in compose["services"].values():
        for volume in service.get("volumes", []):
            if isinstance(volume, str) and volume.startswith("./"):
                assert (ROOT / volume.split(":", 1)[0]).exists(), volume
dashboard = json.loads((ROOT / "ops/grafana/dashboards/b1.json").read_text())
assert len(dashboard["panels"]) == 8 and dashboard["uid"] == "b1-architecture"
assert (ROOT / "Jenkinsfile").exists()
print("Entrega validada: seis ADRs/labs, três ASRs por desafio, links locais e configurações")
