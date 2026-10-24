# AmbMão

Aplicação de cartilhas educativas sobre condições das mãos e punhos, com consulta pública e uma área para profissionais de saúde gerenciarem o conteúdo.

O front utiliza HTML, Sass e JavaScript. O backend usa Java 26, Spring Boot 4.1.2, Thymeleaf, Spring Security, JPA e Flyway, com PostgreSQL no Supabase.

## Funcionalidades

- Consulta pública com busca por nome, sem distinção de acentos ou maiúsculas.
- Cartilhas expansíveis com definição, sintomas em tópicos, orientações e referência.
- Login de profissionais com sessão e senhas BCrypt.
- Cadastro, edição e exclusão de cartilhas com confirmação.
- Menu de conta com ícone de perfil, nome, e-mail e logout.
- Layout responsivo, navegação por teclado e animações suaves que respeitam movimento reduzido.

## Requisitos

- Java 26.
- Maven 3.9 ou superior.
- Node.js e npm.
- Projeto Supabase com credenciais de conexão ao PostgreSQL.

## Executar localmente

Na raiz do projeto:

```bash
npm ci
cp -n backend/.env.example backend/.env
```

Preencha `backend/.env` com a URL JDBC, usuário e senha do PostgreSQL. Para criar o primeiro profissional, preencha também `AMBMAO_PROFISSIONAL_NOME`, `AMBMAO_PROFISSIONAL_EMAIL` e `AMBMAO_PROFISSIONAL_SENHA`. Use uma senha de pelo menos 12 caracteres e no máximo 72 bytes UTF-8.

As credenciais são as do banco; chaves da API do Supabase não substituem a senha JDBC. Consulte os detalhes de conexão e provisionamento no [README do backend](backend/README.md). Se já tiver um `.env` configurado, preserve esse arquivo.

```bash
npm run backend:run
```

Abra **http://localhost:8080**. Esse comando compila o Sass, sincroniza templates e assets e inicia o Spring. Depois que o profissional for criado, remova seus três valores de provisionamento do `.env`; a conta permanece no banco. Não há senha padrão nem cadastro público.

## Desenvolvimento

As fontes editáveis ficam em `src/`. Para recompilar o Sass durante a edição:

```bash
npm run sass:watch
```

Para sincronizar as alterações com o backend:

```bash
npm run backend:prepare
```

Reinicie o Spring para carregar as alterações. Os arquivos em `backend/src/main/resources/templates/` e `static/` são mantidos no repositório, mas as páginas e assets compartilhados devem ser editados em `src/` e sincronizados pelo comando acima.

Também é possível abrir `src/index.html` e `src/cartilhas.html` como prévia estática. Autenticação e gravação de dados funcionam através do Spring.

## Testes

```bash
npm test
npm run backend:test
```

Os testes Java usam H2 em memória e não alteram o Supabase. A última verificação passou com **16 testes Java**, além dos testes do front. A suíte cobre autenticação, autorização, CSRF, cadastro, edição, exclusão, busca, validação e renderização segura.

## Banco de dados

O Flyway gerencia as tabelas no schema privado `ambmao`. Em um banco novo, a migração inicial cria a estrutura sem conteúdo. O projeto Supabase utilizado no desenvolvimento já tem a estrutura e o baseline Flyway; não reaplique o bootstrap.

Três cartilhas de exemplo foram cadastradas nesse projeto: síndrome do túnel do carpo, tenossinovite de De Quervain e dedo em gatilho. O script opcional está em `backend/supabase/example-cartilhas.sql`; ele não roda automaticamente ao iniciar a aplicação.

## Estrutura

```text
src/                  HTML, Sass, JavaScript e assets editáveis
backend/              Aplicação Spring Boot e testes Java
backend/supabase/     Scripts manuais de bootstrap, exemplos e verificação SQL
scripts/              Sincronização do front e configuração do MCP
tests/                Testes do front
docs/                 Documentação de integração
```

## Antes de fazer commit

O `.gitignore` mantém `.env`, dependências, builds, logs e configurações locais fora do versionamento. O arquivo `backend/.env.example`, sem credenciais reais, deve ser versionado junto com as fontes, migrações, testes e `package-lock.json`.

Revise os arquivos preparados para o commit:

```bash
git status --short
git diff --cached --stat
```

Se um `.env` já estiver sendo rastreado, o `.gitignore` não o remove do índice. Nesse caso, use `git rm --cached backend/.env` para mantê-lo apenas no computador.

Veja também a [configuração detalhada do backend](backend/README.md) e a [integração com Spring e Thymeleaf](docs/INTEGRACAO-SPRING.md).

## Colaboração

Consulte o [guia de Git e GitHub](GUIA_GIT.md) para o fluxo de contribuição. A aplicação atual é executada a partir de `backend/`; os arquivos Maven e Java na raiz, preservados do histórico remoto, pertencem à estrutura inicial.

## Equipe

- Brenda Luiza
- João Pedro Amaral Souto
- Lucas Yago dos Santos Paula
