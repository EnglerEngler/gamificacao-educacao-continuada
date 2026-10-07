"""Compara dependências reais do caso de uso AC1 antes/depois; gera artefato versionável."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PREFIX = "br.edu.unifacens.gamificacao."


def imports(path):
    return re.findall(r"import\s+([\w.*]+);", path.read_text())


before = ROOT / "labs/desafio1/baseline/src/main/java/br/edu/unifacens/gamificacao/service/AlunoService.java"
after = ROOT / "src/main/java/br/edu/unifacens/gamificacao/aluno/application/AlunoService.java"
old = [i for i in imports(before) if i.startswith(PREFIX)]
new = [i for i in imports(after) if i.startswith(PREFIX)]
java = ROOT / "src/main/java"
domain = list(java.glob("**/domain/*.java"))
violations = []
for path in domain:
    violations += [i for i in imports(path) if i.startswith(("org.springframework", "jakarta", PREFIX))]
assert not violations, violations
result = {
    "baseline_commit_ac1": "b31abd3",
    "caso_de_uso": "conclusao com recompensa",
    "antes": {"arquivo": str(before.relative_to(ROOT)), "imports": old,
              "acesso_direto_entidade_jpa": any(".entity." in i for i in old),
              "acesso_direto_spring_data_repository": any(".repository." in i for i in old)},
    "depois": {"arquivo": str(after.relative_to(ROOT)), "imports": new,
               "acesso_direto_entidade_jpa": any(".internal.persistence." in i for i in new),
               "acesso_direto_spring_data_repository": any(".internal.persistence." in i for i in new)},
    "dominio": {"arquivos": len(domain), "imports_tecnicos": violations},
    "observacao": "Agrupar pastas não reduz necessariamente o número de imports; a melhoria testada é a direção da dependência e o encapsulamento da persistência.",
}
out = ROOT / "docs/evidencias/b1/desafio1/dependencias.json"
out.parent.mkdir(parents=True, exist_ok=True)
out.write_text(json.dumps(result, ensure_ascii=False, indent=2) + "\n")
print(json.dumps(result, ensure_ascii=False, indent=2))
