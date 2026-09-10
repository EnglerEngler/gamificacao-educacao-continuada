# Guia de contribuição — Eduardo Bismara Nastri

**Responsável:** Eduardo Bismara Nastri — **RA:** 211466  
**User story:** US02 — Premiação por participação no fórum.

## Sua tarefa

Implemente a regra de recompensa por participação no fórum. A contribuição deve ser feita por você, usando sua conta do GitHub, para que a autoria apareça corretamente no histórico.

O cenário que você representa é:

> Como aluno, quero ser recompensado pela minha participação e ajuda no fórum para ser incentivado a colaborar com outros alunos.

## Alteração técnica sugerida

Abra `docs/gamificacao.feature` e acrescente os cenários da US02, incluindo o caso em que somente o aluno selecionado como destaque do mês recebe a recompensa:

```gherkin
  Cenário: Não premiar aluno que não foi o destaque do fórum
    Dado que o aluno não foi identificado como o mais ativo no fórum no mês
    Quando a premiação mensal for processada
    Então o aluno não deve receber curso adicional
```

Depois, implemente essa regra em `domain/Aluno.java`, crie os testes correspondentes em `domain/AlunoTest.java` e exponha um endpoint no Controller. Atualize o README com o endpoint criado.

## Como fazer o commit

1. Peça ao João Victor seu @usuário do GitHub para ser convidado ao repositório e aceite o convite.
2. No terminal, execute:

```bash
git clone https://github.com/EnglerEngler/gamificacao-educacao-continuada.git
cd gamificacao-educacao-continuada
git config user.name "Eduardo Bismara Nastri"
git config user.email "SEU_EMAIL_DO_GITHUB"
git checkout -b docs/us02-eduardo
```

3. Faça as duas alterações descritas acima.
4. Confira o que mudou e envie seu commit:

```bash
git diff
git add .
git commit -m "feat: implementa recompensa de forum da US02"
git push -u origin docs/us02-eduardo
```

5. No GitHub, abra um **Pull Request** da branch `docs/us02-eduardo` para `main`.

## Resultado esperado

Seu nome aparecerá como autor do commit e do Pull Request. Tire uma captura da página do PR ou do histórico de commits para a evidência individual.
