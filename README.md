# Aprende+ â€” EducaÃ§Ã£o Continuada Gamificada

> Projeto acadÃªmico â€” ATDD, BDD e TDD | Entrega via GitHub

## DescriÃ§Ã£o do estudo de caso

Uma plataforma de cursos online/EAD funciona por assinatura. O aluno inicia no plano BÃ¡sico e, ao concluir um curso com mÃ©dia **maior que 7,0**, recebe acesso a trÃªs novos cursos. A participaÃ§Ã£o de destaque no fÃ³rum rende um curso adicional ao final do mÃªs. Ao conquistar 12 cursos, o aluno passa para o plano Premium e recebe trÃªs moedas, que poderÃ£o ser acumuladas ou convertidas em benefÃ­cios.

O **Aprende+** implementa essas regras de gamificaÃ§Ã£o. O projeto usa ATDD: os cenÃ¡rios BDD definem o comportamento esperado e os testes TDD validam as regras antes e durante a implementaÃ§Ã£o.

## Integrantes e user stories

| Integrante | RA | User story redigida |
| --- | ---: | --- |
| Eduardo Bismara Nastri | 211466 | **US02** â€” Como aluno, quero ser recompensado pela minha participaÃ§Ã£o e ajuda no fÃ³rum para ser incentivado a colaborar. |
| Khevyn Henrique Guedes T. Alves | 223761 | **US03** â€” Como aluno, quero ter minha assinatura atualizada para Premium ao conquistar 12 cursos para receber benefÃ­cios exclusivos. |
| JoÃ£o Victor Cardoso Engler Rizzi de Araujo | 236602 | **US01** â€” Como aluno, quero desbloquear 3 novos cursos ao concluir um curso com mÃ©dia acima de 7,0 para continuar meus estudos. |

### User story escolhida para a implementaÃ§Ã£o central

**US01 â€” JoÃ£o Victor Cardoso Engler Rizzi de Araujo (RA 236602).**

CritÃ©rios de aceitaÃ§Ã£o (BDD), tambÃ©m disponÃ­veis em [docs/gamificacao.feature](docs/gamificacao.feature):

1. Com 5 cursos disponÃ­veis, a conclusÃ£o com mÃ©dia 8,0 libera 3, totalizando 8.
2. A conclusÃ£o com mÃ©dia 7,0 ou menor nÃ£o libera cursos.
3. Um curso que ainda estÃ¡ em andamento nÃ£o gera recompensa.

**Escopo implementado nesta entrega:** somente a US01. As US02 e US03 estÃ£o descritas como backlog e reservadas para implementaÃ§Ã£o individual de Eduardo e Khevyn, respectivamente.

## BDD: cenÃ¡rios e responsÃ¡veis

Os cenÃ¡rios estÃ£o em [docs/gamificacao.feature](docs/gamificacao.feature). A identificaÃ§Ã£o a seguir atende Ã  atribuiÃ§Ã£o individual dos BDDs.

| ResponsÃ¡vel | User story | CenÃ¡rios BDD redigidos |
| --- | --- | --- |
| JoÃ£o Victor Cardoso Engler Rizzi de Araujo â€” RA 236602 | US01 | Desbloqueio com mÃ©dia maior que 7; mÃ©dia igual a 7 nÃ£o libera cursos; curso em andamento nÃ£o libera cursos. |
| Eduardo Bismara Nastri â€” RA 211466 | US02 | A redigir e implementar: premiaÃ§Ã£o mensal para aluno mais participativo do fÃ³rum. |
| Khevyn Henrique Guedes T. Alves â€” RA 223761 | US03 | A redigir e implementar: mudanÃ§a para plano Premium ao atingir 12 cursos e crÃ©dito de trÃªs moedas. |

Exemplo do cenÃ¡rio principal (US01):

```gherkin
CenÃ¡rio: Desbloquear cursos apÃ³s conclusÃ£o com boa mÃ©dia
  Dado que o aluno possui 5 cursos disponÃ­veis
  Quando concluir um curso com mÃ©dia final 8,0
  EntÃ£o o sistema deve liberar 3 novos cursos
  E o aluno deve visualizar 8 cursos disponÃ­veis
```

Guias individuais de contribuiÃ§Ã£o: [Khevyn](docs/GUIA-KHEVYN.md) e [Eduardo](docs/GUIA-EDUARDO.md).

## Tecnologias

- Java 21, Spring Boot, Spring Web, Spring Data JPA
- H2 (execuÃ§Ã£o local) e PostgreSQL (container)
- Swagger/OpenAPI, em `http://localhost:8080/swagger-ui.html`
- JUnit 5 e JaCoCo
- Vue 3 + Vite
- Docker e Docker Compose

A planilha original da atividade estÃ¡ preservada em [docs/ATDD-Case-AC1.xlsx](docs/ATDD-Case-AC1.xlsx).

## Estrutura e camadas

```text
src/main/java/br/edu/unifacens/gamificacao/
â”œâ”€â”€ domain/       # regras de negÃ³cio puras e seus testes TDD
â”œâ”€â”€ entity/       # persistÃªncia JPA
â”œâ”€â”€ repository/   # acesso aos dados
â”œâ”€â”€ service/      # orquestra o caso de uso
â”œâ”€â”€ dto/          # contratos da API
â””â”€â”€ controller/   # endpoints REST documentados no Swagger
```

## TDD: RED â†’ GREEN â†’ BLUE

O teste principal estÃ¡ em `src/test/java/.../domain/AlunoTest.java`.

- **RED:** criar o teste `desbloqueiaTresCursosQuandoConcluiComMediaMaiorQueSete`; antes de existir `Aluno.concluirCurso`, ele falha por nÃ£o haver implementaÃ§Ã£o.
- **GREEN:** implementar o mÃ­nimo em `Aluno.concluirCurso` para liberar trÃªs cursos quando a mÃ©dia for maior que 7,0.
- **BLUE:** refatorar a regra da US01 (curso em andamento e mÃ©dia limite), mantendo todos os testes verdes e cobertura de linhas do pacote `domain` em 100%.

Para a evidÃªncia formal, os commits devem ser feitos nesta sequÃªncia, sem deixar falhas na branch `main`:

```bash
git commit -m "test(red): especifica desbloqueio de cursos"
git tag evidencia-tdd-red
git commit -m "feat(green): desbloqueia cursos por mÃ©dia"
git tag evidencia-tdd-green
git commit -m "refactor(blue): consolida regras de gamificaÃ§Ã£o"
git tag evidencia-tdd-blue
```

Execute a evidÃªncia de testes e cobertura:

```bash
mvn clean verify
```

O relatÃ³rio fica em `target/site/jacoco/index.html`. O JaCoCo exige 100% das linhas e decisÃµes (branches) do pacote `domain`, sem itens amarelos ou vermelhos nesse relatÃ³rio. Na versÃ£o atual, `mvn clean verify` executa os testes de domÃ­nio e valida essa meta automaticamente.

## Executar localmente

Backend com H2:

```bash
mvn spring-boot:run
```

- API: `http://localhost:8080/api/alunos`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Console H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:gamificacao`
  - usuÃ¡rio: `sa`; senha vazia.

Frontend:

```bash
cd frontend
npm install
npm run dev
```

Abra `http://localhost:5173`.

## Executar com Docker e PostgreSQL

Primeiro empacote a aplicaÃ§Ã£o:

```bash
mvn clean package
docker compose up --build
```

O PostgreSQL estarÃ¡ em `localhost:5432`, com banco, usuÃ¡rio e senha `gamificacao`. A aplicaÃ§Ã£o sobe no perfil `postgres` em `http://localhost:8080`.

O pgAdmin estarÃ¡ em `http://localhost:5050` (login `admin@gamificacao.local`, senha `admin`). No pgAdmin, cadastre o host `postgres`, porta `5432`, usuÃ¡rio `gamificacao` e senha `gamificacao`.

> Para a evidÃªncia, execute os dois modos (H2 local e Docker/PostgreSQL) e anexe screenshots do Swagger, console H2, containers em execuÃ§Ã£o e tela do front ao envio no Canvas.

## Endpoints

| MÃ©todo | Rota | Finalidade |
| --- | --- | --- |
| POST | `/api/alunos` | Cadastra aluno |
| GET | `/api/alunos` | Lista alunos |
| POST | `/api/alunos/{id}/cursos/conclusao` | Registra mÃ©dia e conclusÃ£o do curso |

Exemplo para criar aluno:

```json
{ "nome": "JoÃ£o Victor", "cursosDisponiveis": 5 }
```

Exemplo para registrar uma conclusÃ£o com recompensa:

```json
{ "media": 8.0, "concluido": true }
```

## EvidÃªncias para a entrega

| EvidÃªncia exigida | Como gerar | SituaÃ§Ã£o |
| --- | --- | --- |
| BDD | Arquivo `docs/gamificacao.feature` e planilha anexada | US01 implementada; US02 e US03 aguardam os responsÃ¡veis |
| RED, GREEN e BLUE | HistÃ³rico de commits/tags e screenshots da IDE | Registrar antes da postagem no Canvas |
| Testes e cobertura | `mvn clean verify`; abrir `target/site/jacoco/index.html` | Validado localmente: 8 testes e regra de 100% do domÃ­nio atendida |
| Swagger | Abrir `/swagger-ui.html` com a API em execuÃ§Ã£o | Capturar screenshot |
| H2 | Abrir `/h2-console` com a API em execuÃ§Ã£o | Capturar screenshot |
| PostgreSQL e pgAdmin | `mvn package` e `docker compose up --build`; abrir `localhost:5050` | Capturar screenshot quando Docker estiver disponÃ­vel |
| Frontend Vue | `cd frontend && npm run dev` | Capturar screenshot |

> NÃ£o envie evidÃªncias inventadas. As capturas devem ser feitas durante a apresentaÃ§Ã£o/execuÃ§Ã£o do projeto e podem ser salvas em `docs/evidencias/`.

## US03 — Plano Premium (Khevyn)

A US03 promove automaticamente o aluno do plano **BASICO** para **PREMIUM** quando o total de cursos concluídos chega a **12**. Na promoção, o aluno recebe **3 moedas**. Se já estiver Premium, novas conclusões não duplicam essa premiação.

O ciclo TDD foi registrado em commits separados: 	est(red), eat(green) e efactor(blue). Os testes de domínio cobrem o cenário de promoção, o limite anterior aos 12 cursos e a proteção contra moedas duplicadas.

A validação final deve ser feita com:

`ash
mvn clean verify
`

O relatório JaCoCo é gerado em 	arget/site/jacoco/index.html. O workflow .github/workflows/us03-jacoco.yml executa a mesma validação no GitHub Actions e publica o relatório como artefato jacoco-us03.
