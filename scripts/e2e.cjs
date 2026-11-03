// Chromium/Playwright opcionais ficam fora das dependências do produto.
// Execute contra o laboratório nativo ativo; cria dois alunos de teste.
const { chromium } = require(process.env.B1_PLAYWRIGHT_MODULE || 'playwright');
const assert = require('node:assert/strict');
const { randomUUID, createHash } = require('node:crypto');
const { execFileSync } = require('node:child_process');
const fs = require('node:fs');
const path = require('node:path');

const base = process.env.B1_E2E_URL || 'http://127.0.0.1:8080';
const out = path.resolve(process.env.B1_E2E_OUTPUT || '.runtime/e2e');
const fallbackOnly = process.argv.includes('--fallback');
const checks = [];

(async () => {
  fs.mkdirSync(out, { recursive: true });
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ viewport: { width: 1440, height: 1000 } });
  page.setDefaultTimeout(15000);
  const errors = [];
  page.on('pageerror', error => errors.push(error.message));
  async function action(route, click) {
    const [response] = await Promise.all([
      page.waitForResponse(r => new URL(r.url()).pathname === route && r.request().method() === 'POST'),
      click()
    ]);
    assert(response.ok(), `${route}: HTTP ${response.status()}`);
    return response.json();
  }
  async function recommend() {
    const [response] = await Promise.all([
      page.waitForResponse(r => new URL(r.url()).pathname === '/api/ia/recomendacoes'),
      page.getByRole('button', { name: 'Consultar cursos', exact: true }).click()
    ]);
    assert.equal(response.status(), 200);
    return response.json();
  }
  async function create(name) {
    await page.getByLabel('Nome do aluno', { exact: true }).fill(name);
    await page.getByLabel('Cursos disponíveis', { exact: true }).fill('5');
    const student = await action('/api/alunos', () => page.getByRole('button', { name: 'Cadastrar', exact: true }).click());
    await page.getByRole('heading', { name, exact: true }).waitFor();
    return student;
  }
  function card(name) {
    return page.locator('article.aluno').filter({ has: page.getByRole('heading', { name, exact: true }) });
  }
  async function conclude(student, course, mean) {
    const current = card(student.nome);
    await current.getByLabel('Código do curso').fill(course);
    await current.getByLabel('Média final').fill(String(mean));
    return action(`/api/alunos/${student.id}/cursos/conclusao`,
      () => current.getByRole('button', { name: 'Registrar conclusão' }).click());
  }
  async function rendered(student) {
    await page.waitForFunction(student => {
      const current = [...document.querySelectorAll('article.aluno')]
        .find(c => c.querySelector('h2').textContent === student.nome);
      if (!current) return false;
      const values = [...current.querySelectorAll('.stats b')].map(b => Number(b.textContent));
      return JSON.stringify(values) === JSON.stringify([
        student.cursosDisponiveis, student.cursosConcluidos, student.moedas, student.pontos
      ]) && current.textContent.includes(student.plano);
    }, student);
  }
  try {
    const response = await page.goto(base);
    assert.equal(response.status(), 200);
    await page.getByRole('heading', { name: 'Aprenda, conquiste e continue' }).waitFor();
    checks.push('interface servida pelo JAR, sem mock de rede');
    if (fallbackOnly) {
      const result = await recommend();
      assert.equal(result.degradado, true);
      await page.getByText('As recomendações estão temporariamente indisponíveis. Veja uma opção do catálogo.').waitFor();
      await page.getByPlaceholder('Como recebo recompensas?').fill('Como recebo recompensas?');
      const answer = await action('/api/ia/assistente', () => page.getByRole('button', { name: 'Perguntar', exact: true }).click());
      assert.equal(answer.degradado, true);
      await page.getByText('Assistente temporariamente indisponível. Consulte o material do curso.', { exact: true }).waitFor();
      assert((await page.request.get(base + '/api/ranking')).ok());
      checks.push('catálogo e assistente degradados no navegador com IA realmente parada', 'ranking continua respondendo');
      await page.screenshot({ path: path.join(out, 'fallback.png'), fullPage: true });
    } else {
      const suffix = randomUUID().slice(0, 6);
      const student = await create('E2E-' + suffix);
      assert.equal(student.cursosDisponiveis, 5);
      let result = await conclude(student, 'limite-7', 7);
      assert.equal(result.cursosDisponiveis, 5);
      assert.equal(result.cursosConcluidos, 1);
      assert.equal(result.pontos, 0);
      await rendered(result);
      checks.push('cadastro pelo formulário e média 7 sem liberar cursos');
      result = await conclude(student, 'aprovado-8', 8);
      assert.equal(result.cursosDisponiveis, 8);
      assert.equal(result.cursosAprovados, 1);
      assert(result.badges.includes('primeira-conclusao'));
      result = await conclude(student, 'aprovado-8', 8);
      assert.equal(result.cursosDisponiveis, 8);
      assert.equal(result.cursosConcluidos, 2);
      await rendered(result);
      checks.push('aprovação concede três cursos e badge; reenvio não duplica recompensa');
      for (let i = 3; i <= 12; i++) result = await conclude(student, 'premium-' + i, 7);
      assert.equal(result.plano, 'PREMIUM');
      assert.equal(result.moedas, 3);
      assert.equal(result.cursosConcluidos, 12);
      assert.equal(result.cursosDisponiveis, 8);
      assert.equal(result.pontos, 103);
      await rendered(result);
      const ranking = page.locator('tbody tr').filter({ hasText: student.nome });
      await ranking.waitFor();
      assert.match(await ranking.innerText(), /103/);
      result = await conclude(student, 'premium-13', 7);
      assert.equal(result.moedas, 3);
      await rendered(result);
      checks.push('Premium na 12ª conclusão, três moedas uma única vez e ranking com 103 pontos');
      await page.getByLabel('Área de interesse').fill('mqtt');
      assert.equal((await recommend()).degradado, false);
      await page.getByRole('listitem').filter({ hasText: 'IoT e MQTT' }).waitFor();
      await page.getByPlaceholder('Como recebo recompensas?').fill('Qual média libera recompensa?');
      const answer = await action('/api/ia/assistente', () => page.getByRole('button', { name: 'Perguntar', exact: true }).click());
      assert.equal(answer.degradado, false);
      assert(answer.fontes.includes('ac1-us01'));
      await page.getByText(/Fontes do material:.*ac1-us01/).waitFor();
      checks.push('recomendação e assistente com fonte via Core → FastAPI reais');
      await page.screenshot({ path: path.join(out, 'desktop.png'), fullPage: true });
      const [tenantResponse] = await Promise.all([
        page.waitForResponse(r => new URL(r.url()).pathname === '/api/alunos' && r.request().headers()['x-instituicao'] === 'instituicao-b'),
        page.getByRole('combobox').selectOption('instituicao-b')
      ]);
      assert(tenantResponse.ok());
      await page.getByRole('heading', { name: student.nome, exact: true }).waitFor({ state: 'hidden' });
      assert.equal(await page.locator('tbody tr').filter({ hasText: student.nome }).count(), 0);
      const other = await create('B-' + suffix);
      assert.equal(other.cursosDisponiveis, 5);
      const crossTenant = await page.request.post(base + `/api/alunos/${student.id}/cursos/conclusao`, {
        headers: { 'X-Instituicao': 'instituicao-b' }, data: { media: 8, concluido: true, cursoId: 'proibido' }
      });
      assert.equal(crossTenant.status(), 404);
      checks.push('troca de instituição isola alunos/ranking e rejeita acesso cruzado');
      await page.setViewportSize({ width: 390, height: 844 });
      assert(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth), 'Layout transborda horizontalmente no celular');
      const mobileResult = await conclude(other, 'mobile-8', 8);
      assert.equal(mobileResult.cursosDisponiveis, 8);
      await rendered(mobileResult);
      await page.screenshot({ path: path.join(out, 'mobile.png'), fullPage: true });
      checks.push('interface móvel de 390 px sem rolagem horizontal e conclusão pelo formulário');
    }
    assert.deepEqual(errors, [], 'Erros JavaScript no navegador');
    checks.push('nenhum erro JavaScript não tratado');
    const inputs = ['frontend/package-lock.json', 'frontend/src/App.vue', 'frontend/src/style.css', 'scripts/e2e.cjs'];
    let revision = null;
    try { revision = execFileSync('git', ['rev-parse', 'HEAD'], { encoding: 'utf8', stdio: ['ignore', 'pipe', 'ignore'] }).trim(); }
    catch { /* O pacote ZIP pode ser executado sem o histórico Git. */ }
    const report = {
      status: 'PASS', modo: fallbackOnly ? 'fallback' : 'interface', executado_utc: new Date().toISOString(),
      revisao_base: revision,
      navegador: 'Chromium ' + browser.version(), url: base, verificacoes: checks,
      codigo_sha256: Object.fromEntries(inputs.map(name => [name, createHash('sha256').update(fs.readFileSync(name)).digest('hex')]))
    };
    fs.writeFileSync(path.join(out, fallbackOnly ? 'fallback.json' : 'interface.json'), JSON.stringify(report, null, 2) + '\n');
    console.log(JSON.stringify(report, null, 2));
  } catch (error) {
    await page.screenshot({ path: path.join(out, 'falha-' + (fallbackOnly ? 'fallback' : 'interface') + '.png'), fullPage: true }).catch(() => {});
    throw error;
  } finally {
    await browser.close();
  }
})().catch(error => { console.error(error); process.exitCode = 1; });
