"""Valida rastreabilidade e arquivos operacionais. Não substitui execução dos serviços."""
from pathlib import Path
from urllib.parse import unquote
import json
import re
import yaml

ROOT = Path(__file__).resolve().parents[1]
docs = [ROOT / "README.md", ROOT / "CONTRIBUTING.md", ROOT / "labs/README.md",
        *sorted((ROOT / "docs").rglob("*.md")),
        *sorted((ROOT / "labs").glob("*/README.md")),
        ROOT / "scripts/README.md", ROOT / "services/README.md", ROOT / "ops/README.md"]
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
assert not (ROOT / "docs/ENTREGA-B1.md").exists(), "Consolidar entrega em docs/arquitetura/README.md"
rows = [line for line in matrix.splitlines() if line.startswith("| ASR-")]
assert len(rows) == 18
for i in range(1, 7):
    assert sum(f"D{i} /" in row for row in rows) == 3, f"D{i}: máximo três ASRs"
    assert (ROOT / f"labs/desafio{i}/README.md").exists()
    challenge = (ROOT / f"labs/desafio{i}/README.md").read_text()
    for stage in ('1. RF', '2. RNF', '3. ASR', '4. RPC', '5. Alternativas/trade-offs',
                  '6. ADR', '7. C4', '8. Evidência'):
        assert f'| {stage} |' in challenge, f'D{i}: etapa {stage} ausente'
    assert (ROOT / f"docs/evidencias/b1/desafio{i}/README.md").exists()
assert (ROOT / 'docs/historico/ac1/gamificacao.feature').exists()
assert not (ROOT / 'docs/gamificacao.feature').exists(), 'BDD AC1 deve permanecer no histórico'
for entry in (ROOT / 'docs/evidencias').iterdir():
    assert entry.name in {'README.md', 'b1'}, f'Evidência fora de sua etapa: {entry.name}'
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
print("Entrega validada: seis ADRs/labs, oito etapas por desafio, três ASRs por desafio, evidências indexadas, links e configurações")
