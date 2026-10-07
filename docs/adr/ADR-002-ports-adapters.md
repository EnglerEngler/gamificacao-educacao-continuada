# ADR-002 — Isolar a recompensa por Ports & Adapters

Status: aceito para B1. Desafio 2. RF02. ASR-04/05/06.

## Contexto

A AC1 já possui a regra pura Aluno.concluirCurso, mas AlunoService importa diretamente AlunoEntity e Spring Data repository. A contribuição US03 acrescenta Premium/moedas; o B1 acrescenta badge, ranking e eventos; substituir a persistência não deve exigir reescrever regra de negócio.

RNFs: manutenibilidade, testabilidade, modificabilidade e facilidade de evolução. Selecionamos os três primeiros; evolução é consequência das fronteiras estabelecidas.

RPCs: Core continua Spring Boot; regra AC1 reaproveitada; proibição de reescrita integral para aplicar padrão.

## Alternativas

| Alternativa | Benefício | Custo |
| --- | --- | --- |
| A — Estrutura AC1 atual | Poucos arquivos; domínio já parcialmente puro | Caso de uso depende de tipos JPA e repository técnico; teste exige mock desses tipos. |
| B — Ports & Adapters aplicado ao caso de uso | Mesmo contrato em fake/JPA; domínio sem framework | Interface, mapeamento e testes de adapter adicionais. |
| C — Clean/Onion integral em todo o produto | Fronteiras uniformes | Reorganização maior que a hipótese exigida; custo desproporcional ao pequeno legado. |

## Decisão

Escolher B. Aluno é o agregado puro com aprovação > 7,0, três cursos, primeira conquista e prêmio Premium uma única vez. AlunoStore é porta de saída definida na aplicação; JpaAlunoAdapter a implementa e mapeia entidades. ConcluirCurso e RegistrarEvento são APIs públicas utilizadas entre módulos. O framework continua nas bordas de aplicação/adapter.

A dependência invertida é **caso de uso → interface AlunoStore ← persistência JPA**, substituindo caso de uso → implementação técnica. DI do Spring monta o grafo; DI sozinha não garante a inversão.

DDD ajuda a nomear agregado, invariantes e capacidades. Hexagonal fornece ports/adapters; Clean/Onion compartilham a direção de dependência. São ideias combináveis; não estamos declarando adoção integral de todos esses padrões nem criando um bounded context distribuído por classe.

## Consequências e evidência

Os mesmos testes de caso de uso são herdados por FakeAdapterContractTest e JpaAdapterContractTest. Testes cobrem aprovação/limite, reenvio e isolamento de instituição. Domain tests não inicializam Spring e ArchUnit proíbe dependência técnica no domínio. [Relatório](../evidencias/b1/desafio2/testes.json).

O fake não oferece as garantias de transação/lock do JPA; o teste concorrente e as falhas transacionais são verificados separadamente no adapter real. O contrato comum cobre semântica funcional.

Concessão não é repetida quando a mesma instituição/aluno/curso reaparece. O bloqueio de aluno serializa atualizações e a constraint do banco protege a chave. Os novos campos opcionais preservam payload da AC1; chamadas legadas sem cursoId nem eventoId representam nova operação a cada chamada e não têm idempotência de identidade de curso.

## C4 e critério de aceitação

[C4 Component](../arquitetura/diagramas/d2-component.mmd), contratos e cobertura 100% do domínio. Critério: trocar persistência sem alterar classe de domínio/regra e manter testes da AC1.

Revisar mapeamentos/porta se novas invariantes exigirem outro agregado; evitar interfaces genéricas para toda classe sem necessidade.


## Continuidade com US03

A main ef33fa7 foi integrada preservando a contribuição de Khevyn: toda conclusão incrementa cursosConcluidos e pode atingir Premium, independentemente da média. CursosAprovados controla apenas novos cursos, badge e pontuação, mantendo > 7,0. Plano e moedas persistidos são reaproveitados, sem promover novamente quem já está Premium. Os testes originais de US03 foram movidos para o novo pacote e complementados com o limite de média 7 no décimo segundo curso.
