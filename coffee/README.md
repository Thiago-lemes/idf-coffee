# coffee — PDV

Projeto PDV (ponto de venda) chamado "coffee" desenvolvido em Kotlin com Spring Boot.

Resumo
- Nome: coffee
- Artefato: `coffee:0.0.1-SNAPSHOT` (ver `pom.xml`)
- Linguagens: Kotlin (principal), Java (runtime target)
- Propósito: Backend de um sistema PDV com persistência em MySQL, migrações com Flyway e integração com RabbitMQ.

Tecnologias principais
- Java 21
- Kotlin 1.9.x
- Spring Boot 3.5.5
- Spring Web, Spring Data JPA, Spring Security
- Flyway (migrações)
- MySQL (conector: `mysql-connector-j`)
- RabbitMQ (cliente e starter)
- springdoc-openapi (UI / OpenAPI)
- jackson-module-kotlin
- java-jwt (JWT handling)

Pré-requisitos
- Java 21 (JDK)
- Maven
- Banco MySQL disponível (para o profile `dev-mysql`)
- (Opcional) RabbitMQ para integrações assíncronas

Como buildar
No PowerShell, a partir da raiz do projeto (`coffee`):

```powershell
mvn clean package
```

Executando em desenvolvimento
O profile ativo padrão é `dev-mysql` (definido em `src/main/resources/application.yml`), que usa as propriedades em `src/main/resources/application-dev-mySql.yml`.

Exemplo (PowerShell) — exportar variáveis de ambiente e executar com Maven:

```powershell
$env:MYSQL_HOST = 'localhost'
$env:MYSQL_PORT = '3306'
$env:MYSQL_DATABASE = 'coffee'
$env:MYSQL_USER = 'root'
$env:MYSQL_PASSWORD = 'root'
$env:RABBIT_HOST = 'localhost'
$env:RABBIT_PORT = '5672'
$env:RABBIT_USER = 'guest'
$env:RABBIT_PASSWORD = 'guest'

mvn spring-boot:run
```

Executando o JAR gerado
Após `mvn clean package` o JAR típico será `target/coffee-0.0.1-SNAPSHOT.jar`. Para rodar diretamente:

```powershell
java -jar target/coffee-0.0.1-SNAPSHOT.jar
```

Testes
- Rodar todos os testes:

```powershell
mvn test
```
- Os testes estão em `src/test/kotlin/` (ex.: `CoffeeApplicationTests.kt`).

Configuração e variáveis de ambiente
O profile `dev-mysql` (arquivo `src/main/resources/application-dev-mySql.yml`) usa as seguintes variáveis/keys:
- MYSQL_HOST (ex.: localhost)
- MYSQL_PORT (ex.: 3306)
- MYSQL_DATABASE (ex.: coffee)
- MYSQL_USER (ex.: root)
- MYSQL_PASSWORD (ex.: root)
- RABBIT_HOST (ex.: localhost)
- RABBIT_PORT (ex.: 5672)
- RABBIT_USER (ex.: guest)
- RABBIT_PASSWORD (ex.: guest)

Além disso o arquivo define as chaves do RabbitMQ:
- `rabbitmq.exchange` (ex.: `coffee.exchange`)
- `rabbitmq.queue` (ex.: `fechamento.caixa.queue`)
- `rabbitmq.routing-key` (ex.: `fechamento.caixa`)

Migrações (Flyway)
- As migrações são aplicadas automaticamente pelo Flyway quando o profile `dev-mysql` está ativo.
- Arquivos SQL estão em `src/main/resources/db/migration/` — atualmente há scripts V1__... até V9__... que criam as tabelas iniciais.

Classe principal
- A classe principal do Spring Boot está em `src/main/kotlin/org/br/idf/coffee/CoffeeApplication.kt`.

OpenAPI / Swagger UI
- O projeto usa `springdoc-openapi` (dependência em `pom.xml`).
- Endpoints comuns:
  - OpenAPI JSON: `/v3/api-docs`
  - Swagger UI: `/swagger-ui/index.html` ou `/swagger-ui.html` (depende da versão e configuração)

Estrutura do projeto
Abaixo está um resumo da árvore de pastas mais relevante (caminho base: `src/main/kotlin/org/br/idf/coffee`):

- src/
  - main/
    - kotlin/
      - org/br/idf/coffee/
        - auth/            -> Autenticação (controllers, dto, services relacionados a login/registro, tokens JWT)
        - categoria/       -> Recursos de categoria (controller, dto, entity, mapper, repository)
        - produto/         -> Recursos de produto (controllers, dto, entity, mapper, repository)
        - insumo/          -> Recursos de insumos (estoque de insumos, entidades, serviços)
        - estoque/         -> Lógica de estoque e controle de quantidades
        - fluxo_caixa/     -> Fechamento de caixa / movimentação financeira
        - transacoes/      -> Transações e itens (vendas, recebimentos)
        - usuario/         -> Entidade e serviço de usuários
        - rabbit/          -> Integração com RabbitMQ (publishers, consumers, config)
        - security/        -> Configurações de segurança (filters, providers, config do Spring Security)
        - config/          -> Beans de configuração (datasource, jackson, bean extras)
        - ultils/          -> Helpers e utilitários
        - CoffeeApplication.kt (classe principal)

Responsabilidades por camada
- Controller (API): recebe requisições HTTP, valida entrada inicial, mapeia para DTOs e delega para Services.
- DTO / Mapper: objetos de transferência entre camadas; mappers transformam Entity ↔ DTO.
- Service (Application): lógica de negócio, transações, orquestração entre repositórios e infra (ex.: publicar eventos RabbitMQ).
- Repository (Persistence): interfaces Spring Data JPA que tratam persistência e consultas.
- Entity (Domain): classes mapeadas para o banco (JPA / Hibernate).
- Config / Infra: configuração de datasource, Flyway, RabbitMQ e segurança. Consumidores/producers do RabbitMQ ficam em `rabbit`.

Arquitetura (visão geral)
O projeto segue um padrão em camadas com separação clara de responsabilidades:

1. API Layer (Controllers)
   - Recebe requisições REST, valida e converte para DTOs.
2. Application / Service Layer
   - Contém regras de negócio e orquestração (uso de repositórios, validação complexa, envio de eventos).
3. Domain Layer (Entities)
   - Representações persistidas dos modelos do negócio.
4. Persistence Layer (Repositories)
   - Responsável por conversas com o BD via Spring Data JPA.
5. Infrastructure
   - Integrações externas (RabbitMQ, serviços externos, config de datasource, Flyway).
6. Security
   - Camada de autenticação/autorização (JWT, filtros do Spring Security).

Fluxo típico de uma requisição de escrita
1. Cliente → Controller (recebe JSON) → validadores iniciais
2. Controller → Service (DTO convertido para entidade ou usado diretamente)
3. Service → Repositório (persistência) + lógica adicional (ex.: publicar evento no RabbitMQ)
4. Service retorna DTO/resultado para o Controller → resposta HTTP

Boas práticas e pontos de atenção
- Mantenha DTOs separados das Entities para evitar vazamento de camadas.
- Centralize configurações sensíveis em variáveis de ambiente (não commit em VCS).
- Migrations do Flyway são a fonte de verdade para o esquema do BD — atualize sempre que o modelo mudar.
- Use perfis do Spring (`dev-mysql`, `prod`, `test` etc.) para separação de configurações.
