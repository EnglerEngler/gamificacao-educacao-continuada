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

  Cenário: Atualizar assinatura para Premium ao concluir o décimo segundo curso
    Dado que o aluno está no plano Básico com 11 cursos concluídos e 0 moedas
    Quando concluir o décimo segundo curso
    Então o plano do aluno deve ser atualizado para Premium
    E o aluno deve receber 3 moedas

  Cenário: Permanecer no plano Básico antes de concluir 12 cursos
    Dado que o aluno está no plano Básico com 10 cursos concluídos e 0 moedas
    Quando concluir mais um curso
    Então o plano do aluno deve continuar Básico
    E o aluno deve continuar com 0 moedas

  Cenário: Não duplicar moedas de aluno que já é Premium
    Dado que o aluno já está no plano Premium com 12 cursos concluídos e 3 moedas
    Quando concluir outro curso
    Então o plano deve continuar Premium
    E o aluno deve continuar com 3 moedas
