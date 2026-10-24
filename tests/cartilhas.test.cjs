const test = require('node:test');
const assert = require('node:assert/strict');
const vm = require('node:vm');
const fs = require('node:fs');
const { corresponde, fonteSegura } = require('../src/js/cartilhas-utils.js');

test('busca por nome aceita acentos, caixa e múltiplos termos', () => {
    assert.equal(corresponde('Síndrome do túnel do carpo', ' TUNEL carpo '), true);
    assert.equal(corresponde('Tenossinovite de De Quervain', 'quervain'), true);
    assert.equal(corresponde('Dedo em gatilho', 'túnel'), false);
    assert.equal(corresponde('Dedo em gatilho', ''), true);
});
test('referências aceitam somente URLs HTTP ou HTTPS completas', () => {
    assert.equal(fonteSegura('https://www.nhs.uk/conditions/carpal-tunnel-syndrome/'), 'https://www.nhs.uk/conditions/carpal-tunnel-syndrome/');
    for (const value of ['', undefined, '/relativa', 'javascript:alert(1)', 'data:text/html,teste', 'file:///tmp/arquivo']) assert.equal(fonteSegura(value), null);
});
test('todos os exemplos possuem os campos usados na leitura e edição', () => {
    const contexto = { window: {} };
    vm.runInNewContext(fs.readFileSync('src/js/cartilhas-dados.js', 'utf8'), contexto);
    const exemplos = contexto.window.AmbMaoExemplos;
    assert.equal(exemplos.length, 3);
    assert.equal(new Set(exemplos.map((x) => x.codigo)).size, exemplos.length);
    for (const cartilha of exemplos) {
        for (const campo of ['nomeDoenca', 'oQueE', 'sinaisSintomas', 'orientacoesGerais']) assert.ok(cartilha[campo].trim());
        assert.ok(fonteSegura(cartilha.fonteUrl));
    }
});
test('prévia não grava dados nem simula autenticação e usa texto ao renderizar o conteúdo', () => {
    const script = fs.readFileSync('src/js/cartilhas.js', 'utf8');
    assert.doesNotMatch(script, /localStorage|sessionStorage|innerHTML|fetch\(/);
    const form = fs.readFileSync('src/profissional-cartilha-form.html', 'utf8');
    assert.match(form, /id="salvar-cartilha"[^>]*disabled/);
    assert.match(form, /th:field="\*\{orientacoesGerais\}"/);
});
