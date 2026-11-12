const UFS = ['AC', 'AL', 'AM', 'AP', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MG', 'MS', 'MT', 'PA', 'PB',
    'PE', 'PI', 'PR', 'RJ', 'RN', 'RO', 'RR', 'RS', 'SC', 'SE', 'SP', 'TO'];
const MESES = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
const CORES = ['#c2410c', '#2563eb', '#16a34a', '#7c3aed', '#0891b2', '#a16207', '#db2777'];

const $ = (id) => document.getElementById(id);
const fmt = (n) => Number(n).toLocaleString('pt-BR');
const fmtMs = (n) => Number(n).toLocaleString('pt-BR', {maximumFractionDigits: 2});
const esc = (s) => String(s).replace(/[&<>"]/g, c => ({'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;'}[c]));
const graficos = {};
let paginaAtual = 0;

async function api(url, opcoes) {
    const resp = await fetch(url, opcoes);
    const corpo = await resp.json().catch(() => null);
    if (!resp.ok) throw new Error(corpo?.erro || 'Erro ' + resp.status);
    return corpo;
}

async function executar(fn) {
    document.body.classList.add('ocupado');
    try {
        await fn();
    } catch (e) {
        alert(e.message);
    } finally {
        document.body.classList.remove('ocupado');
    }
}

function parametros(obj) {
    const p = new URLSearchParams();
    for (const [k, v] of Object.entries(obj)) {
        if (v !== '' && v != null) p.append(k, v);
    }
    return p;
}

function grafico(id, config) {
    graficos[id]?.destroy();
    graficos[id] = new Chart($(id), config);
}

function kpi(rotulo, valor) {
    return `<div class="kpi"><span>${esc(rotulo)}</span><b>${valor}</b></div>`;
}

function trocarAba(nome) {
    document.querySelectorAll('nav button').forEach(b => b.classList.toggle('ativa', b.dataset.aba === nome));
    document.querySelectorAll('main > section').forEach(s => s.hidden = s.id !== nome);
    if (nome === 'painel') executar(carregarPainel);
}

function preencher(seletor, valores) {
    document.querySelectorAll(seletor).forEach(sel => {
        const atual = sel.value;
        sel.innerHTML = '<option value="">Todos</option>' + valores.map(v => `<option>${esc(v)}</option>`).join('');
        sel.value = valores.map(String).includes(atual) ? atual : '';
    });
}

async function atualizarStatus() {
    const s = await api('/api/dados');
    $('status').textContent = s.total ? `${fmt(s.total)} focos carregados` : 'Nenhum dado carregado';
    $('tabArquivos').innerHTML = s.arquivos.map(a =>
        `<tr><td>${esc(a.nome)}</td><td>${fmt(a.registros)}</td><td>${fmt(a.invalidas)}</td></tr>`).join('')
        || '<tr><td colspan="3">Nenhum arquivo carregado.</td></tr>';
    $('dicaLimite').textContent = `Bubble, Selection e Insertion Sort (O(n²)) só rodam até ${fmt(s.limiteQuadratico)} `
        + 'registros. Todos os resultados são conferidos com o Merge Sort.';
    preencher('.filtroAno', s.anos);
    preencher('.filtroBioma', s.biomas);
}

$('formInpe').addEventListener('submit', (e) => {
    e.preventDefault();
    const anos = [...document.querySelectorAll('input[name=ano]:checked')].map(c => c.value);
    if (!anos.length) return alert('Selecione ao menos um ano.');
    const qs = new URLSearchParams({uf: $('uf').value});
    anos.forEach(a => qs.append('anos', a));
    executar(async () => {
        const arquivos = await api('/api/dados/inpe?' + qs, {method: 'POST'});
        alert(fmt(arquivos.reduce((t, a) => t + a.registros, 0)) + ' focos carregados.');
        await atualizarStatus();
    });
});

$('formUpload').addEventListener('submit', (e) => {
    e.preventDefault();
    const dados = new FormData();
    dados.append('arquivo', $('arquivo').files[0]);
    executar(async () => {
        const a = await api('/api/dados/upload', {method: 'POST', body: dados});
        alert(`${a.nome}: ${fmt(a.registros)} registros (${fmt(a.invalidas)} linhas inválidas).`);
        $('arquivo').value = '';
        await atualizarStatus();
    });
});

$('btnLimpar').addEventListener('click', () => {
    if (!confirm('Remover todos os dados carregados?')) return;
    executar(async () => {
        await fetch('/api/dados', {method: 'DELETE'});
        await atualizarStatus();
    });
});

function filtrosOrdenacao() {
    return {
        criterio: $('oCriterio').value,
        algoritmo: $('oAlgoritmo').value,
        decrescente: $('oOrdem').value,
        ano: $('oAno').value,
        bioma: $('oBioma').value,
        amostra: $('oAmostra').value
    };
}

async function ordenar(pagina) {
    const r = await api('/api/ordenar?' + parametros({...filtrosOrdenacao(), pagina, tamanho: 50}));
    const m = r.resultado;
    paginaAtual = r.pagina;
    $('metricas').innerHTML = kpi('Algoritmo', esc(m.algoritmo))
        + kpi('Registros', fmt(m.tamanho))
        + kpi('Comparações', fmt(m.comparacoes))
        + kpi('Trocas', fmt(m.trocas))
        + kpi('Movimentações', fmt(m.movimentacoes))
        + kpi('Total de operações', fmt(m.totalOperacoes))
        + kpi('Tempo', fmtMs(m.tempoMs) + ' ms');
    const inicio = r.pagina * 50;
    $('tabOrdenado').innerHTML = r.itens.map((f, i) => `<tr><td>${fmt(inicio + i + 1)}</td>
        <td>${f.dataHora.replace('T', ' ')}</td><td>${esc(f.municipio)}</td><td>${esc(f.bioma)}</td>
        <td>${f.latitude.toFixed(4)}</td><td>${f.longitude.toFixed(4)}</td></tr>`).join('');
    $('pagInfo').textContent = `Página ${fmt(r.pagina + 1)} de ${fmt(r.totalPaginas)}`;
    $('pagAnterior').disabled = r.pagina === 0;
    $('pagProxima').disabled = r.pagina + 1 >= r.totalPaginas;
}

$('formOrdenar').addEventListener('submit', (e) => {
    e.preventDefault();
    executar(() => ordenar(0));
});
$('pagAnterior').addEventListener('click', () => executar(() => ordenar(paginaAtual - 1)));
$('pagProxima').addEventListener('click', () => executar(() => ordenar(paginaAtual + 1)));
$('btnExportar').addEventListener('click', () => {
    window.location.href = '/api/exportar?' + parametros(filtrosOrdenacao());
});

$('formComparar').addEventListener('submit', (e) => {
    e.preventDefault();
    executar(async () => {
        const itens = await api('/api/comparar?' + parametros({
            criterio: $('cCriterio').value,
            ano: $('cAno').value,
            bioma: $('cBioma').value,
            amostra: $('cAmostra').value
        }));
        $('tabComparar').innerHTML = itens.map(it => {
            const m = it.resultado;
            if (!m) return `<tr><td>${esc(it.algoritmo)}</td><td>${it.complexidade}</td>
                <td colspan="6">${esc(it.observacao)}</td></tr>`;
            return `<tr><td>${esc(it.algoritmo)}</td><td>${it.complexidade}</td><td>${fmt(m.comparacoes)}</td>
                <td>${fmt(m.trocas)}</td><td>${fmt(m.movimentacoes)}</td><td><b>${fmt(m.totalOperacoes)}</b></td>
                <td>${fmtMs(m.tempoMs)}</td><td>${it.correto ? '✓ correto' : '✗ divergente'}</td></tr>`;
        }).join('');

        const executados = itens.filter(it => it.resultado);
        const nomes = executados.map(it => it.algoritmo);
        grafico('gOperacoes', {
            type: 'bar',
            data: {
                labels: nomes,
                datasets: [{label: 'Operações', data: executados.map(it => it.resultado.totalOperacoes), backgroundColor: CORES[0]}]
            },
            options: {scales: {y: {type: 'logarithmic'}}, plugins: {legend: {display: false}}}
        });
        grafico('gTempo', {
            type: 'bar',
            data: {
                labels: nomes,
                datasets: [{label: 'Tempo (ms)', data: executados.map(it => it.resultado.tempoMs), backgroundColor: CORES[1]}]
            },
            options: {plugins: {legend: {display: false}}}
        });
    });
});

async function carregarPainel() {
    const r = await api('/api/estatisticas?' + parametros({ano: $('pAno').value, bioma: $('pBioma').value}));
    const anos = Object.keys(r.porAno);
    $('kpis').innerHTML = kpi('Total de focos', fmt(r.total))
        + anos.map(a => kpi('Focos em ' + a, fmt(r.porAno[a]))).join('')
        + kpi('Municípios afetados', fmt(r.municipios));

    grafico('gMes', {
        type: 'line',
        data: {
            labels: MESES,
            datasets: anos.map((a, i) => ({label: a, data: r.porMes[a], borderColor: CORES[i], backgroundColor: CORES[i]}))
        }
    });
    grafico('gBioma', {
        type: 'bar',
        data: {
            labels: r.porBioma.map(b => b.nome),
            datasets: [{label: 'Focos', data: r.porBioma.map(b => b.focos), backgroundColor: CORES[2]}]
        },
        options: {plugins: {legend: {display: false}}}
    });
    grafico('gMunicipios', {
        type: 'bar',
        data: {
            labels: r.topMunicipios.map(m => m.nome),
            datasets: [{label: 'Focos', data: r.topMunicipios.map(m => m.focos), backgroundColor: CORES[0]}]
        },
        options: {indexAxis: 'y', plugins: {legend: {display: false}}}
    });
}

$('pAno').addEventListener('change', () => executar(carregarPainel));
$('pBioma').addEventListener('change', () => executar(carregarPainel));

$('uf').innerHTML = UFS.map(u => `<option ${u === 'SP' ? 'selected' : ''}>${u}</option>`).join('');
document.querySelectorAll('nav button').forEach(b => b.addEventListener('click', () => trocarAba(b.dataset.aba)));
executar(atualizarStatus);
