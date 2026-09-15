# Aprende+ — Educação Continuada Gamificada

> Projeto acadêmico — ATDD, BDD e TDD | Entrega via GitHub

## Descrição do estudo de caso

Uma plataforma de cursos online/EAD funciona por assinatura. O aluno inicia no plano Básico e, ao concluir um curso com média **maior que 7,0**, recebe acesso a três novos cursos. A participação de destaque no fórum rende um curso adicional ao final do mês. Ao conquistar 12 cursos, o aluno passa para o plano Premium e recebe três moedas, que poderão ser acumuladas ou convertidas em benefícios.

O **Aprende+** implementa essas regras de gamificação. O projeto usa ATDD: os cenários BDD definem o comportamento esperado e os testes TDD validam as regras antes e durante a implementação.

## Integrantes e user stories

| Integrante | RA | User story redigida |
| --- | ---: | --- |
| Eduardo Bismara Nastri | 211466 | **US02** — Como aluno, quero ser recompensado pela minha participação e ajuda no fórum para ser incentivado a colaborar. |
| Khevyn Henrique Guedes T. Alves | 223761 | **US03** — Como aluno, quero ter minha assinatura atualizada para Premium ao conquistar 12 cursos para receber benefícios exclusivos. |
| João Victor Cardoso Engler Rizzi de Araujo | 236602 | **US01** — Como aluno, quero desbloquear 3 novos cursos ao concluir um curso com média acima de 7,0 para continuar meus estudos. |

### User story escolhida para a implementação central

**US01 — João Victor Cardoso Engler Rizzi de Araujo (RA 236602).**

Critérios de aceitação (BDD), também disponíveis em [docs/gamificacao.feature](docs/gamificacao.feature):

1. Com 5 cursos disponíveis, a conclusão com média 8,0 libera 3, totalizando 8.
2. A conclusão com média 7,0 ou menor não libera cursos.
3. Um curso que ainda está em andamento não gera recompensa.

**Escopo implementado nesta entrega:** somente a US01. As US02 e US03 estão descritas como backlog e reservadas para implementação individual de Eduardo e Khevyn, respectivamente.

## BDD: cenários e responsáveis

Os cenários estão em [docs/gamificacao.feature](docs/gamificacao.feature). A identificação a seguir atende à atribuição individual dos BDDs.

| Responsável | User story | Cenários BDD redigidos |
| --- | --- | --- |
| João Victor Cardoso Engler Rizzi de Araujo — RA 236602 | US01 | Desbloqueio com média maior que 7; média igual a 7 não libera cursos; curso em andamento não libera cursos. |
| Eduardo Bismara Nastri — RA 211466 | US02 | A redigir e implementar: premiação mensal para aluno mais participativo do fórum. |
| Khevyn Henrique Guedes T. Alves — RA 223761 | US03 | A redigir e implementar: mudança para plano Premium ao atingir 12 cursos e crédito de três moedas. |

Exemplo do cenário principal (US01):

```gherkin
Cenário: Desbloquear cursos após conclusão com boa média
  Dado que o aluno possui 5 cursos disponíveis
  Quando concluir um curso com média final 8,0
  Então o sistema deve liberar 3 novos cursos
  E o aluno deve visualizar 8 cursos disponíveis
```

Guias individuais de contribuição: [Khevyn](docs/GUIA-KHEVYN.md) e [Eduardo](docs/GUIA-EDUARDO.md).



A planilha original da atividade está preservada em [docs/ATDD-Case-AC1.xlsx](docs/ATDD-Case-AC1.xlsx).

## Estrutura e camadas

```text
src/main/java/br/edu/unifacens/gamificacao/
├── domain/       # regras de negócio puras e seus testes TDD
├── entity/       # persistência JPA
├── repository/   # acesso aos dados
├── service/      # orquestra o caso de uso
├── dto/          # contratos da API
└── controller/   # endpoints REST documentados no Swagger
```

## TDD: RED → GREEN → BLUE

O teste principal está em `src/test/java/.../domain/AlunoTest.java`.

- **RED:** criar o teste `desbloqueiaTresCursosQuandoConcluiComMediaMaiorQueSete`; antes de existir `Aluno.concluirCurso`, ele falha por não haver implementação.
- **GREEN:** implementar o mínimo em `Aluno.concluirCurso` para liberar três cursos quando a média for maior que 7,0.
- **BLUE:** refatorar a regra da US01 (curso em andamento e média limite), mantendo todos os testes verdes e cobertura de linhas do pacote `domain` em 100%.

Para a evidência formal, os commits devem ser feitos nesta sequência, sem deixar falhas na branch `main`:

```bash
git commit -m "test(red): especifica desbloqueio de cursos"
git tag evidencia-tdd-red
git commit -m "feat(green): desbloqueia cursos por média"
git tag evidencia-tdd-green
git commit -m "refactor(blue): consolida regras de gamificação"
git tag evidencia-tdd-blue
```

Execute a evidência de testes e cobertura:

```bash
mvn clean verify
```

O relatório fica em `target/site/jacoco/index.html`. O JaCoCo exige 100% das linhas e decisões (branches) do pacote `domain`, sem itens amarelos ou vermelhos nesse relatório. Na versão atual, `mvn clean verify` executa os testes de domínio e valida essa meta automaticamente.


## Evidências para a entrega

| Evidência exigida | Localização / Comprovação | Status |
| --- | --- | --- |
| **BDD** | Arquivo `docs/gamificacao.feature` e planilha anexada | US01 implementada; US02 e US03 aguardam os responsáveis |
| **RED, GREEN e BLUE** | Histórico de commits/tags e screenshots abaixo | Concluído (US01) |
| **Testes e cobertura** | Relatório JaCoCo (`target/site/jacoco/index.html`) | Validado: 100% do domínio atendido |

### Fase RED
Testes criados antes da implementação falhando, garantindo que a regra de negócio é necessária.
![Painel de Testes - Fase RED](docs/evidencias/tdd-red.png)

###  Fase GREEN
Implementação mínima para fazer os testes passarem.
![Painel de Testes e Cobertura - Fase GREEN](docs/evidencias/tdd-green.jpg)

###  Fase BLUE e Cobertura (JaCoCo)
Refatoração do código (Clean Code e remoção de Magic Numbers) mantendo os testes passando, junto com a comprovação de 100% de cobertura pelo JaCoCo.
![Painel de Testes - Fase BLUE](docs/evidencias/tdd-blue.jpg)
![Relatório JaCoCo](docs/evidencias/jacoco.png)

---

## Como executar o projeto (Docker)

O projeto está configurado para rodar em contêineres, garantindo a padronização do ambiente de desenvolvimento.

1. Certifique-se de ter o **Docker Desktop** instalado e rodando.
2. Na raiz do projeto, execute o comando:
   ```bash
   docker compose up -d --build
