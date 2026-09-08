# Aprende+ — Educação Continuada Gamificada

Projeto acadêmico desenvolvido para demonstrar **ATDD**: critérios de aceitação em BDD guiando a implementação testada em TDD. A plataforma recompensa o aluno por concluir cursos, colaborar no fórum e atingir o plano Premium.

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

## Tecnologias

- Java 21, Spring Boot, Spring Web, Spring Data JPA
- H2 (execução local) e PostgreSQL (container)
- Swagger/OpenAPI, em `http://localhost:8080/swagger-ui.html`
- JUnit 5 e JaCoCo
- Vue 3 + Vite
- Docker e Docker Compose

## Estrutura e camadas

```text
src/main/java/br/edu/fatec/gamificacao/
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

O relatório fica em `target/site/jacoco/index.html`. O JaCoCo exige 100% das linhas do pacote `domain`.

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
