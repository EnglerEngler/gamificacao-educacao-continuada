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

**Escopo atual da entrega:** a US01 permanece como implementação central do grupo. A **US03 foi implementada por Khevyn** em contribuição individual, incluindo promoção automática para Premium ao atingir 12 cursos concluídos, crédito de três moedas e proteção contra duplicidade da premiação. A US02 permanece reservada para implementação individual de Eduardo.

## BDD: cenários e responsáveis

Os cenários estão em [docs/gamificacao.feature](docs/gamificacao.feature). A identificação a seguir atende à atribuição individual dos BDDs.

| Responsável | User story | Cenários BDD redigidos |
| --- | --- | --- |
| João Victor Cardoso Engler Rizzi de Araujo — RA 236602 | US01 | Desbloqueio com média maior que 7; média igual a 7 não libera cursos; curso em andamento não libera cursos. |
| Eduardo Bismara Nastri — RA 211466 | US02 | A redigir e implementar: premiação mensal para aluno mais participativo do fórum. |
| Khevyn Henrique Guedes T. Alves — RA 223761 | US03 | Implementado: promoção para Premium ao atingir 12 cursos concluídos, crédito de três moedas e proteção contra duplicidade para aluno já Premium. |

Exemplo do cenário principal (US01):

```gherkin
Cenário: Desbloquear cursos após conclusão com boa média
  Dado que o aluno possui 5 cursos disponíveis
  Quando concluir um curso com média final 8,0
  Então o sistema deve liberar 3 novos cursos
  E o aluno deve visualizar 8 cursos disponíveis
```
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
| **BDD** | Arquivo `docs/gamificacao.feature` e planilha anexada | US01 e US03 implementadas; US02 aguarda o responsável |
| **RED, GREEN e BLUE** | Histórico de commits/tags e screenshots abaixo | Concluído para US01; ciclo individual da US03 registrado em commits próprios |
| **Testes e cobertura** | Relatório JaCoCo (`target/site/jacoco/index.html`) | Validado: 100% do domínio; US03 também validada no GitHub Actions |

### Fase RED
Testes criados antes da implementação falhando, garantindo que a regra de negócio é necessária.
![TDD RED.png](docs/evidencias/TDD%20RED.png)

###  Fase GREEN
Implementação mínima para fazer os testes passarem.
![TDD Green.png](docs/evidencias/TDD%20Green.png)

###  Fase BLUE e Cobertura (JaCoCo)
Refatoração do código (Clean Code e remoção de Magic Numbers) mantendo os testes passando, junto com a comprovação de 100% de cobertura pelo JaCoCo.
![TDD BLUE.png](docs/evidencias/TDD%20BLUE.png)
![Relatório JaCoCo](docs/evidencias/Jacoco.png)

---
Fase de Integração

Demonstração da User Story 01 validada ponta a ponta, processando as regras de negócio via API integrada à interface.

1. Estado Inicial:
   Aluno cadastrado e com saldo original de 1 curso disponível.
![Aluno-registrado.png](docs/evidencias/Aluno-registrado.png)

2. Sucesso (Média > 7.0):
   Após registrar a conclusão de um curso com média 8,0, a regra de negócio libera 3 novos cursos. 1 saldo é atualizado corretamente para 4.
![Aluno-media-8.png](docs/evidencias/Aluno-media-8.png)

3. Trava de Segurança (Média <= 7.0):
   Ao registrar a conclusão de outro curso com média 7,0, o sistema recusa a recompensa, mantendo o saldo de cursos intacto (4).
![Aluno-media-7.png](docs/evidencias/Aluno-media-7.png)

---

## US03 — Plano Premium (Khevyn)

A US03 promove automaticamente o aluno do plano **BASICO** para **PREMIUM** quando o total de cursos concluídos chega a **12**. Na promoção, o aluno recebe **3 moedas**. Se já estiver Premium, novas conclusões não duplicam essa premiação.

O ciclo TDD da US03 foi registrado em commits separados:

- `test(red): especifica plano Premium da US03`
- `feat(green): implementa plano Premium e moedas da US03`
- `refactor(blue): consolida regras e cobertura da US03`

Os testes de domínio cobrem o cenário de promoção, o limite anterior aos 12 cursos e a proteção contra moedas duplicadas. A validação final foi executada pelo GitHub Actions com:

```bash
mvn clean verify
```

O relatório JaCoCo confirmou **100% de cobertura de instruções e 100% de branches** no pacote `domain`. O workflow `.github/workflows/us03-jacoco.yml` publica o relatório como artefato `jacoco-us03`.

A contribuição individual da US03 está registrada no [Pull Request #1](https://github.com/EnglerEngler/gamificacao-educacao-continuada/pull/1).

## Como executar o projeto (Docker)

O projeto usa o mesmo padrão apresentado em aula: aplicação Spring Boot, PostgreSQL e pgAdmin em containers separados. O Dockerfile compila o frontend Vue e o backend dentro da imagem, portanto não é necessário executar Maven ou npm antes.

1. Certifique-se de que o Docker Desktop esteja aberto e que a integração WSL esteja ativa.
2. Na raiz do projeto, execute:

```bash
docker compose up --build
```

3. Aguarde o log da aplicação informar que iniciou na porta 8080. O Compose espera o healthcheck do PostgreSQL antes de iniciar a API.
![Docker.jpeg](docs/evidencias/Docker.jpeg)
### Acessos Docker

- Aplicação e frontend Vue: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- PostgreSQL: porta `5432`, banco/usuário/senha `gamificacao`
- pgAdmin: `http://127.0.0.1:5050` (ou `http://localhost:5050`)
  - e-mail: `admin@gamificacao.com`
  - senha: `admin`

No pgAdmin, registre o servidor com host `postgres`, porta `5432`, banco `gamificacao`, usuário `gamificacao` e senha `gamificacao`.

Para encerrar os containers:

```bash
docker compose down
```

Para apagar também os dados do banco e recomeçar do zero:

```bash
docker compose down -v
```

## Executar localmente com H2

```bash
mvn spring-boot:run
```

- API: `http://localhost:8080/api/alunos`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Console H2: `http://localhost:8080/h2-console`

Para rodar o frontend Vue fora do Docker:

```bash
cd frontend
npm install
npm run dev
```
