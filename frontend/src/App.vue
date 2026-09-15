<script setup>
import { onMounted, ref } from 'vue'

// No Docker, o frontend é servido pelo próprio Spring Boot. Por isso a rota
// relativa funciona em qualquer máquina, sem depender de "localhost" fixo.
const api = '/api/alunos'
const alunos = ref([])
const nome = ref('')
const cursos = ref(5)
const mensagem = ref('')
<<<<<<< HEAD

async function carregar() {
  alunos.value = await (await fetch(api)).json()
}

async function cadastrar() {
  const resposta = await fetch(api, {
    method: 'POST',
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify({nome: nome.value, cursosDisponiveis: cursos.value})
  })
  if (!resposta.ok) return mensagem.value = 'Não foi possível cadastrar o aluno.'
  nome.value = ''
  mensagem.value = 'Aluno cadastrado com sucesso.'
  await carregar()
}

async function concluir(aluno, media) {
  await fetch(`${api}/${aluno.id}/cursos/conclusao`, {
    method: 'POST',
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify({media, concluido: true})
  })
  mensagem.value = `Conclusão com média ${media} registrada.`
  await carregar()
}

onMounted(carregar)
=======
const tipoMensagem = ref('sucesso')

function exibirMensagem(texto, tipo = 'sucesso') {
  mensagem.value = texto
  tipoMensagem.value = tipo
}

async function carregar() {
  const resposta = await fetch(api)
  if (!resposta.ok) throw new Error('Não foi possível carregar os alunos.')
  alunos.value = await resposta.json()
}

async function cadastrar() {
  try {
    const resposta = await fetch(api, { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({nome: nome.value, cursosDisponiveis: cursos.value}) })
    if (!resposta.ok) throw new Error()
    nome.value = ''
    exibirMensagem('Aluno cadastrado com sucesso.')
    await carregar()
  } catch {
    exibirMensagem('Não foi possível cadastrar o aluno.', 'erro')
  }
}

async function concluir(aluno, media) {
  try {
    const resposta = await fetch(`${api}/${aluno.id}/cursos/conclusao`, { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({media, concluido: true}) })
    if (!resposta.ok) throw new Error()
    exibirMensagem(`Conclusão com média ${media.toFixed(1)} registrada.`)
    await carregar()
  } catch {
    exibirMensagem('Não foi possível registrar a conclusão.', 'erro')
  }
}

onMounted(async () => {
  try {
    await carregar()
  } catch {
    exibirMensagem('Não foi possível carregar os alunos.', 'erro')
  }
})
>>>>>>> origin/main
</script>

<template>
  <main>
<<<<<<< HEAD
    <header>
      <p class="eyebrow">Educação continuada</p>
      <h1>Aprende+</h1>
      <p>Seu progresso transforma estudo em novas oportunidades.</p>
    </header>

    <section class="card">
      <h2>Cadastrar aluno</h2>
      <form @submit.prevent="cadastrar">
        <input v-model="nome" required placeholder="Nome do aluno">
        <input v-model.number="cursos" type="number" min="0" placeholder="Cursos disponíveis">
        <button>Cadastrar</button>
      </form>
    </section>

    <p v-if="mensagem" class="message">{{ mensagem }}</p>

    <section class="grid">
      <article v-for="aluno in alunos" :key="aluno.id" class="card aluno">
        <div>
          <p class="eyebrow">US01 + US03 — Gamificação</p>
          <h2>{{ aluno.nome }}</h2>
        </div>

        <div class="stats">
          <span><b>{{ aluno.cursosDisponiveis }}</b> cursos disponíveis</span>
          <span><b>{{ aluno.cursosConcluidos }}</b> cursos concluídos</span>
          <span><b>{{ aluno.plano }}</b> plano</span>
          <span><b>{{ aluno.moedas }}</b> moedas</span>
        </div>

        <div class="actions">
          <button @click="concluir(aluno, 8)">Concluir com 8,0</button>
          <button class="secondary" @click="concluir(aluno, 7)">Concluir com 7,0</button>
        </div>
      </article>
    </section>

    <p v-if="!alunos.length" class="empty">Cadastre um aluno para começar.</p>
=======
    <header><p class="eyebrow">Projeto acadêmico — US01</p><h1>Recompensa por conclusão de curso</h1><p>Cadastre o aluno e registre a média final para validar a regra de gamificação.</p></header>
    <section class="card">
      <h2>Cadastrar aluno</h2>
      <form @submit.prevent="cadastrar">
        <label>Nome do aluno<input v-model="nome" required placeholder="Ex.: João Engler"></label>
        <label>Cursos disponíveis<input v-model.number="cursos" type="number" min="0" placeholder="Ex.: 5"></label>
        <button>Cadastrar</button>
      </form>
    </section>
    <section class="rule"><b>Regra da US01:</b> ao concluir um curso com média maior que 7,0, o aluno recebe 3 novos cursos. Com média 7,0, não recebe recompensa.</section>
    <p v-if="mensagem" class="message" :class="tipoMensagem">{{ mensagem }}</p>
    <section class="grid"><article v-for="aluno in alunos" :key="aluno.id" class="card aluno"><div><h2>{{ aluno.nome }}</h2></div><div class="stats"><span><b>{{ aluno.cursosDisponiveis }}</b> cursos disponíveis</span></div><div class="actions"><button @click="concluir(aluno, 8)">Registrar média 8,0 (+3 cursos)</button><button class="secondary" @click="concluir(aluno, 7)">Registrar média 7,0 (sem recompensa)</button></div></article></section>
    <p v-if="!alunos.length" class="empty">Cadastre um aluno acima para começar a demonstração.</p>
>>>>>>> origin/main
  </main>
</template>
