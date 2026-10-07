<script setup>
import { onMounted, ref } from 'vue'
const alunos = ref([]), ranking = ref([]), tenant = ref('ac1'), nome = ref(''), cursos = ref(5)
const mensagem = ref(''), tipoMensagem = ref('sucesso'), cursosPorAluno = ref({}), medias = ref({})
const interesse = ref('programacao'), recomendacoes = ref(null), pergunta = ref(''), resposta = ref(null)
function exibir(texto, tipo = 'sucesso') { mensagem.value = texto; tipoMensagem.value = tipo }
async function api(path, body) {
  const response = await fetch('/api' + path, {
    method: body ? 'POST' : 'GET',
    headers: { 'Content-Type': 'application/json', 'X-Instituicao': tenant.value },
    ...(body ? { body: JSON.stringify(body) } : {})
  })
  if (!response.ok) throw new Error('Não foi possível concluir a operação.')
  return response.json()
}
async function carregar() {
  try { alunos.value = await api('/alunos'); ranking.value = await api('/ranking?limite=10') }
  catch (e) { exibir(e.message, 'erro') }
}
async function cadastrar() {
  try {
    await api('/alunos', { nome: nome.value, cursosDisponiveis: cursos.value })
    nome.value = ''; exibir('Aluno cadastrado com sucesso.'); await carregar()
  } catch (e) { exibir(e.message, 'erro') }
}
async function concluir(aluno) {
  try {
    await api('/alunos/' + aluno.id + '/cursos/conclusao', {
      media: Number(medias.value[aluno.id] ?? 8), concluido: true,
      cursoId: cursosPorAluno.value[aluno.id] || 'curso-1', eventoId: crypto.randomUUID()
    })
    exibir('Conclusão registrada. Reenviar o mesmo código de curso mantém a recompensa original.')
    await carregar()
  } catch (e) { exibir(e.message, 'erro') }
}
async function recomendar() {
  try { recomendacoes.value = await api('/ia/recomendacoes?interesse=' + encodeURIComponent(interesse.value)) }
  catch (e) { exibir(e.message, 'erro') }
}
async function perguntar() {
  try { resposta.value = await api('/ia/assistente', { pergunta: pergunta.value }) }
  catch (e) { exibir(e.message, 'erro') }
}
onMounted(carregar)
</script>

<template>
  <main>
    <header>
      <p class="eyebrow">Aprende+ — Educação continuada</p>
      <h1>Aprenda, conquiste e continue</h1>
      <p>Acompanhe suas recompensas, consulte o ranking e encontre seu próximo curso.</p>
      <label>Instituição<select v-model="tenant" @change="carregar"><option value="ac1">Turma AC1</option><option value="instituicao-b">Instituição B</option></select></label>
    </header>
    <section class="card">
      <h2>Cadastrar aluno</h2>
      <form @submit.prevent="cadastrar">
        <label>Nome do aluno<input v-model="nome" required maxlength="120" placeholder="Ex.: João Engler"></label>
        <label>Cursos disponíveis<input v-model.number="cursos" type="number" min="0" required></label>
        <button>Cadastrar</button>
      </form>
    </section>
    <section class="rule"><b>Recompensa:</b> concluir um curso com média maior que 7,0 libera três novos cursos. Ao atingir 12 conclusões, o plano passa a Premium e recebe três moedas, independentemente da média.</section>
    <p v-if="mensagem" class="message" :class="tipoMensagem" role="status">{{ mensagem }}</p>
    <section class="grid">
      <article v-for="aluno in alunos" :key="aluno.id" class="card aluno">
        <h2>{{ aluno.nome }}</h2>
        <div class="stats"><span><b>{{ aluno.cursosDisponiveis }}</b> cursos disponíveis</span><span><b>{{ aluno.cursosConcluidos }}</b> cursos concluídos</span><span><b>{{ aluno.moedas }}</b> moedas</span><span><b>{{ aluno.pontos }}</b> pontos · {{ aluno.plano }}</span></div>
        <p v-if="aluno.badges.length">Conquista: primeira conclusão aprovada 🏅</p>
        <form @submit.prevent="concluir(aluno)">
          <label>Código do curso<input v-model="cursosPorAluno[aluno.id]" placeholder="curso-1" maxlength="120"></label>
          <label>Média final<input v-model.number="medias[aluno.id]" type="number" min="0" max="10" step="0.1" placeholder="8"></label>
          <button>Registrar conclusão</button>
        </form>
      </article>
    </section>
    <p v-if="!alunos.length" class="empty">Cadastre um aluno para começar.</p>
    <section class="card">
      <h2>Ranking da instituição</h2>
      <table v-if="ranking.length"><thead><tr><th>Posição</th><th>Aluno</th><th>Pontos</th></tr></thead>
        <tbody><tr v-for="(item, index) in ranking" :key="item.alunoId"><td>{{ index + 1 }}</td><td>{{ item.nome }}</td><td>{{ item.pontos }}</td></tr></tbody>
      </table>
      <p v-else>O ranking aparecerá após o primeiro cadastro.</p>
    </section>
    <section class="grid">
      <article class="card">
        <h2>Seu próximo curso</h2>
        <form @submit.prevent="recomendar"><label>Área de interesse<input v-model="interesse" maxlength="200" required></label><button>Consultar cursos</button></form>
        <template v-if="recomendacoes"><p v-if="recomendacoes.degradado">As recomendações estão temporariamente indisponíveis. Veja uma opção do catálogo.</p><ul><li v-for="curso in recomendacoes.cursos" :key="curso">{{ curso }}</li></ul></template>
      </article>
      <article class="card">
        <h2>Assistente de estudos</h2>
        <form @submit.prevent="perguntar"><label>Sua dúvida<input v-model="pergunta" maxlength="1000" required placeholder="Como recebo recompensas?"></label><button>Perguntar</button></form>
        <p v-if="resposta">{{ resposta.resposta }}</p>
        <small v-if="resposta?.fontes?.length">Fontes do material: {{ resposta.fontes.join(', ') }}</small>
      </article>
    </section>
  </main>
</template>
