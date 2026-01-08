# Coffee ☕ - PDV (Ponto de Venda)

Backend de um sistema **PDV (Ponto de Venda)** moderno, desenvolvido em **Kotlin** com **Spring Boot 3.5.5**, projetado para gerenciar transações, inventário, usuários e integração com fila de mensagens.

---

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Setup](#instalação-e-setup)
- [Configuração de Ambiente](#configuração-de-ambiente)
- [Como Executar](#como-executar)
- [Docker e Docker Compose](#docker-e-docker-compose)
- [Deployment (Fly.io)](#deployment-flyio)
- [Segurança](#segurança)
- [Endpoints e Documentação](#endpoints-e-documentação)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Visão Geral

O projeto **Coffee** é um backend robusto para sistemas de PDV que oferece:

- ✅ Autenticação e autorização com JWT
- ✅ Gestão de usuários, categorias, produtos e insumos
- ✅ Controle de caixa e transações
- ✅ Integração com RabbitMQ para processamento assíncrono
- ✅ Migrações automáticas com Flyway
- ✅ Suporte para múltiplos ambientes (dev, test, prod)
- ✅ Documentação automática com OpenAPI/Swagger

### Artefato

- **GroupId**: `org.br.idf`
- **ArtifactId**: `coffee`
- **Versão**: `0.0.1-SNAPSHOT`

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Descrição |
|---|---|---|
| **Java** | 21 | Runtime |
| **Kotlin** | 1.9.25 | Linguagem Principal |
| **Spring Boot** | 3.5.5 | Framework Web |
| **Spring Data JPA** | 3.5.5 | ORM |
| **Spring Security** | 3.5.5 | Autenticação/Autorização |
| **MySQL** | 8.0+ | Banco de Dados |
| **Flyway** | Latest | Migrações |
| **RabbitMQ** | 3.12+ | Message Broker |
| **JWT** | 4.4.0 | Token Management |
| **OpenAPI/Swagger** | 2.3.0 | Documentação |

---

## 🏗️ Arquitetura

### Estrutura de Diretórios

```
coffee/
├── src/
│   ├── main/
│   │   ├── kotlin/org/br/idf/coffee/
│   │   │   ├── CoffeeApplication.kt       # Main entry point
│   │   │   ├── auth/                      # Authentication & JWT
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   └── service/
│   │   │   ├── categoria/                 # Product categories
│   │   │   ├── produto/                   # Products
│   │   │   ├── insumo/                    # Supplies/Ingredients
│   │   │   ├── estoque/                   # Stock management
│   │   │   ├── usuario/                   # Users
│   │   │   ├── transacoes/                # Transactions
│   │   │   ├── fluxo_caixa/               # Cash flow
│   │   │   ├── rabbit/                    # RabbitMQ integration
│   │   │   ├── security/                  # Security config
│   │   │   ├── config/                    # App configuration
│   │   │   └── utils/                     # Utilities
│   │   └── resources/
│   │       ├── application.yml            # Main config
│   │       ├── application-dev-mySql.yml  # Development profile
│   │       ├── application-prod.yml       # Production profile
│   │       ├── application-security.yml   # Security config
│   │       └── db/migration/              # Flyway migrations
│   │           ├── V1__CREATE_TABLE_USUARIO.SQL
│   │           ├── V2__CREATE_TABLE_CATEGORIA.SQL
│   │           └── ...
│   └── test/
│       └── kotlin/org/br/idf/coffee/
├── pom.xml                                 # Maven configuration
├── Dockerfile                              # Docker image
├── docker-compose.yml                      # Local development stack
├── .env.example                            # Environment variables template
├── .env.prod                               # Production template
└── README.md                               # This file
```

### Camadas da Aplicação

```
┌─────────────────────────────────────────┐
│         Client (Web/Mobile)             │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      API Layer (Controllers)            │
│  - JWT Authentication                  │
│  - Request Validation                  │
│  - Error Handling                       │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│     Business Logic (Services)           │
│  - Core business rules                  │
│  - Transaction processing               │
│  - RabbitMQ publishing                  │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│    Data Access (JPA Repositories)       │
│  - Database queries                     │
│  - Entity relationships                 │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│        Database (MySQL)                 │
│  - Relational data storage              │
│  - Migrations with Flyway               │
└─────────────────────────────────────────┘
```

---

## 📦 Pré-requisitos

### Desenvolvimento Local

- **Java 21** (JDK) - [Download](https://adoptium.net/temurin/releases/?version=21)
- **Maven 3.9+** - [Download](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** - [Download](https://dev.mysql.com/downloads/)
- **RabbitMQ 3.12+** - [Download](https://www.rabbitmq.com/download.html)
- **Git** - [Download](https://git-scm.com/)

### Com Docker

- **Docker 20.10+** - [Install](https://docs.docker.com/get-docker/)
- **Docker Compose 2.0+** - [Install](https://docs.docker.com/compose/install/)

### Deploy em Produção

- **Fly.io Account** - [Sign up](https://fly.io/)
- **Flyctl CLI** - [Install](https://fly.io/docs/hands-on/install-flyctl/)

---

## ⚙️ Instalação e Setup

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/coffee.git
cd coffee
```

### 2. Configurar Variáveis de Ambiente

```bash
# Copiar template para arquivo local
cp .env.example .env

# Editar .env com seus valores
nano .env  # ou use seu editor favorito
```

**Variáveis essenciais para desenvolvimento**:

```env
SPRING_PROFILES_ACTIVE=dev-mysql
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/coffee?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
RABBIT_HOST=localhost
RABBIT_PORT=5672
RABBIT_USER=guest
RABBIT_PASSWORD=guest
JWT_SECRET=seu-secret-muito-secreto-com-32-chars-minimo
```

### 3. Instalar Dependências

```bash
mvn clean install
```

---

## 🌍 Configuração de Ambiente

O projeto suporta **múltiplos profiles** para diferentes ambientes:

### Profiles Disponíveis

| Profile | Uso | Arquivo |
|---------|-----|---------|
| `dev-mysql` | Desenvolvimento com MySQL | `application-dev-mySql.yml` |
| `test` | Testes com H2 (em memória) | `application-h2.test` |
| `prod` | Produção | `application-prod.yml` |

### Ativar um Profile

```bash
# Via variável de ambiente
export SPRING_PROFILES_ACTIVE=prod

# Via linha de comando
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev-mysql"

# Via Docker
docker run -e SPRING_PROFILES_ACTIVE=prod ...
```

### Variáveis de Ambiente Principais

#### 🔐 Segurança

```env
# JWT Secret (OBRIGATÓRIO em produção)
JWT_SECRET=gerar-com: openssl rand -base64 32
JWT_EXPIRATION_MS=3600000  # 1 hora em ms

# CORS
CORS_ALLOWED_ORIGINS=https://seudominio.com,https://www.seudominio.com

# SSL/HTTPS
SSL_ENABLED=true
SSL_KEYSTORE_PATH=/app/secrets/keystore.p12
SSL_KEYSTORE_PASSWORD=sua-senha-keystore
```

#### 📊 Banco de Dados

```env
SPRING_DATASOURCE_URL=jdbc:mysql://HOST:PORT/DATABASE
SPRING_DATASOURCE_USERNAME=usuario
SPRING_DATASOURCE_PASSWORD=senha

# Pool Connection
DB_POOL_SIZE=20
DB_POOL_MIN_IDLE=10
```

#### 🐰 RabbitMQ

```env
RABBIT_HOST=rabbitmq.seudominio.com
RABBIT_PORT=5671
RABBIT_USER=usuario
RABBIT_PASSWORD=senha

RABBITMQ_EXCHANGE=coffee.exchange.prod
RABBITMQ_QUEUE=fechamento.caixa.queue.prod
RABBITMQ_ROUTING_KEY=fechamento.caixa
```

#### 📝 Logging

```env
LOG_LEVEL=INFO   # Valores: TRACE, DEBUG, INFO, WARN, ERROR
```

---

## 🚀 Como Executar

### Modo Desenvolvimento (Sem Docker)

#### 1. Iniciar MySQL e RabbitMQ (Via Docker)

```bash
# Iniciar apenas MySQL
docker run -d --name coffee-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=coffee \
  -p 3306:3306 \
  mysql:8.0-alpine

# Iniciar apenas RabbitMQ
docker run -d --name coffee-rabbit \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3.12-management-alpine
```

#### 2. Executar a Aplicação

```bash
# Via Maven
mvn spring-boot:run

# Via IDE (IntelliJ IDEA, VS Code)
# Clique em "Run" no arquivo CoffeeApplication.kt
```

#### 3. Acessar a Aplicação

- **API**: http://localhost:8080/api
- **Swagger/OpenAPI**: http://localhost:8080/api/swagger-ui.html
- **Health**: http://localhost:8080/api/actuator/health
- **RabbitMQ Console**: http://localhost:15672 (guest/guest)

---

## 🐳 Docker e Docker Compose

### Usando Docker Compose (Recomendado)

```bash
# Iniciar todos os serviços
docker-compose up -d

# Ver logs
docker-compose logs -f coffee-app

# Parar serviços
docker-compose down

# Parar e remover volumes
docker-compose down -v
```

**Serviços iniciados**:
- ✅ MySQL (porta 3306)
- ✅ RabbitMQ (porta 5672, UI em 15672)
- ✅ Coffee App (porta 8080)

### Build Manual do Docker

```bash
# Build da imagem
docker build -t coffee:latest .

# Executar container
docker run -d \
  --name coffee-app \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/coffee" \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=senha \
  -e JWT_SECRET=$(openssl rand -base64 32) \
  coffee:latest
```

---

## ☁️ Deployment (Fly.io)

### 1. Login no Fly.io

```bash
flyctl auth login
```

### 2. Criar App (primeira vez)

```bash
flyctl launch
```

Responda às perguntas:
- App name: `coffee-pdv`
- Region: `gru` (São Paulo)
- Database: `MySQL` (opcional - usar managed service)
- Postgres: `No`

### 3. Configurar Secrets

```bash
# Gerar JWT Secret seguro
JWT_SECRET=$(openssl rand -base64 32)

# Definir secrets no Fly.io
flyctl secrets set \
  SPRING_PROFILES_ACTIVE=prod \
  JWT_SECRET=$JWT_SECRET \
  SPRING_DATASOURCE_URL="jdbc:mysql://seu-db-host:3306/coffee" \
  SPRING_DATASOURCE_USERNAME=admin \
  SPRING_DATASOURCE_PASSWORD=sua-senha-super-segura \
  RABBIT_HOST=seu-rabbitmq-host \
  RABBIT_USER=usuario \
  RABBIT_PASSWORD=senha

# Verificar secrets
flyctl secrets list
```

### 4. Configurar fly.toml

```toml
app = 'coffee-pdv'
primary_region = 'gru'

[build]
  # Dockerfile será usado automaticamente

[env]
  SPRING_PROFILES_ACTIVE = "prod"
  LOG_LEVEL = "WARN"

[[services]]
  protocol = "tcp"
  internal_port = 8080
  
  [services.http_service]
    force_https = true
    auto_stop_machines = 'stop'
    auto_start_machines = true
    min_machines_running = 0
```

### 5. Deploy

```bash
# Deploy com Fly.io
flyctl deploy

# Monitorar deployment
flyctl logs

# Status da app
flyctl status
```

### 6. Acessar Aplicação

```bash
# Obter URL da app
flyctl info

# Acessar
open https://coffee-pdv.fly.dev/api/swagger-ui.html
```

---

## 🔒 Segurança

### Boas Práticas Implementadas

#### 1. **Autenticação JWT**

```kotlin
// Controller exemplo
@PostMapping("/login")
fun login(@RequestBody credentials: LoginRequest): TokenResponse {
    val token = authService.authenticate(credentials.username, credentials.password)
    return TokenResponse(token)
}

// Usando em endpoints protegidos
@GetMapping("/me")
@PreAuthorize("hasRole('USER')")
fun getCurrentUser(@AuthenticationPrincipal user: UserDetails): UserResponse {
    return UserResponse.from(user)
}
```

#### 2. **Usuário Não-Root em Containers**

```dockerfile
# Dockerfile executa como usuario 'spring' (uid 1000)
USER spring:spring
```

#### 3. **Variaveis de Ambiente Sensíveis**

- ❌ **Não commitir** `.env` ou `.env.prod` no Git
- ✅ **Usar** Fly.io Secrets, AWS Secrets Manager, ou Vault
- ✅ **Rotacionar** JWT_SECRET regularmente

#### 4. **CORS Restrictivo**

```yaml
# application-security.yml
spring:
  web:
    cors:
      allowed-origins: https://yourdomain.com
      allowed-methods: GET,POST,PUT,DELETE
      allow-credentials: true
```

#### 5. **HTTPS Obrigatório em Produção**

```env
SSL_ENABLED=true
SSL_KEYSTORE_PATH=/app/secrets/keystore.p12
```

#### 6. **Validação de Entrada**

```kotlin
@PostMapping
fun create(@Valid @RequestBody dto: CreateUserDTO): UserResponse {
    return userService.create(dto)
}
```

#### 7. **SQL Injection Prevention**

- Usar JPA/Hibernate (prepared statements automáticos)
- Nunca concatenar queries SQL manualmente

#### 8. **Rate Limiting (Implementar)**

```yaml
# TODO: Adicionar rate limiter via Spring Cloud
```

### Checklist de Segurança para Produção

- [ ] JWT_SECRET é uma string random de 32+ caracteres
- [ ] Database tem user diferente de root com permissões restritas
- [ ] RabbitMQ tem credentials customizadas
- [ ] CORS está restrictivo ao seu domínio
- [ ] HTTPS está ativo
- [ ] Logs não expõem informações sensíveis
- [ ] Health endpoint está protegido ou desabilitado
- [ ] Secrets estão em Fly.io Secrets, não em fly.toml
- [ ] Database backups estão configurados
- [ ] SSL certificates são válidos e não expirados

---

## 📚 Endpoints e Documentação

### OpenAPI/Swagger

A documentação interativa está disponível em:

```
http://localhost:8080/api/swagger-ui.html
```

### Endpoints Principais

#### 🔐 Autenticação

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/auth/login` | Login | ❌ |
| POST | `/api/auth/register` | Registrar novo usuário | ❌ |
| POST | `/api/auth/refresh` | Refresh token | ✅ |
| GET | `/api/auth/me` | Dados do usuário atual | ✅ |

#### 👥 Usuários

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/usuarios` | Listar usuários | ✅ |
| GET | `/api/usuarios/{id}` | Obter usuário | ✅ |
| PUT | `/api/usuarios/{id}` | Atualizar usuário | ✅ |
| DELETE | `/api/usuarios/{id}` | Deletar usuário | ✅ |

#### 📦 Produtos

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/produtos` | Listar produtos | ✅ |
| GET | `/api/produtos/{id}` | Obter produto | ✅ |
| POST | `/api/produtos` | Criar produto | ✅ |
| PUT | `/api/produtos/{id}` | Atualizar produto | ✅ |

#### 💰 Transações

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/transacoes` | Listar transações | ✅ |
| POST | `/api/transacoes` | Criar transação | ✅ |
| GET | `/api/transacoes/{id}` | Obter transação | ✅ |

#### 🏥 Health/Status

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/actuator/health` | Status da aplicação | ❌ |
| GET | `/api/actuator/metrics` | Métricas | ✅ |

### Exemplo de Requisição (cURL)

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# Resposta
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

# Usar token em requisições subsequentes
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 🐛 Troubleshooting

### Problema: "Failed to build docker image"

**Causa**: Arquivo `.dockerignore` está excluindo `pom.xml` ou `src`

**Solução**:
```diff
- pom.xml
- src/
```

### Problema: "Cannot connect to MySQL"

**Verificar**:
```bash
# 1. MySQL está rodando?
docker ps | grep mysql

# 2. Credenciais estão corretas?
echo $SPRING_DATASOURCE_URL
echo $SPRING_DATASOURCE_USERNAME

# 3. Portas abertas?
netstat -an | grep 3306
```

### Problema: "JWT token expired"

**Aumentar tempo de expiração**:
```env
JWT_EXPIRATION_MS=7200000  # 2 horas
```

### Problema: "RabbitMQ connection refused"

**Verificar**:
```bash
# RabbitMQ está rodando?
docker ps | grep rabbitmq

# Credenciais corretas?
echo $RABBIT_USER
echo $RABBIT_PASSWORD
```

### Problema: "Database migration failed"

```bash
# Verificar logs do Flyway
mvn flyway:info

# Limpar e reexecutar
mvn flyway:clean flyway:migrate

# Ou via Docker
docker-compose logs mysql
```

### Problema: "Out of memory"

**Aumentar heap size** (Dockerfile ou JVM args):
```bash
-Xmx1g  # 1GB heap
-Xms512m  # 512MB inicial
```

---

## 📝 Logs e Monitoramento

### Ver Logs

```bash
# Via Docker Compose
docker-compose logs -f coffee-app

# Via Fly.io
flyctl logs

# Via Maven
mvn spring-boot:run -X  # Debug mode
```

### Niveis de Log

```env
LOG_LEVEL=DEBUG    # Para desenvolvimento
LOG_LEVEL=INFO     # Normal
LOG_LEVEL=WARN     # Produção
LOG_LEVEL=ERROR    # Apenas erros
```

### Métricas (Actuator)

```bash
# Health
curl http://localhost:8080/api/actuator/health

# Métricas
curl http://localhost:8080/api/actuator/metrics

# Database pool
curl http://localhost:8080/api/actuator/metrics/hikaricp.connections
```

---

## 📚 Recursos Adicionais

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Kotlin for Spring Boot](https://spring.io/guides/tutorials/spring-boot-kotlin/)
- [Spring Security](https://spring.io/projects/spring-security)
- [Flyway Migrations](https://flywaydb.org/)
- [RabbitMQ Tutorials](https://www.rabbitmq.com/getstarted.html)
- [Fly.io Docs](https://fly.io/docs/)

---

## 📄 Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

---

## 👨‍💻 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## 📞 Suporte

- 📧 Email: dev@coffee.local
- 💬 Issues: [GitHub Issues](https://github.com/seu-usuario/coffee/issues)
- 📖 Documentação: Este README

---

**Última atualização**: Janeiro 2026  
**Versão**: 0.0.1-SNAPSHOT

