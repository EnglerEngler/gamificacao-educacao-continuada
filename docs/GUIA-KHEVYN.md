# Guia de contribuição — Khevyn Henrique Guedes T. Alves

**Responsável:** Khevyn Henrique Guedes T. Alves — **RA:** 223761  
**User story:** US03 — Plano Premium e moedas.

## Sua tarefa

Implemente a regra de mudança para o plano Premium e moedas. A contribuição deve ser feita por você, usando sua conta do GitHub, para que a autoria apareça corretamente no histórico.

O cenário que você representa é:

> Como aluno, quero ter minha assinatura atualizada para Premium ao conquistar 12 cursos para receber benefícios exclusivos e três moedas.

## Alteração técnica sugerida

Abra `docs/gamificacao.feature` e acrescente os cenários da US03, incluindo o caso que confirma que as moedas não são duplicadas quando um aluno já Premium conclui outro curso:

```gherkin
  Cenário: Não duplicar moedas de aluno que já é Premium
    Dado que o aluno já está no plano Premium com 3 moedas
    Quando concluir outro curso
    Então o plano deve continuar Premium
    E o aluno deve continuar com 3 moedas
```

Depois, implemente os campos de plano, cursos concluídos e moedas nas camadas Domain, Entity, DTO, Service e Controller. Crie os testes correspondentes em `domain/AlunoTest.java` e atualize o README.

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
git add .
git commit -m "feat: implementa plano Premium da US03"
git push -u origin docs/us03-khevyn
```

5. No GitHub, abra um **Pull Request** da branch `docs/us03-khevyn` para `main`.

## Resultado esperado

Seu nome aparecerá como autor do commit e do Pull Request. Tire uma captura da página do PR ou do histórico de commits para a evidência individual.
