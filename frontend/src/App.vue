<script setup>
import { onMounted, ref } from 'vue'
const api = 'http://localhost:8080/api/alunos'
const alunos = ref([])
const nome = ref('')
const cursos = ref(5)
const mensagem = ref('')
async function carregar() { alunos.value = await (await fetch(api)).json() }
async function cadastrar() {
  const resposta = await fetch(api, { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({nome: nome.value, cursosDisponiveis: cursos.value}) })
  if (!resposta.ok) return mensagem.value = 'Não foi possível cadastrar o aluno.'
  nome.value = ''; mensagem.value = 'Aluno cadastrado com sucesso.'; await carregar()
}
async function concluir(aluno, media) {
  await fetch(`${api}/${aluno.id}/cursos/conclusao`, { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({media, concluido: true}) })
  mensagem.value = `Conclusão com média ${media} registrada.`; await carregar()
}
async function premiarForum(aluno) {
  await fetch(`${api}/${aluno.id}/forum/premiacao`, {method:'POST'}); mensagem.value = 'Prêmio do fórum aplicado.'; await carregar()
}
onMounted(carregar)
</script>

<template>
  <main>
    <header><p class="eyebrow">Educação continuada</p><h1>Aprende+</h1><p>Seu progresso transforma estudo em novas oportunidades.</p></header>
    <section class="card"><h2>Cadastrar aluno</h2><form @submit.prevent="cadastrar"><input v-model="nome" required placeholder="Nome do aluno"><input v-model.number="cursos" type="number" min="0" placeholder="Cursos disponíveis"><button>Cadastrar</button></form></section>
    <p v-if="mensagem" class="message">{{ mensagem }}</p>
    <section class="grid"><article v-for="aluno in alunos" :key="aluno.id" class="card aluno"><div><p class="eyebrow">Plano {{ aluno.plano }}</p><h2>{{ aluno.nome }}</h2></div><div class="stats"><span><b>{{ aluno.cursosDisponiveis }}</b> cursos disponíveis</span><span><b>{{ aluno.cursosConcluidos }}</b> concluídos</span><span><b>{{ aluno.moedas }}</b> moedas</span></div><div class="actions"><button @click="concluir(aluno, 8)">Concluir com 8,0</button><button class="secondary" @click="concluir(aluno, 7)">Concluir com 7,0</button><button class="secondary" @click="premiarForum(aluno)">Premiar fórum</button></div></article></section>
    <p v-if="!alunos.length" class="empty">Cadastre um aluno para começar.</p>
  </main>
</template>
