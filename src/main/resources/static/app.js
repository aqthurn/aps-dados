const $ = (id) => document.getElementById(id);

async function api(url, opcoes) {
    const resp = await fetch(url, opcoes);
    const corpo = await resp.json().catch(() => null);
    if (!resp.ok) throw new Error(corpo?.erro || 'Erro ' + resp.status);
    return corpo;
}

async function atualizarStatus() {
    const s = await api('/api/dados');
    $('status').textContent = s.total ? s.total + ' focos carregados' : 'Nenhum dado carregado';
}

$('formUpload').addEventListener('submit', async (e) => {
    e.preventDefault();
    const dados = new FormData();
    dados.append('arquivo', $('arquivo').files[0]);
    try {
        const r = await api('/api/dados/upload', {method: 'POST', body: dados});
        alert(r.importados + ' registros importados');
        atualizarStatus();
    } catch (err) {
        alert(err.message);
    }
});

$('btnLimpar').addEventListener('click', async () => {
    await fetch('/api/dados', {method: 'DELETE'});
    atualizarStatus();
});

$('formOrdenar').addEventListener('submit', async (e) => {
    e.preventDefault();
    const qs = new URLSearchParams({
        criterio: $('criterio').value,
        algoritmo: $('algoritmo').value,
        decrescente: $('ordem').value
    });
    try {
        const r = await api('/api/ordenar?' + qs);
        const m = r.resultado;
        $('metricas').textContent = `${m.algoritmo}: ${m.tamanho} registros, ${m.comparacoes} comparações, `
            + `${m.trocas} trocas, ${m.tempoMs.toFixed(2)} ms`;
        $('tabela').innerHTML = r.itens.map(f =>
            `<tr><td>${f.dataHora.replace('T', ' ')}</td><td>${f.municipio}</td><td>${f.bioma}</td></tr>`).join('');
    } catch (err) {
        alert(err.message);
    }
});

atualizarStatus();
