# AmbMão — Spring Boot e Supabase

Backend em Java 26 e Spring Boot 4.1.2, organizado em Spring MVC → Service → Repository, com DTOs, Thymeleaf, Spring Security, JPA e migrações Flyway. Utiliza PostgreSQL no Supabase.

## O que está implementado

- Consulta pública e busca por nome, ignorando acentos e maiúsculas.
- Cartilhas com nome da doença, definição, sinais/sintomas, orientações gerais e referência opcional.
- Login real com Spring Security, sessão e senha BCrypt; profissionais ficam no PostgreSQL.
- Cadastro e edição protegidos pela função `PROFISSIONAL`, com validação de campos no servidor.
- Formulários POST/PUT/DELETE com CSRF, mensagens de sucesso, validação na própria tela e logout.
- Exclusão de cartilhas com tela de confirmação e menu de conta com ícone, nome e e-mail do profissional conectado.
- Migração das tabelas no schema `ambmao`, separado do `public` exposto por padrão pela Data API do Supabase.

O Supabase é o banco PostgreSQL. Esta implementação usa autenticação do Spring, não Supabase Auth.

### Cartilhas de exemplo

No ambiente de desenvolvimento, foram cadastradas no projeto `clcfbwdtiesbjmaropra` três cartilhas educativas: síndrome do túnel do carpo, tenossinovite de De Quervain e dedo em gatilho. Elas incluem definição, sintomas, orientações gerais e referências verificadas do NHS/AAOS. Podem ser editadas na área profissional.

O cadastro manual pelo MCP está registrado em [`supabase/example-cartilhas.sql`](supabase/example-cartilhas.sql). Esse script preserva registros com o mesmo nome normalizado e não faz parte das migrações automáticas: outros bancos continuam começando sem cartilhas. Nenhuma credencial de login foi incluída no script.

As animações discretas ficam em `src/scss/_motion.scss`, são compiladas para o CSS compartilhado e respeitam `prefers-reduced-motion`. Não dependem de JavaScript adicional. O CSS e os templates foram sincronizados com o backend.

## Preparação

Requisitos: Java 26, Maven 3.9+, Node/npm e um projeto Supabase.

Na raiz do repositório:

```bash
npm install
npm run backend:prepare
```

O front é editado em `src/` na raiz. `backend:prepare` compila o Sass e copia os templates e assets para `backend/src/main/resources/`. Esses arquivos já estão incluídos no projeto Java; execute a sincronização novamente quando editar o front. Não é necessário um servidor separado para o HTML.

## Conectar o Supabase

1. No painel do seu projeto Supabase, abra **Connect → Session pooler → JDBC** e copie host e usuário reais. Use a porta **5432** ou a conexão direta quando compatível com a sua rede.
2. Dentro de `backend/`, copie `.env.example` para `.env` e preencha:

```properties
SUPABASE_DB_URL=jdbc:postgresql://HOST_REAL:5432/postgres?sslmode=require
SUPABASE_DB_USERNAME=USUARIO_DO_SESSION_POOLER
SUPABASE_DB_PASSWORD=SENHA_DO_BANCO
```

O nome do banco normalmente é `postgres`; confirme no painel. A senha fica separada da URL. Não use a chave pública/anon nem a chave service-role como senha JDBC. Não adicione o schema `ambmao` aos schemas expostos pela Data API: o acesso acontece pelo Java.

O `.env` é importado como arquivo Java `.properties` pelo Spring: não use `export` nem aspas nos valores. Escape barras invertidas como `\\` se existirem. Você também pode definir as mesmas variáveis diretamente no ambiente da aplicação.

Ao iniciar, o Flyway cria o schema e as tabelas de cartilhas e profissionais. A conta de conexão precisa poder criar o schema e as tabelas; a conexão administrativa padrão do projeto é suficiente. O Hibernate apenas valida a estrutura (`ddl-auto=validate`), preservando o controle das mudanças nas migrações. O banco começa sem cartilhas; os exemplos clínicos da prévia não são publicados automaticamente.

### Projeto já preparado pelo MCP

Em 24/10/2026, o MCP conectado a `https://clcfbwdtiesbjmaropra.supabase.co` aplicou a migração `20261024200826_ambmao_schema_inicial_v01`. O SQL está em [`supabase/bootstrap.sql`](supabase/bootstrap.sql): cria o schema `ambmao`, as tabelas da V01 e revoga o acesso de `PUBLIC`, `anon` e `authenticated` ao schema, tabelas e sequences. O schema permanece destinado ao JDBC, sem políticas de acesso pela Data API. Não execute o bootstrap novamente nesse banco.

A estrutura foi adotada pelo Flyway com `BASELINE` na versão `1` e `success=true`, confirmado no banco. As opções temporárias de baseline já foram removidas do `.env`. As próximas migrações devem começar em V02; não reaplique o bootstrap nem configure um novo baseline nesse banco.

Para outro banco vazio, deixe o Flyway executar a V01 normalmente. Somente se criar a estrutura manualmente com o bootstrap será necessário adotar a V01 no primeiro início, usando temporariamente:

```properties
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=1
```

Confira a estrutura antes de adotar o baseline e remova essas opções após confirmar o registro em `ambmao.flyway_schema_history`. O histórico de migrações do MCP é separado do histórico do Flyway.

## Criar o primeiro profissional

No mesmo `.env`, preencha:

```properties
AMBMAO_PROFISSIONAL_NOME=Nome do profissional
AMBMAO_PROFISSIONAL_EMAIL=nome@instituicao.br
AMBMAO_PROFISSIONAL_SENHA=SUA_SENHA_FORTE
```

Use pelo menos 12 caracteres e no máximo 72 bytes UTF-8 para a senha. Na inicialização, o backend cria a conta somente se aquele e-mail ainda não existir, armazenando o hash BCrypt. Depois do primeiro início bem-sucedido, remova essas três variáveis: a conta permanece no banco. Para provisionar outro profissional, configure outro e-mail e reinicie. Isso não troca a senha de uma conta já existente. Não existe cadastro público de profissionais ou senha padrão.

## Executar

A partir desta pasta `backend/`, para que o `.env` seja localizado:

```bash
mvn spring-boot:run
```

Ou, a partir da raiz:

```bash
npm run backend:run
```

Abra `http://localhost:8080`. Consulte `/cartilhas`, entre por `/login` e use `/profissional/cartilhas` para cadastrar, editar ou excluir. Ao salvar, a cartilha aparece na consulta pública. A sessão expira após 30 minutos de inatividade. O botão **Sair** encerra a sessão.

Para gerar o executável:

```bash
mvn clean package
java -jar target/ambmao-0.0.1-SNAPSHOT.jar
```

Para ambiente publicado com HTTPS, ative `SPRING_PROFILES_ACTIVE=prod`. Esse perfil exige cookies de sessão seguros e considera os headers do proxy da hospedagem. A aplicação não foi publicada automaticamente.

## Estrutura e rotas

- `web.ambmao.cartilha`: entidade, repository, specifications, service e controller.
- `cartilha.dto`: `CartilhaForm`, `CartilhaResponse` e `CartilhaSearchRequest`.
- `web.ambmao.profissional`: entidade e repository dos profissionais.
- `web.ambmao.seguranca`: autenticação, autorização e provisionamento inicial.
- `web.ambmao.core`: validação de URLs e controllers/atributos compartilhados.
- `db/migration/V01__cria_cartilha_e_profissional.sql`: schema inicial gerenciado pelo Flyway.
- `templates/` e `static/`: front integrado com Thymeleaf.

| Método | Rota | Acesso |
| --- | --- | --- |
| GET | `/`, `/cartilhas?nomeDoenca=...`, `/login` | Público |
| POST | `/login` | Login com CSRF |
| GET | `/profissional/cartilhas` | Profissional |
| GET | `/profissional/cartilhas/nova` | Profissional |
| POST | `/profissional/cartilhas` | Profissional + CSRF |
| GET | `/profissional/cartilhas/{codigo}/editar` | Profissional |
| PUT | `/profissional/cartilhas/{codigo}` | Profissional + CSRF |
| GET | `/profissional/cartilhas/{codigo}/excluir` | Profissional; tela de confirmação |
| DELETE | `/profissional/cartilhas/{codigo}` | Profissional + CSRF |
| POST | `/logout` | Encerra sessão com CSRF |

O formulário HTML envia POST e `_method=PUT` na edição ou `_method=DELETE` na exclusão, convertido pelo filtro do Spring. Na listagem profissional, **Excluir** abre uma confirmação com o nome da cartilha e opção de cancelar. Somente o envio da confirmação remove o registro; depois ele deixa de aparecer na consulta pública. A exclusão é definitiva. O cabeçalho mostra **Minha conta** com ícone, nome, e-mail, link de gerenciamento e logout para o profissional autenticado. Os dados são renderizados com `th:text`, sem aceitar HTML executável nas cartilhas. Referências aceitam somente URL absoluta HTTP/HTTPS. Não há upload de PDF, programas de exercícios ou recuperação de senha nesta implementação.

## Testar

Na raiz:

```bash
npm test
npm run backend:test
```

Ou, depois de sincronizar o front:

```bash
cd backend
mvn test
```

Os testes de integração usam H2 em memória no perfil `test`, com a mesma migração, e nunca o Supabase. Cobrem renderização, cadastro/edição/exclusão, validação, busca, escaping, 404, sintomas em tópicos, login real, menu de conta, hash de senha, autorização, CSRF e logout.

Última verificação, em 24/10/2026:

- Front: testes aprovados e sincronização Sass/templates/assets concluída.
- Java: **16 testes aprovados**, sem falhas, erros ou testes ignorados. Incluem sintomas em tópicos, HTML escapado, confirmação de exclusão, bloqueio sem autenticação/perfil/CSRF e conta do profissional após login.
- PostgreSQL real: [`supabase/smoke-test.sql`](supabase/smoke-test.sql) aprovado via MCP, com CRUD, constraints, IDs automáticos e isolamento do schema. Os registros desse teste sofrem rollback; as sequences podem avançar.
- Supabase: baseline Flyway versão `1` confirmado após a execução local do backend. As três cartilhas de exemplo foram adicionadas separadamente pelo script manual.

Os testes Java não substituem a verificação da aplicação com o PostgreSQL real. Após configurar um novo ambiente, confira a inicialização, a consulta pública, o login e as operações de gerenciamento pelo navegador.

## Fontes técnicas

- [Supabase com Spring Boot](https://supabase.com/docs/guides/getting-started/quickstarts/spring-boot): JDBC, SSL e conexão por session pooler.
- [Conexões PostgreSQL do Supabase](https://supabase.com/docs/guides/database/connecting-to-postgres).
- [Spring Security: DaoAuthenticationProvider](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/dao-authentication-provider.html).
