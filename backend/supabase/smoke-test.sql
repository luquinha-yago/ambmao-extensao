-- Executar via MCP execute_sql. Todas as linhas de teste sofrem rollback.
-- As sequences podem avancar, mesmo com rollback.
begin;
do $$
declare
    cartilha_id bigint;
    profissional_id bigint;
    email_teste text := 'smoke-' || gen_random_uuid()::text || '@example.test';
begin
    assert not has_schema_privilege('anon', 'ambmao', 'USAGE'), 'anon acessa o schema';
    assert not has_schema_privilege('authenticated', 'ambmao', 'USAGE'), 'authenticated acessa o schema';
    assert not has_table_privilege('anon', 'ambmao.profissional', 'SELECT'), 'anon acessa profissionais';
    assert not has_table_privilege('authenticated', 'ambmao.profissional', 'SELECT'), 'authenticated acessa profissionais';

    insert into ambmao.cartilha (nome_doenca, nome_busca, o_que_e, sinais_sintomas,
            orientacoes_gerais, criado_em, atualizado_em)
        values ('Teste técnico temporário', 'teste tecnico temporario', 'Definição de teste',
            'Sintoma de teste', 'Orientação de teste', now(), now())
        returning codigo into cartilha_id;
    assert cartilha_id is not null, 'Identity de cartilha ausente';
    assert (select count(*) = 1 from ambmao.cartilha
        where codigo = cartilha_id and nome_busca like '%tecnico%'), 'Consulta falhou';
    update ambmao.cartilha set nome_doenca = 'Teste atualizado', atualizado_em = now()
        where codigo = cartilha_id;
    assert (select nome_doenca = 'Teste atualizado' from ambmao.cartilha
        where codigo = cartilha_id), 'Atualização falhou';

    begin
        update ambmao.cartilha set nome_doenca = '   ' where codigo = cartilha_id;
        raise exception 'Nome em branco foi aceito';
    exception when check_violation then null;
    end;
    begin
        update ambmao.cartilha set o_que_e = '   ' where codigo = cartilha_id;
        raise exception 'Conteúdo em branco foi aceito';
    exception when check_violation then null;
    end;
    begin
        update ambmao.cartilha set sinais_sintomas = null where codigo = cartilha_id;
        raise exception 'Conteúdo nulo foi aceito';
    exception when not_null_violation then null;
    end;

    insert into ambmao.profissional (nome, email, senha_hash)
        values ('Teste temporário', email_teste, 'HASH_FICTICIO_SEM_LOGIN')
        returning codigo into profissional_id;
    assert (select ativo from ambmao.profissional where codigo = profissional_id),
        'Profissional deveria iniciar ativo';
    begin
        insert into ambmao.profissional (nome, email, senha_hash)
            values ('Duplicado', email_teste, 'HASH_FICTICIO_SEM_LOGIN');
        raise exception 'Email duplicado foi aceito';
    exception when unique_violation then null;
    end;
end $$;
select 'PASS: CRUD, identity, constraints e isolamento do schema' as resultado;
rollback;
