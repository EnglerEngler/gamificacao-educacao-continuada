# Guia de contribuição — Khevyn Henrique Guedes T. Alves

**Responsável:** Khevyn Henrique Guedes T. Alves — **RA:** 223761  
**User story:** US03 — Plano Premium e moedas.

## Sua tarefa

Revise e melhore a documentação do cenário Premium. A contribuição deve ser feita por você, usando sua conta do GitHub, para que a autoria apareça corretamente no histórico.

O cenário que você representa é:

> Como aluno, quero ter minha assinatura atualizada para Premium ao conquistar 12 cursos para receber benefícios exclusivos e três moedas.

## Alteração sugerida

Abra `docs/gamificacao.feature` e, abaixo do cenário **Migrar aluno para Premium**, acrescente um cenário que confirme que as moedas não são duplicadas quando um aluno já Premium conclui outro curso:

```gherkin
  Cenário: Não duplicar moedas de aluno que já é Premium
    Dado que o aluno já está no plano Premium com 3 moedas
    Quando concluir outro curso
    Então o plano deve continuar Premium
    E o aluno deve continuar com 3 moedas
```

Depois, em `README.md`, inclua uma frase no item da US03 informando que a regra impede a duplicação das moedas Premium.

## Como fazer o commit

1. Peça ao João Victor seu @usuário do GitHub para ser convidado ao repositório e aceite o convite.
2. No terminal, execute:

```bash
git clone https://github.com/EnglerEngler/gamificacao-educacao-continuada.git
cd gamificacao-educacao-continuada
git config user.name "Khevyn Henrique Guedes T. Alves"
git config user.email "SEU_EMAIL_DO_GITHUB"
git checkout -b docs/us03-khevyn
```

3. Faça as duas alterações descritas acima.
4. Confira o que mudou e envie seu commit:

```bash
git diff
git add docs/gamificacao.feature README.md
git commit -m "docs: detalha cenario Premium da US03"
git push -u origin docs/us03-khevyn
```

5. No GitHub, abra um **Pull Request** da branch `docs/us03-khevyn` para `main`.

## Resultado esperado

Seu nome aparecerá como autor do commit e do Pull Request. Tire uma captura da página do PR ou do histórico de commits para a evidência individual.
