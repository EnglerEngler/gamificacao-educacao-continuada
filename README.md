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

Os cenários de fórum (US02) e migração Premium (US03) também foram implementados e testados no domínio.

## BDD: cenários e responsáveis

Os cenários estão em [docs/gamificacao.feature](docs/gamificacao.feature). A identificação a seguir atende à atribuição individual dos BDDs.

| Responsável | User story | Cenários BDD redigidos |
| --- | --- | --- |
| João Victor Cardoso Engler Rizzi de Araujo — RA 236602 | US01 | Desbloqueio com média maior que 7; média igual a 7 não libera cursos; curso em andamento não libera cursos. |
| Eduardo Bismara Nastri — RA 211466 | US02 | Premiação mensal para aluno mais participativo do fórum. |
| Khevyn Henrique Guedes T. Alves — RA 223761 | US03 | Mudança para plano Premium ao atingir 12 cursos e crédito de três moedas. |

Exemplo do cenário principal (US01):

```gherkin
Cenário: Desbloquear cursos após conclusão com boa média
  Dado que o aluno possui 5 cursos disponíveis
  Quando concluir um curso com média final 8,0
  Então o sistema deve liberar 3 novos cursos
  E o aluno deve visualizar 8 cursos disponíveis
```

Guias individuais de contribuição: [Khevyn](docs/GUIA-KHEVYN.md) e [Eduardo](docs/GUIA-EDUARDO.md).

## Tecnologias

- Java 21, Spring Boot, Spring Web, Spring Data JPA
- H2 (execução local) e PostgreSQL (container)
- Swagger/OpenAPI, em `http://localhost:8080/swagger-ui.html`
- JUnit 5 e JaCoCo
- Vue 3 + Vite
- Docker e Docker Compose

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
- **BLUE:** refatorar e complementar a regra (curso em andamento, média limite, fórum e Premium), mantendo todos os testes verdes e cobertura de linhas do pacote `domain` em 100%.

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

## Executar localmente

Backend com H2:

```bash
mvn spring-boot:run
```

- API: `http://localhost:8080/api/alunos`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Console H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:gamificacao`
  - usuário: `sa`; senha vazia.

Frontend:

```bash
cd frontend
npm install
npm run dev
```

Abra `http://localhost:5173`.

## Executar com Docker e PostgreSQL

Primeiro empacote a aplicação:

```bash
mvn clean package
docker compose up --build
```

O PostgreSQL estará em `localhost:5432`, com banco, usuário e senha `gamificacao`. A aplicação sobe no perfil `postgres` em `http://localhost:8080`.

O pgAdmin estará em `http://localhost:5050` (login `admin@gamificacao.local`, senha `admin`). No pgAdmin, cadastre o host `postgres`, porta `5432`, usuário `gamificacao` e senha `gamificacao`.

> Para a evidência, execute os dois modos (H2 local e Docker/PostgreSQL) e anexe screenshots do Swagger, console H2, containers em execução e tela do front ao envio no Canvas.

## Endpoints

| Método | Rota | Finalidade |
| --- | --- | --- |
| POST | `/api/alunos` | Cadastra aluno |
| GET | `/api/alunos` | Lista alunos |
| POST | `/api/alunos/{id}/cursos/conclusao` | Registra média e conclusão do curso |
| POST | `/api/alunos/{id}/forum/premiacao` | Concede curso por participação no fórum |

Exemplo para criar aluno:

```json
{ "nome": "João Victor", "cursosDisponiveis": 5 }
```

Exemplo para registrar uma conclusão com recompensa:

```json
{ "media": 8.0, "concluido": true }
```

## Evidências para a entrega

| Evidência exigida | Como gerar | Situação |
| --- | --- | --- |
| BDD | Arquivo `docs/gamificacao.feature` e planilha anexada | Incluída no repositório |
| RED, GREEN e BLUE | Histórico de commits/tags e screenshots da IDE | Registrar antes da postagem no Canvas |
| Testes e cobertura | `mvn clean verify`; abrir `target/site/jacoco/index.html` | Validado localmente: 8 testes e regra de 100% do domínio atendida |
| Swagger | Abrir `/swagger-ui.html` com a API em execução | Capturar screenshot |
| H2 | Abrir `/h2-console` com a API em execução | Capturar screenshot |
| PostgreSQL e pgAdmin | `mvn package` e `docker compose up --build`; abrir `localhost:5050` | Capturar screenshot quando Docker estiver disponível |
| Frontend Vue | `cd frontend && npm run dev` | Capturar screenshot |

> Não envie evidências inventadas. As capturas devem ser feitas durante a apresentação/execução do projeto e podem ser salvas em `docs/evidencias/`.
