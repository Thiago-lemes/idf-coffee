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

---

## Docker & Containerização

Este projeto está totalmente containerizado e pronto para deployment com Docker.

### Arquivos Docker fornecidos

| Arquivo | Propósito |
|---------|-----------|
| `Dockerfile` | Multi-stage build: Maven para compilar, eclipse-temurin:21-jre-alpine para runtime |
| `docker-compose.yml` | Ambiente de **desenvolvimento**: app + MySQL + RabbitMQ com healthchecks |
| `docker-compose.prod.yml` | Referência para **produção** com managed services externos |
| `.dockerignore` | Exclui arquivos desnecessários (node_modules, .git, test files, etc.) |
| `application-docker.yml` | Profile Spring Boot para ambiente containerizado |
| `.env.docker` | Variáveis de ambiente padrão para dev (valores seguros) |

### Quick Start com Docker Compose (Desenvolvimento)

#### Opção 1: Usando docker-compose (recomendado)

**Pré-requisitos:**
- Docker e Docker Compose instalados
- Windows: PowerShell recomendado

**Passos:**

1. **Navegar até a pasta do projeto:**
```powershell
cd coffee
```

2. **Iniciar os serviços:**
```powershell
# Linux/Mac
docker compose --env-file .env.docker up -d

# Windows PowerShell
docker compose --env-file .env.docker up -d
```

3. **Acompanhar os logs:**
```powershell
docker compose logs -f coffee-app
```

4. **Parar os serviços:**
```powershell
docker compose down
```

**Acesso aos serviços:**
- **Aplicação:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Health Check:** http://localhost:8080/actuator/health
- **MySQL:** localhost:3306 (user: `coffee_user`, password: `coffeepwd123`)
- **RabbitMQ Management:** http://localhost:15672 (user: `guest`, password: `guest`)

#### Opção 2: Usando scripts helper

**Windows:**
```powershell
.\docker-helper.bat start
.\docker-helper.bat logs-app
.\docker-helper.bat stop
```

**Linux/Mac:**
```bash
bash docker-helper.sh start
bash docker-helper.sh logs-app
bash docker-helper.sh stop
```

**Comandos disponíveis:**
```
start       - Iniciar todos os serviços
stop        - Parar todos os serviços
restart     - Reiniciar serviços
logs        - Ver todos os logs
logs-app    - Ver logs da aplicação
build       - Compilar imagem Docker
clean       - Remover containers e volumes
health      - Verificar saúde dos serviços
help        - Mostrar ajuda
```

### Build da Docker Image

#### Build local (desenvolvimento)
```powershell
docker compose build coffee-app
```

#### Build com tag para produção
```powershell
docker build -t coffee:latest -t coffee:prod .
```

#### Push para registry (ex: Docker Hub, ECR)
```powershell
$env:REGISTRY_URL = "your-registry.com"
docker tag coffee:latest ${env:REGISTRY_URL}/coffee:latest
docker push ${env:REGISTRY_URL}/coffee:latest
```

### Variáveis de Ambiente

#### Desenvolvimento (.env.docker)
```dotenv
# Spring
SPRING_PROFILES_ACTIVE=docker
SERVER_PORT=8080

# MySQL
MYSQL_HOST=mysql
MYSQL_DATABASE=coffee
MYSQL_USER=coffee_user
MYSQL_PASSWORD=coffeepwd123

# RabbitMQ
RABBIT_HOST=rabbitmq
RABBIT_USER=guest
RABBIT_PASSWORD=guest

# Security (MUDAR EM PRODUÇÃO!)
JWT_SECRET=your-development-secret-key-here-min-32-chars!
JWT_EXPIRATION_MS=86400000
```

#### Produção
Para produção, **NUNCA** use `.env` files. Use:
- **Railway.app:** Secrets integrados no dashboard
- **Fly.io:** `flyctl secrets set`
- **AWS:** AWS Secrets Manager ou Systems Manager Parameter Store
- **Google Cloud:** Cloud Secret Manager
- **Azure:** Azure Key Vault
- **Heroku:** Config Vars

**Exemplo Railway:**
```bash
# Definir secrets via CLI
railway variables set JWT_SECRET="seu-secret-seguro"
railway variables set SPRING_DATASOURCE_PASSWORD="db-password-segura"
```

**Exemplo Fly.io:**
```bash
# Definir secrets
flyctl secrets set JWT_SECRET="seu-secret-seguro"
flyctl secrets set SPRING_DATASOURCE_PASSWORD="db-password-segura"
```

### Segurança em Docker

#### Implementações incluídas no Dockerfile:
✅ **Non-root user:** Aplicação roda como `spring` (UID 1000), não como `root`
✅ **Imagem minimal:** Usa `eclipse-temurin:21-jre-alpine` (~180MB vs ~500MB)
✅ **Multi-stage build:** Apenas runtime necessário na imagem final (sem Maven, compilador)
✅ **dumb-init:** Proper signal handling para graceful shutdown
✅ **Healthchecks:** Docker Compose monitora saúde dos serviços
✅ **Network isolation:** Services se comunicam via docker network privada

#### Boas práticas adicionais:
- ✅ Variáveis sensíveis via secrets manager (nunca em código)
- ✅ HTTPS/TLS termination via reverse proxy (Nginx, Cloudflare, etc.)
- ✅ Rate limiting e DDoS protection na edge
- ✅ Scan de vulnerabilidades: `docker scout cves`
- ✅ Atualizações regulares de base images

### Troubleshooting

#### Erro: "port 3306 already in use"
```powershell
# Encontrar processo usando a porta
Get-NetTCPConnection -LocalPort 3306

# Ou parar containers existentes
docker compose down
docker ps -a  # verificar se há outros containers rodando
```

#### Erro: "failed to solve: failed to calculate checksum of ref"
```powershell
# Limpar build cache
docker builder prune

# Reconstruir sem cache
docker compose build --no-cache coffee-app
```

#### Logs não aparecem
```powershell
# Verificar status dos containers
docker compose ps

# Ver logs detalhados
docker compose logs --tail=100 coffee-app
```

#### Aplicação não conecta no MySQL
```powershell
# Verificar healthcheck do MySQL
docker compose ps

# Executar comando de teste no MySQL
docker compose exec mysql mysqladmin ping -h localhost

# Aumentar tempo de startup
# (editar docker-compose.yml, aumentar retries no healthcheck)
```

### Deployment em Plataformas Gerenciadas

#### Railway.app (Recomendado - mais fácil)

1. **Conectar repositório Git** ao Railway
2. **Configurar variáveis** no dashboard (Secrets)
3. **Configurar banco de dados** (MySQL add-on)
4. **Deploy automático** a cada push

```bash
# CLI alternativo
railway login
railway init
railway variables set JWT_SECRET="seu-secret"
railway up
```

#### Fly.io

1. **Instalar Fly CLI:** https://fly.io/docs/getting-started/installing-fly/
2. **Autenticar:** `flyctl auth login`
3. **Criar app:** `flyctl app create coffee-app`
4. **Configurar variáveis:** `flyctl secrets set JWT_SECRET="seu-secret"`
5. **Fazer deploy:** `flyctl deploy`

#### AWS ECS/Fargate

1. Fazer push da imagem para ECR
2. Criar task definition apontando para a imagem
3. Configurar RDS MySQL e ElastiCache RabbitMQ (ou CloudAMQP)
4. Criar ECS service

#### Google Cloud Run

```bash
# Fazer push para Google Container Registry
gcloud builds submit --tag gcr.io/PROJECT/coffee:latest

# Deploy
gcloud run deploy coffee --image gcr.io/PROJECT/coffee:latest \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod"
```

### Usar Makefile (Linux/Mac)

Se estiver em Linux/Mac, pode usar o Makefile incluído:

```bash
make help              # Ver todos os comandos
make docker-up        # Iniciar services
make docker-down      # Parar services
make docker-logs      # Ver logs
make docker-clean     # Limpar tudo
make build            # Build Maven
make test             # Rodar testes
```

### Monitoramento & Observabilidade

#### Health Checks integrados:
```bash
# Verificar saúde dos serviços
curl http://localhost:8080/actuator/health

# Ver métricas
curl http://localhost:8080/actuator/metrics

# Detailed health info (requer autorização)
curl http://localhost:8080/actuator/health/db
curl http://localhost:8080/actuator/health/diskSpace
```

#### Logs centralizados (produção):
- **Datadog:** https://www.datadoghq.com
- **New Relic:** https://newrelic.com
- **Elastic/ELK:** https://www.elastic.co
- **Splunk:** https://www.splunk.com
- **CloudWatch (AWS):** https://aws.amazon.com/cloudwatch/
