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

  Cenário: Não desbloquear cursos que ainda estão em andamento
    Dado que o aluno possui 5 cursos disponíveis
    Quando registrar média final 9,0 para um curso não concluído
    Então o sistema não deve desbloquear cursos
    E o aluno deve continuar com 5 cursos disponíveis
