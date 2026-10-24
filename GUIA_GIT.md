# Guia rápido de Git e GitHub

> Objetivo: manter o projeto organizado sem adicionar burocracia. Use o que fizer sentido para a equipe; campos e referências a Issues são opcionais.

## 1. Combinados simples

- Faça uma branch para cada tarefa ou alteração relacionada.
- Evite desenvolver diretamente na `main`.
- Mantenha a `main` funcionando sempre que possível.
- Faça commits pequenos, com mensagens que expliquem a mudança.
- Abra uma Pull Request (PR) para revisar e integrar alterações.
- Teste o que mudou antes de pedir revisão.
- Não inclua senhas, tokens, chaves de API ou arquivos com dados pessoais no repositório.
- Não misture mudanças sem relação na mesma PR.

## 2. Nomes de branches

Use letras minúsculas, hífens e um nome curto que explique a tarefa.

| Tipo | Quando usar | Exemplo |
|---|---|---|
| `feat/` | Nova funcionalidade | `feat/cadastro-usuario` |
| `fix/` | Correção de erro | `fix/validacao-email` |
| `docs/` | Documentação | `docs/guia-instalacao` |
| `refactor/` | Reorganização interna sem mudar o comportamento esperado | `refactor/servico-usuario` |
| `test/` | Criação ou ajuste de testes | `test/login` |
| `chore/` | Manutenção e configuração | `chore/atualiza-dependencias` |

Se a equipe usa Issues, pode incluir o número: `feat/12-cadastro-usuario`. O número é opcional.

## 3. Mensagens de commit

Uma convenção simples é usar o padrão **Conventional Commits**:

`tipo: descrição curta no presente`

| Tipo | Uso | Exemplo |
|---|---|---|
| `feat` | Nova funcionalidade | `feat: adiciona cadastro de usuário` |
| `fix` | Correção de bug | `fix: corrige validação do e-mail` |
| `docs` | Documentação | `docs: atualiza guia de instalação` |
| `style` | Formatação sem alterar a lógica | `style: ajusta indentação` |
| `refactor` | Melhoria interna sem mudar comportamento | `refactor: simplifica serviço de usuário` |
| `test` | Testes | `test: adiciona testes de login` |
| `chore` | Tarefas de manutenção | `chore: atualiza dependências` |

Boas práticas:
- Descreva uma mudança principal por commit.
- Prefira mensagens específicas a `ajustes`, `teste`, `final` ou `mudanças`.
- Escreva de forma curta e clara.
- O escopo é opcional. Exemplo: `feat(auth): adiciona recuperação de senha`.

## 4. Fluxo recomendado

Os exemplos abaixo consideram que a branch principal se chama `main`. Se o projeto usa outro nome, substitua `main` pelo nome correto.

### A. Atualizar a branch principal

```bash
git switch main
git pull origin main
```

Atualiza sua cópia local com as mudanças do repositório remoto.

### B. Criar uma branch para a tarefa

```bash
git switch -c feat/nome-da-tarefa
```

Cria a branch e muda para ela. Escolha um nome relacionado à alteração.

### C. Conferir e selecionar as alterações

```bash
git status
git diff
git add caminho/do/arquivo
```

- `git status`: mostra os arquivos alterados e o estado do Git.
- `git diff`: mostra as mudanças ainda não preparadas para commit.
- `git add caminho/do/arquivo`: prepara um arquivo específico para o próximo commit.

Para preparar todas as alterações atuais, pode usar `git add .`, mas confira antes o `git status` para não incluir arquivos indevidos.

### D. Criar o commit

```bash
git commit -m "feat: descreve a mudança"
```

Registra as alterações preparadas com uma mensagem clara.

### E. Enviar a branch ao GitHub

```bash
git push -u origin feat/nome-da-tarefa
```

Publica a branch remota. O `-u` configura o acompanhamento para facilitar os próximos `git push` e `git pull` nessa branch.

### F. Abrir a Pull Request

No GitHub, abra uma PR da sua branch para `main` e preencha o modelo disponível em `.github/PULL_REQUEST_TEMPLATE.md`. Revise o resumo, os testes e os arquivos alterados antes de solicitar revisão.

### G. Depois do merge

```bash
git switch main
git pull origin main
git branch -d feat/nome-da-tarefa
```

Atualiza a `main` local e apaga a branch local já integrada. Só apague branches que não tenham trabalho pendente. A exclusão local não apaga a branch remota.

## 5. Outros comandos úteis

| Comando | O que faz |
|---|---|
| `git branch` | Lista branches locais. |
| `git switch nome-da-branch` | Troca para uma branch existente. |
| `git branch -a` | Lista branches locais e remotas conhecidas. |
| `git log --oneline -10` | Mostra os últimos 10 commits de forma resumida. |
| `git diff --staged` | Mostra o que está preparado para o próximo commit. |
| `git fetch origin` | Busca informações novas do remoto sem integrar mudanças na branch atual. |
| `git pull origin main` | Busca e integra as atualizações remotas de `main` na branch atual. Use estando na branch que deseja atualizar. |
| `git restore caminho/do/arquivo` | Descarta alterações locais não preparadas naquele arquivo. **Use com cuidado: pode perder trabalho.** |
| `git restore --staged caminho/do/arquivo` | Remove o arquivo da área de stage, mas mantém as alterações no disco. |
| `git stash` | Guarda temporariamente alterações locais para você trocar de contexto. |
| `git stash pop` | Tenta reaplicar as alterações guardadas mais recentemente. |
| `git show --stat` | Mostra o resumo do commit mais recente. |

### Se houver mudanças novas em `main` enquanto você trabalha

Uma opção simples é atualizar sua branch com merge:

```bash
git fetch origin
git switch feat/nome-da-tarefa
git merge origin/main
```

Se houver conflitos, resolva os arquivos indicados, teste o projeto e finalize o merge conforme o Git solicitar. Não execute comandos de reset ou limpeza sem entender o efeito.

## 6. Antes de abrir uma PR

- [ ] A branch tem um nome claro.
- [ ] A PR trata de uma tarefa principal.
- [ ] Os arquivos alterados foram conferidos.
- [ ] Testei o que foi alterado ou expliquei o que não consegui testar.
- [ ] Não incluí senhas, tokens ou arquivos locais indevidos.
- [ ] A descrição explica o problema e a solução.
- [ ] Relacionei uma Issue, se existir e for relevante.

## 7. Estrutura sugerida

```text
projeto/
├── .github/
│   └── PULL_REQUEST_TEMPLATE.md
├── src/                     # exemplo: código-fonte
├── README.md                # apresentação e instruções do projeto
└── GUIA_GIT.md              # este guia
```

A pasta `src/` é apenas um exemplo: mantenha a estrutura real do seu projeto.

## 8. Regra de ouro

**Uma tarefa por branch, commits claros e uma PR fácil de entender.** Para um projeto pequeno, isso já ajuda bastante. Ajustem estas convenções se a equipe tiver um padrão próprio.
