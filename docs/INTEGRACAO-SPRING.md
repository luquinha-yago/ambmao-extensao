# Integração do front AmbMão com Spring MVC e Thymeleaf

O backend já está implementado em `backend/`, com Spring Security, JPA e Flyway para PostgreSQL do Supabase. Veja [configuração e execução](../backend/README.md). As fontes HTML continuam funcionando também como prévia estática, sem autenticação ou gravação no navegador.

## Referência utilizada

Foi analisado o arquivo `controlevacinacao.zip`: `PessoaController`, `PessoaService`, `PessoaRepository`, os records em `pessoa/dto` e os templates `pessoas/lista.html`, `create.html`, `edit.html` e seus campos. O modelo é Spring MVC com HTML renderizado pelo Thymeleaf, não uma API REST consumida por um framework JavaScript.

O front do AmbMão segue esse formato: formulários HTML com nomes estáveis, bindings `th:field`, mensagens de validação, coleção renderizada com `th:each`, busca GET, cadastro POST e edição PUT. Mantém o Sass e a identidade visual deste projeto. Não depende do Tailwind ou do HTMX da referência; ambos são opcionais para o backend.

## Visualizar agora

Abra `src/cartilhas.html` no navegador. A navegação permite consultar a página de login e abrir, por um link identificado como demonstração, a listagem e o formulário profissional. Os exemplos são definidos em `src/js/cartilhas-dados.js`.

- A leitura tem três exemplos expansíveis, busca por nome sem distinção de acento ou maiúscula, contador e estado vazio.
- A área profissional permite abrir uma cartilha de exemplo para edição e começar uma nova.
- **Visualizar cartilha** mostra os campos digitados, sem salvar. Recarregar descarta as alterações.
- **Entrar** e **Salvar cartilha** ficam desabilitados na prévia. Não existe login simulado nem verificação de senha no JavaScript.
- A prévia pode ser aberta por arquivo local; não exige `fetch`, servidor ou armazenamento no navegador.

## Exportar os arquivos para o projeto Java

```bash
npm run export:spring
```

O comando compila o Sass e produz `build/spring-resources/`, sem criar classes Java, projeto Maven ou banco. Copie suas pastas `templates/` e `static/` para `src/main/resources/` do projeto Spring.

| Arquivo fonte neste projeto | Template gerado / retorno do controller |
| --- | --- |
| `src/index.html` | `templates/index.html` / `index` |
| `src/cartilhas.html` | `templates/cartilhas/lista.html` / `cartilhas/lista` |
| `src/login.html` | `templates/login.html` / `login` |
| `src/profissional-cartilhas.html` | `templates/profissional/cartilhas/lista.html` / `profissional/cartilhas/lista` |
| `src/profissional-cartilha-form.html` | `templates/profissional/cartilhas/form.html` / `profissional/cartilhas/form` |
| `src/profissional-cartilha-excluir.html` | `templates/profissional/cartilhas/excluir.html` / `profissional/cartilhas/excluir` |

CSS, JS e o ícone da marca são copiados para `static/`. Fotografias e dados de demonstração não são exportados. Faça as alterações no `src/` deste projeto e exporte novamente; `build/` é gerado.

Todos os caminhos de assets e links que mudam no Spring possuem `th:href`/`th:src`. O Thymeleaf também troca `data-preview="true"` por `false` no `body`, remove avisos/controles da demonstração com `th:remove="all"` e habilita os botões de submissão com `th:disabled="${false}"`. O script não sobrescreve conteúdo nem intercepta submissões de edição/login em modo servidor. A busca pode filtrar os registros presentes, mas o botão Buscar faz GET para o servidor e deve pesquisar no conjunto completo.

## Rotas implementadas no backend

| Método e rota | Uso / atributos do Model |
| --- | --- |
| `GET /` | Página inicial |
| `GET /cartilhas?nomeDoenca=...` | Leitura pública; atributo `cartilhas`: lista de respostas |
| `GET /login` | Tela de login; parâmetro `error` mostra falha |
| `POST /login` | Processamento pelo Spring Security; `username` (e-mail) e `password` |
| `GET /profissional/cartilhas?nomeDoenca=...` | Lista profissional; atributo `cartilhas`; `mensagem` opcional |
| `GET /profissional/cartilhas/nova` | Formulário; `cartilhaForm` vazio e `codigo` nulo |
| `POST /profissional/cartilhas` | Cadastro validado |
| `GET /profissional/cartilhas/{codigo}/editar` | Formulário; `cartilhaForm` preenchido e `codigo` |
| `PUT /profissional/cartilhas/{codigo}` | Atualização validada |
| `GET /profissional/cartilhas/{codigo}/excluir` | Confirmação; atributos `cartilha` e `codigo`, sem remover dados |
| `DELETE /profissional/cartilhas/{codigo}` | Exclusão definitiva com função `PROFISSIONAL` e CSRF |

A listagem espera `List<CartilhaResponse>`, não `PageWrapper`: ainda não há paginação no front. Caso o backend use `Page`, passe seu `getContent()` como `cartilhas` e implemente os controles de paginação quando necessário. Não é preciso copiar a infraestrutura inteira do projeto de vacinação.

O backend contém `cartilha/CartilhaController`, `CartilhaService`, `CartilhaRepository`, `Cartilha` e os DTOs `CartilhaForm`, `CartilhaSearchRequest` e `CartilhaResponse`, seguindo a organização da referência. O record `CartilhaForm` é compartilhado entre criação e edição. Para sincronizar diretamente com o backend, use `npm run backend:prepare`.

## Campos e tipos

| Campo | Tipo Java sugerido | Obrigatório | Limite do front |
| --- | --- | --- | --- |
| `codigo` | `Long` | Gerado pelo banco; presente na resposta | — |
| `nomeDoenca` | `String` | Sim | 160 caracteres |
| `oQueE` | `String` | Sim | 5.000 caracteres |
| `sinaisSintomas` | `String` | Sim | 5.000 caracteres |
| `orientacoesGerais` | `String` | Sim | 10.000 caracteres |
| `fonteTitulo` | `String` | Não | 200 caracteres |
| `fonteUrl` | `String` | Não | 2.000 caracteres |

Os quatro campos obrigatórios representam a cartilha. Os sinais/sintomas e orientações são textos com uma entrada por linha: preserve as quebras `\n`. Os sintomas são exibidos em uma lista `ul/li` com bolinhas, um tópico por linha não vazia; `CartilhaResponse.getSinaisSintomasTopicos()` prepara a leitura no servidor e a prévia usa criação segura de elementos com `textContent`. As orientações mantêm `white-space: pre-line`. Não são strings HTML; mantenha `th:text` para exibir conteúdo escapado. Os nomes dos campos de create e update devem ser os mesmos, facilitando compartilhar o formulário. Para as fontes, aceite somente URL absoluta HTTP ou HTTPS, ou vazio. A validação em Java precisa repetir limites e obrigatoriedade (`@NotBlank`, `@Size` e validação de URL), incluindo texto composto apenas de espaços.

Uma resposta possui `codigo` e todos os demais campos. Os campos de referência podem ser nulos ou vazios. Se houver URL sem título, é recomendável o backend preencher o título com “Consultar referência”.

## Formulários, erros e autenticação

- Ao abrir o formulário, use `model.addAttribute("cartilhaForm", ...)`. O mesmo atributo deve existir quando houver erro. Na edição, mantenha também `codigo`.
- No POST/PUT, vincule o DTO como `@Valid @ModelAttribute("cartilhaForm")`, seguido de `BindingResult`. Com erros, retorne `profissional/cartilhas/form`; os elementos `th:errors` e o resumo do formulário exibem a validação.
- O formulário usa `method="post"` e `_method=PUT` na edição. Habilite `spring.mvc.hiddenmethod.filter.enabled=true`, ou adapte a rota de edição para POST junto com o formulário.
- Após salvar, redirecione para `/profissional/cartilhas` com flash attribute `mensagem` para a confirmação.
- O backend deve proteger todas as rotas `/profissional/**`, autenticar o profissional e autorizar a escrita. O link da demonstração não é controle de acesso e é removido pelo Thymeleaf.
- Com Spring Security e integração MVC/Thymeleaf, `th:action` permite incluir o token CSRF nos formulários POST. Mantenha CSRF ativo e valide a integração no projeto Java; nenhum token foi inventado no front.
- Depois do login, configure o sucesso para `/profissional/cartilhas`; falha para `/login?error`. A forma de cadastrar profissionais e armazenar senhas pertence ao backend.
- A confirmação de exclusão usa POST com `_method=DELETE`, CSRF e opção de cancelar. A rota GET nunca exclui o registro.
- `UsuarioModelAdvice` fornece `usuarioAutenticado` e `contaProfissional` (somente nome e e-mail) ao cabeçalho. O menu nativo `details/summary` mostra o ícone de perfil, gerenciamento e logout; fecha com Escape ou clique externo. A senha e seu hash não fazem parte desse modelo.

## Conteúdo de exemplo e referências

Os exemplos servem para conferir o layout e devem ser revisados pela equipe de saúde antes de publicar. Não houve criação de conteúdo em nome da UFTM nem de programas de exercícios.

- Síndrome do túnel do carpo: [NHS](https://www.nhs.uk/conditions/carpal-tunnel-syndrome/).
- Tenossinovite de De Quervain: [AAOS / OrthoInfo](https://www.orthoinfo.org/diseases--conditions/de-quervains-tendinosis/).
- Dedo em gatilho: [AAOS / OrthoInfo](https://www.orthoinfo.org/diseases--conditions/trigger-finger/).

Cada exemplo também mostra sua referência na própria cartilha.

## Verificação

```bash
npm test
npm run export:spring
```

Os testes locais cobrem busca com acentos, URLs de referência, campos dos exemplos e separação da demonstração. Após conectar o backend, teste renderização Thymeleaf, autenticação, autorização, CSRF, validação, busca e persistência de cadastro/edição no ambiente Java. Essas operações são implementadas no backend. O build completo e a conexão ao Supabase ainda precisam ser verificados com dependências e credenciais disponíveis, conforme o README do backend.
