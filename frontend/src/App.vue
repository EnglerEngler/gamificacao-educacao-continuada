<script setup>
import { onMounted, ref } from 'vue'

// No Docker, o frontend é servido pelo próprio Spring Boot. Por isso a rota
// relativa funciona em qualquer máquina, sem depender de "localhost" fixo.
const api = '/api/alunos'
const alunos = ref([])
const nome = ref('')
const cursos = ref(5)
const mensagem = ref('')
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
</script>

<template>
  <main>
    <header><p class="eyebrow">Educação continuada</p><h1>Aprende+</h1><p>Seu progresso transforma estudo em novas oportunidades.</p></header>
    <section class="card"><h2>Cadastrar aluno</h2><form @submit.prevent="cadastrar"><input v-model="nome" required placeholder="Nome do aluno"><input v-model.number="cursos" type="number" min="0" placeholder="Cursos disponíveis"><button>Cadastrar</button></form></section>
    <p v-if="mensagem" class="message" :class="tipoMensagem">{{ mensagem }}</p>
    <section class="grid"><article v-for="aluno in alunos" :key="aluno.id" class="card aluno"><div><p class="eyebrow">US01 — Educação continuada</p><h2>{{ aluno.nome }}</h2></div><div class="stats"><span><b>{{ aluno.cursosDisponiveis }}</b> cursos disponíveis</span></div><div class="actions"><button @click="concluir(aluno, 8)">Concluir com 8,0</button><button class="secondary" @click="concluir(aluno, 7)">Concluir com 7,0</button></div></article></section>
    <p v-if="!alunos.length" class="empty">Cadastre um aluno para começar.</p>
  </main>
</template>
