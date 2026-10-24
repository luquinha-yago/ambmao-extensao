const header = document.querySelector('.cabecalho');
const button = document.querySelector('.cabecalho__menu');
const menu = document.querySelector('#menu-principal');
const desktop = window.matchMedia('(min-width: 1024px)');
const conta = document.querySelector('.conta');

function setMenu(open) {
    if (!open && conta) conta.open = false;
    menu.hidden = !open;
    button.setAttribute('aria-expanded', String(open));
    button.setAttribute('aria-label', open ? 'Fechar menu' : 'Abrir menu');
}

function syncMenu() {
    button.hidden = desktop.matches;
    setMenu(desktop.matches);
}

header.classList.add('cabecalho--interativo');
syncMenu();
desktop.addEventListener('change', syncMenu);
button.addEventListener('click', () => setMenu(button.getAttribute('aria-expanded') !== 'true'));
menu.addEventListener('click', (event) => {
    if (event.target.closest('a')) {
        if (conta) conta.open = false;
        if (!desktop.matches) setMenu(false);
    }
});
document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape' && conta?.open) {
        conta.open = false;
        conta.querySelector('summary').focus();
        return;
    }
    if (event.key === 'Escape' && !desktop.matches && !menu.hidden) {
        setMenu(false);
        button.focus();
    }
});
document.addEventListener('click', (event) => {
    if (conta && !conta.contains(event.target)) conta.open = false;
    if (!desktop.matches && !header.contains(event.target)) setMenu(false);
});
