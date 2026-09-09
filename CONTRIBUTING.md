# Contribuição do grupo

Cada integrante deve fazer ao menos um commit usando **a própria conta do GitHub**. Não reutilizem o token, e-mail ou perfil de outra pessoa: a autoria exibida no GitHub precisa corresponder ao integrante que realizou a alteração.

## Passos para cada integrante

1. Peça ao administrador do repositório acesso como colaborador usando seu @usuário do GitHub.
2. Aceite o convite no e-mail/GitHub.
3. Clone o repositório com sua conta e configure seu nome e e-mail:

```bash
git clone https://github.com/EnglerEngler/gamificacao-educacao-continuada.git
cd gamificacao-educacao-continuada
git config user.name "SEU NOME COMPLETO"
git config user.email "SEU_EMAIL_DO_GITHUB"
```

4. Crie uma branch individual, faça sua alteração e envie-a:

```bash
git checkout -b docs/us02-eduardo
# editar os arquivos atribuídos
git add .
git commit -m "docs: detalha cenarios BDD da US02"
git push -u origin docs/us02-eduardo
```

5. Abra um Pull Request para `main`. O commit e o PR aparecerão vinculados à conta do integrante.

## Divisão sugerida

| Integrante | Branch | Alteração real sugerida | Commit sugerido |
| --- | --- | --- | --- |
| Eduardo Bismara Nastri | `docs/us02-eduardo` | Revisar/expandir o cenário BDD da premiação do fórum em `docs/gamificacao.feature` e README. | `docs: detalha cenarios BDD da US02` |
| Khevyn Henrique Guedes T. Alves | `docs/us03-khevyn` | Revisar/expandir o cenário BDD Premium e a documentação de moedas. | `docs: detalha cenarios BDD da US03` |
| João Victor Cardoso Engler Rizzi de Araujo | `test/us01-joao` | Revisar os testes da US01 e gerar a evidência JaCoCo. | `test: valida cobertura da US01` |

> Cada alteração deve ser efetivamente revisada pelo autor antes do commit. Não façam commits vazios apenas para gerar histórico.
