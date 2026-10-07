# Fontes e continuidade com a AC1

Os três arquivos originais recebidos estão preservados nesta pasta:

- `Exercicio_B1_Architecture_Lab_6_Desafios_Reavaliado.docx`: enunciado e rubrica.
- `B1_Desafio1_Monolito_Camadas.zip`: exemplo de estrutura horizontal.
- `B1_Desafio1_Monolito_Modular.zip`: exemplo de agrupamento por capacidade.

A base de implementação é o próprio Aprende+ da AC1 (commit b31abd3), para cumprir reaproveitamento do legado. Os ZIPs contêm as mesmas 37 classes após desconsiderar package/import; a versão modular ainda tem acesso direto à persistência de outros módulos e imports ausentes de Usuario/Curso em Matricula. Não foram tratados como arquitetura pronta.

Diferença semântica preservada: a AC1 recompensa média **maior que 7,0**; o exemplo de matrícula recompensa >= 7,0. A regra do produto foi mantida e testada.

Documentação primária consultada na implementação:

- [Spring Boot Actuator / métricas](https://docs.spring.io/spring-boot/reference/actuator/metrics.html)
- [FastAPI / testes](https://fastapi.tiangolo.com/tutorial/testing/)
- [Mosquitto / configuração, ACL e persistência](https://mosquitto.org/man/mosquitto-conf-5.html)
- [Jenkins / sintaxe de Pipeline](https://www.jenkins.io/doc/book/pipeline/syntax/)
- [Jenkins / Docker em Pipeline](https://www.jenkins.io/doc/book/pipeline/docker/)

As versões são do laboratório reproduzível, sem afirmação de serem as versões mais recentes.


A evolução da main ef33fa7 (PR #1 de Khevyn) também foi integrada; cursos concluídos para Premium e aprovados para recompensa/ranking têm contadores distintos. A snapshot inicial b31abd3 continua intacta para comparação estrutural do D1.
