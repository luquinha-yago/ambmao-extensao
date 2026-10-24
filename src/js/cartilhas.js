(() => {
    const preview = document.body.dataset.preview === 'true';
    const { corresponde, fonteSegura } = window.AmbMaoCartilhasUtils;
    const exemplos = preview ? window.AmbMaoExemplos : [];
    const lista = document.querySelector('#lista-cartilhas, #lista-gerenciamento');

    function preencher(elemento, dados) {
        for (const campo of ['nomeDoenca', 'oQueE', 'sinaisSintomas', 'orientacoesGerais']) {
            elemento.querySelectorAll(`[data-field="${campo}"]`).forEach((alvo) => {
                if (campo === 'sinaisSintomas') {
                    const topicos = (dados[campo] || '').split(/\r\n|\r|\n/).map((linha) => linha.trim()).filter(Boolean);
                    alvo.replaceChildren(...topicos.map((texto) => {
                        const item = document.createElement('li');
                        item.textContent = texto;
                        return item;
                    }));
                    return;
                }
                alvo.textContent = (alvo.classList.contains('sr-only') ? ' ' : '') + (dados[campo] || '');
            });
        }
        const referencia = elemento.querySelector('[data-fonte]');
        if (referencia) {
            const url = fonteSegura(dados.fonteUrl);
            referencia.hidden = !url;
            const link = referencia.querySelector('a');
            if (url) {
                link.href = url;
                link.textContent = dados.fonteTitulo || 'Consultar referência';
            } else link.removeAttribute('href');
        }
    }

    if (lista) {
        if (preview) {
            const modelo = lista.querySelector('[data-cartilha]').cloneNode(true);
            lista.replaceChildren();
            exemplos.forEach((cartilha) => {
                const item = modelo.cloneNode(true);
                preencher(item, cartilha);
                const editar = item.querySelector('[data-editar]');
                if (editar) editar.href = `./profissional-cartilha-form.html?codigo=${encodeURIComponent(cartilha.codigo)}`;
                lista.append(item);
            });
        }
        const busca = document.querySelector('#busca-doenca');
        const limpar = document.querySelector('#limpar-busca');
        const resultado = document.querySelector('#resultado-busca');
        const vazio = document.querySelector('#cartilhas-vazio');
        const itens = [...lista.querySelectorAll('[data-cartilha]')];

        function filtrar() {
            let quantidade = 0;
            itens.forEach((item) => {
                item.hidden = !corresponde(item.querySelector('[data-field="nomeDoenca"]').textContent, busca.value);
                if (!item.hidden) quantidade++;
            });
            resultado.textContent = `${quantidade} ${quantidade === 1 ? 'cartilha encontrada' : 'cartilhas encontradas'}`;
            vazio.hidden = quantidade > 0;
            limpar.hidden = !busca.value;
        }

        if (preview) busca.value = new URLSearchParams(window.location.search).get('nomeDoenca') || '';
        busca.addEventListener('input', filtrar);
        busca.form.addEventListener('submit', (evento) => {
            // No servidor o GET também busca registros que não estão nesta página.
            if (preview) { evento.preventDefault(); filtrar(); }
        });
        limpar.addEventListener('click', () => {
            busca.value = '';
            if (!preview) { busca.form.requestSubmit(); return; }
            filtrar();
            busca.focus();
        });
        filtrar();
    }

    const login = document.querySelector('#form-login');
    if (login && preview) {
        login.addEventListener('submit', (evento) => {
            evento.preventDefault();
            const status = document.querySelector('#login-status');
            status.textContent = 'O acesso com senha ainda não está disponível nesta demonstração. Use o link abaixo para visualizar as telas.';
            status.hidden = false;
            login.elements.password.value = '';
        });
    }

    const formulario = document.querySelector('#form-cartilha');
    if (formulario && preview) {
        const status = document.querySelector('#editor-status');
        const codigo = new URLSearchParams(window.location.search).get('codigo');
        const registro = exemplos.find((item) => String(item.codigo) === codigo);
        if (registro) {
            document.querySelector('#titulo-editor').textContent = 'Editar cartilha';
            for (const [nome, valor] of Object.entries(registro)) {
                const campo = formulario.elements.namedItem(nome);
                if (campo) campo.value = valor;
            }
        } else if (codigo) {
            status.textContent = 'Esta cartilha não foi encontrada na demonstração. Volte para a lista para selecionar outra.';
            status.hidden = false;
        }
        const camposTexto = ['nomeDoenca', 'oQueE', 'sinaisSintomas', 'orientacoesGerais'];
        camposTexto.forEach((nome) => {
            const campo = formulario.elements.namedItem(nome);
            campo.addEventListener('input', () => campo.setCustomValidity(''));
        });
        const campoUrl = formulario.elements.namedItem('fonteUrl');
        campoUrl.addEventListener('input', () => campoUrl.setCustomValidity(''));

        function visualizar() {
            camposTexto.forEach((nome) => {
                const campo = formulario.elements.namedItem(nome);
                campo.setCustomValidity(campo.value.trim() ? '' : 'Preencha este campo.');
            });
            campoUrl.setCustomValidity(!campoUrl.value || fonteSegura(campoUrl.value) ? '' : 'Informe um link iniciado por http:// ou https://.');
            if (!formulario.reportValidity()) return;
            const dados = Object.fromEntries(new FormData(formulario));
            const previa = document.querySelector('#previa-cartilha');
            preencher(previa, dados);
            previa.hidden = false;
            previa.focus({ preventScroll: true });
            const reduzirMovimento = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
            previa.scrollIntoView({ behavior: reduzirMovimento ? 'instant' : 'smooth', block: 'start' });
            status.textContent = 'Prévia atualizada. As alterações não foram salvas.';
            status.hidden = false;
        }
        document.querySelector('#visualizar-cartilha').addEventListener('click', visualizar);
        formulario.addEventListener('submit', (evento) => { evento.preventDefault(); visualizar(); });
    }
})();
