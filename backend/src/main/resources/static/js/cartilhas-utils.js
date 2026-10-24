(function (root) {
    const normalizar = (texto) => String(texto ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR').trim();
    const corresponde = (nome, busca) => normalizar(busca).split(/\s+/).every((termo) => normalizar(nome).includes(termo));
    const fonteSegura = (valor) => {
        try {
            const url = new URL(valor);
            return url.protocol === 'https:' || url.protocol === 'http:' ? url.href : null;
        } catch { return null; }
    };
    const utils = { normalizar, corresponde, fonteSegura };
    if (typeof module !== 'undefined' && module.exports) module.exports = utils;
    else root.AmbMaoCartilhasUtils = utils;
})(typeof window === 'undefined' ? globalThis : window);
