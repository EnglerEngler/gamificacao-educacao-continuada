# BDD — escrito pelos integrantes identificados no README.
Funcionalidade: Recompensas da educação continuada gamificada

  Cenário: Desbloquear cursos após conclusão com boa média
    Dado que o aluno possui 5 cursos disponíveis
    Quando concluir um curso com média final 8,0
    Então o sistema deve liberar 3 novos cursos
    E o aluno deve visualizar 8 cursos disponíveis

  Cenário: Não desbloquear cursos com média insuficiente
    Dado que o aluno possui 5 cursos disponíveis
    Quando concluir um curso com média final 7,0
    Então o sistema não deve desbloquear cursos

  Cenário: Recompensar o aluno mais participativo do fórum
    Dado que um aluno foi identificado como o mais ativo no fórum no mês
    Quando a premiação mensal for processada
    Então o aluno deve receber 1 curso adicional

  Cenário: Migrar aluno para Premium
    Dado que o aluno concluiu 11 cursos
    Quando concluir mais um curso
    Então o plano deve ser atualizado para Premium
    E o aluno deve receber 3 moedas
