-- Exemplos educativos solicitados pelo usuário; referências conferidas em 24/10/2026.
-- Executar manualmente via MCP execute_sql; não é uma migração automática Flyway.
-- Preserva cartilhas existentes com o mesmo nome normalizado.
with exemplos (nome_doenca, nome_busca, o_que_e, sinais_sintomas, orientacoes_gerais, fonte_titulo, fonte_url) as (
    values
    ('Síndrome do túnel do carpo', 'sindrome do tunel do carpo', 'É a compressão do nervo mediano na região do punho, que pode provocar alterações de sensibilidade e desconforto na mão.', 'Formigamento ou dormência na mão e nos dedos.
Dor na mão, no punho ou no braço, muitas vezes pior à noite.
Fraqueza do polegar ou dificuldade para segurar objetos.', 'Reduza atividades que exijam dobrar o punho repetidamente ou apertar objetos com força.
Converse com a equipe de saúde sobre adaptações nas suas atividades.
Se os sintomas persistirem ou piorarem, procure avaliação profissional.', 'NHS — Carpal tunnel syndrome', 'https://www.nhs.uk/conditions/carpal-tunnel-syndrome/'),
    ('Tenossinovite de De Quervain', 'tenossinovite de de quervain', 'É uma condição em que os tendões que movimentam o polegar ficam irritados no trajeto junto ao punho, dificultando seu deslizamento.', 'Dor do lado do polegar, próxima ao punho.
Inchaço e sensibilidade na região.
Desconforto ao segurar objetos, levantar peso ou girar o punho.', 'Evite ou adapte movimentos que aumentem a dor, especialmente ao segurar ou levantar objetos.
Converse com a equipe sobre formas de realizar suas tarefas com menos desconforto.
Siga o plano de cuidado indicado para o seu caso.', 'AAOS — De Quervain’s tenosynovitis', 'https://www.orthoinfo.org/diseases--conditions/de-quervains-tendinosis/'),
    ('Dedo em gatilho', 'dedo em gatilho', 'É uma dificuldade de deslizamento do tendão de um dedo ou do polegar, que pode causar engasgos ou travamentos ao dobrar e esticar.', 'Estalos, sensação de engasgo ou travamento do dedo.
Dor ou sensibilidade na base do dedo, na palma da mão.
Rigidez, frequentemente mais perceptível ao acordar.', 'Reduza tarefas que envolvam apertar ou pinçar objetos repetidamente.
Peça orientação à equipe de saúde sobre adaptações na rotina.
Se o dedo permanecer travado ou os sintomas dificultarem suas atividades, procure avaliação.', 'AAOS — Trigger finger', 'https://www.orthoinfo.org/diseases--conditions/trigger-finger/')
)
insert into ambmao.cartilha (nome_doenca, nome_busca, o_que_e, sinais_sintomas, orientacoes_gerais, fonte_titulo, fonte_url, criado_em, atualizado_em)
select e.*, now(), now() from exemplos e
where not exists (select 1 from ambmao.cartilha c where c.nome_busca = e.nome_busca)
returning codigo, nome_doenca;
