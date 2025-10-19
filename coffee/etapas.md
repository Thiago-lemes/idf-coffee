🔹 Etapa 1 – Preparação
Spring Boot (Web, Data JPA, Validation, Security, Actuator).

PostgreSQL Driver.

Flyway/Liquibase.

JWT (ex.: spring-security-oauth2-jose).

MapStruct ou Kotlinx Serialization (para DTOs).

🔹 Etapa 3 – Segurança e Autenticação

Implementar login com JWT.

Roles: ADMIN, CAIXA.

Configurar Spring Security com filtros para proteger endpoints.

Criar endpoints de login e refresh token.

🔹 Etapa 4 – Regras de Negócio (Services)

UsuárioService: cadastro, login, perfis.

ProdutoService: CRUD + atualização de estoque.

VendaService:

Criar venda.

Associar itens.

Calcular total.

Atualizar estoque.

FluxoCaixaService: abrir/fechar caixa, calcular totais.

MovimentacaoEstoqueService: entrada/saída manual.

🔹 Etapa 5 – Exposição da API (Controllers)

Criar endpoints REST bem organizados:

/auth → login, registro de usuário.

/produtos → CRUD produtos, pesquisa, estoque baixo.

/vendas → registrar venda, listar, filtrar por período.

/caixa → abrir, fechar, resumo diário.

/estoque → movimentações.

Retornar DTOs (não expor entidades).

Usar ResponseEntity com status corretos (201, 400, 401, 404, 500).

🔹 Etapa 6 – Testes e Qualidade

Testes unitários (JUnit + Mockito).

Testes de integração (SpringBootTest + Testcontainers).

Validar regras críticas:

Venda não pode acontecer se não houver estoque.

Caixa não pode fechar se não foi aberto.

Usuário CAIXA não pode cadastrar produtos.

🔹 Etapa 7 – Deploy do Backend

Gerar imagem Docker do backend.

Subir banco PostgreSQL em container.

Deploy em plataforma simples (Heroku, Render, Railway, ou VPS com Docker Compose).

Configurar backup automático do banco.