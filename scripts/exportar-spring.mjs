import { cp, mkdir, copyFile } from 'node:fs/promises';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
const raiz = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const integrado = process.argv.includes('--backend');
const destino = resolve(raiz, integrado ? 'backend/src/main/resources' : 'build/spring-resources');
const paginas = {
    'index.html': 'index.html',
    'cartilhas.html': 'cartilhas/lista.html',
    'login.html': 'login.html',
    'profissional-cartilhas.html': 'profissional/cartilhas/lista.html',
    'profissional-cartilha-form.html': 'profissional/cartilhas/form.html',
    'profissional-cartilha-excluir.html': 'profissional/cartilhas/excluir.html'
};
for (const [origem, template] of Object.entries(paginas)) {
    const alvo = resolve(destino, 'templates', template);
    await mkdir(dirname(alvo), { recursive: true });
    await copyFile(resolve(raiz, 'src', origem), alvo);
}
for (const pasta of ['css', 'js', 'assets/icons']) {
    await cp(resolve(raiz, 'src', pasta), resolve(destino, 'static', pasta), {
        recursive: true,
        filter: (caminho) => !caminho.endsWith('cartilhas-dados.js')
    });
}
console.log(integrado ? 'Front integrado em backend/src/main/resources.' : 'Front exportado para build/spring-resources.');
