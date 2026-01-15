# 📚 Coffee PDV - Documentação Completa

Bem-vindo! Este projeto está **100% documentado e pronto para deployment**.

## 🎯 Começar Aqui

Escolha seu caminho:

### 🚀 **Quer rodar localmente?**
→ Leia: [`DOCKER.md`](./DOCKER.md) - Seção "Quick Start"

```powershell
cd coffee
docker compose --env-file .env.docker up -d
```

### 🌐 **Quer fazer deploy?**
→ Escolha a plataforma:
- ⚡ **Railway.app** (mais fácil) → [`RAILWAY.md`](./RAILWAY.md)
- 🌍 **Fly.io** (global) → [`FLY.md`](./FLY.md)
- ☁️ **AWS/GCP** → [`DOCKER.md`](./DOCKER.md) - Seção "Produção"

### 📖 **Quer entender a arquitetura?**
→ Leia: [`ARCHITECTURE.md`](./ARCHITECTURE.md)

### 💻 **Quer informações gerais?**
→ Leia: [`README.md`](./coffee/README.md)

---

## 📋 Mapa de Documentação

| Documento | Para Quem | Tempo de Leitura |
|-----------|-----------|-----------------|
| **[README.md](./coffee/README.md)** | Todos | 10 min |
| **[DOCKER.md](./DOCKER.md)** | Devs interessados em Docker | 15 min |
| **[ARCHITECTURE.md](./ARCHITECTURE.md)** | Devs que vão manutenção código | 20 min |
| **[RAILWAY.md](./RAILWAY.md)** | Quer deploy simples | 10 min |
| **[FLY.md](./FLY.md)** | Quer deploy global | 15 min |

---

## 🗂️ Arquivos Criados

### 📄 Documentação

```
coffee/
├── README.md                    # Documentação principal
│                               # - Tech stack
│                               # - Como rodar local
│                               # - Estrutura do projeto
│                               # - Arquitetura camadas
│                               # - Docker & deployment
│
├── DOCKER.md                    # Docker & Containerização
│                               # - Quick start (30 seg)
│                               # - Docker compose
│                               # - Build & push
│                               # - Segurança
│                               # - Troubleshooting
│                               # - Deployment (Railway, Fly, AWS, GCP)
│
├── RAILWAY.md                   # Railway.app Deployment
│                               # - Setup passo a passo
│                               # - Variáveis de ambiente
│                               # - Segredos
│                               # - CLI commands
│
├── FLY.md                       # Fly.io Deployment
│                               # - Setup passo a passo
│                               # - Regiões (gru = São Paulo)
│                               # - Banco de dados externo
│                               # - Troubleshooting
│
├── ARCHITECTURE.md              # Arquitetura do Projeto
│                               # - Visão geral
│                               # - Estrutura de pastas
│                               # - Padrão de camadas
│                               # - Exemplos de código
│                               # - Fluxos de requisição
│                               # - Segurança JWT
│
└── INDEX.md                     # Este arquivo (guia rápido)
```

### 🐳 Docker

```
coffee/
├── Dockerfile                   # Multi-stage build
│                               # Stage 1: Maven compile
│                               # Stage 2: Alpine runtime
│
├── docker-compose.yml           # Desenvolvimento
│                               # Services: app + mysql + rabbitmq
│                               # Healthchecks
│                               # Networks & volumes
│
├── docker-compose.prod.yml      # Produção (referência)
│                               # Managed services
│                               # Resource limits
│                               # Auto-restart
│
└── .dockerignore                # Exclusões Docker
                                # target/ (com exceção *.jar)
                                # .git/, .idea/, etc.
```

### ⚙️ Configuração

```
coffee/
├── .env.docker                  # Dev environment vars
│                               # Valores SEGUROS padrão
│                               # MySQL, RabbitMQ, JWT
│
├── .env.example                 # Template (copiar para .env.docker)
│
├── .env.prod                    # Production template
│                               # Valores placeholder
│                               # Instruções de segurança
│
├── src/main/resources/
│   ├── application.yml          # Profile ativo (via env var)
│   ├── application-dev-mySql.yml # Dev com MySQL local
│   ├── application-docker.yml    # Docker (novo!)
│   └── application-prod.yml      # Produção
```

### 🔨 Helpers & Scripts

```
coffee/
├── docker-helper.sh             # Script Unix/Linux/Mac
│                               # start, stop, logs, build, health
│
├── docker-helper.bat            # Script Windows
│                               # Mesmos comandos
│
└── Makefile                     # Shortcuts (Linux/Mac)
                                # make docker-up
                                # make docker-logs
                                # make build
```

### 🧪 Testes (pronto para expansão)

```
coffee/src/test/
└── kotlin/org/br/idf/coffee/
    ├── CoffeeApplicationTests.kt
    └── ... (estrutura pronta para mais testes)
```

---

## 📊 Resumo Rápido

### Tech Stack

```
Language: Kotlin 1.9.25
Runtime: Java 21
Framework: Spring Boot 3.5.5
Database: MySQL 8.0
Message Broker: RabbitMQ 3.13
API Docs: OpenAPI 3.0 / Swagger
Auth: JWT (java-jwt 4.4.0)
Migrations: Flyway
Container: Docker
Deployment: Railway.app, Fly.io, AWS, GCP, etc.
```

### Estrutura de Camadas

```
Controllers (REST API)
    ↓
Services (Business Logic)
    ↓
Repositories (Data Access)
    ↓
Entities (Database Models)
    ↓
MySQL Database + RabbitMQ
```

### Profiles de Ambiente

```
dev-mysql  → MySQL local, dev relaxado
docker     → Docker compose, MySQL container
prod       → Produção, managed services
```

---

## 🚀 Quick Start (Tldr)

### Opção 1: Docker (Recomendado)

```bash
cd coffee
docker compose --env-file .env.docker up -d
# Acesse: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
```

### Opção 2: Maven (Local)

```bash
cd coffee
# Configurar variáveis de ambiente
$env:MYSQL_HOST='localhost'
$env:RABBIT_HOST='localhost'
mvn spring-boot:run
```

### Opção 3: Railway.app (Produção)

```bash
# 1. Conectar GitHub ao Railway
# 2. Railway auto-detecta Java + MySQL
# 3. Configure secrets (JWT_SECRET, DB_PASSWORD)
# 4. Push para main → Deploy automático!
```

---

## 🔐 Segurança

### Incluído no Projeto

✅ Non-root user no Docker
✅ Multi-stage build (menor imagem)
✅ JWT authentication
✅ Spring Security com CORS
✅ Password hashing (BCrypt)
✅ SQL Injection protection (JPA)
✅ HTTPS ready (para reverse proxy)

### Para Produção

⚠️ **NUNCA** commite `.env` com valores reais
✅ Use secrets manager (Railway, Fly.io, AWS Secrets Manager)
✅ Use HTTPS/TLS (via reverse proxy ou CDN)
✅ Ative logs centralizados (Datadog, New Relic, etc.)
✅ Configure rate limiting
✅ Backup automático do banco

---

## 📖 Fluxo de Aprendizado Recomendado

### Dia 1: Setup Básico
1. Ler [`README.md`](./coffee/README.md) - seção "Tech Stack"
2. Rodar com Docker Compose
3. Acessar Swagger UI
4. Fazer um request simples

### Dia 2: Código
1. Ler [`ARCHITECTURE.md`](./ARCHITECTURE.md) - seção "Estrutura de Pastas"
2. Explorar o código-fonte
3. Entender padrão Controller → Service → Repository
4. Rodar testes: `mvn test`

### Dia 3: Deployment
1. Ler [`RAILWAY.md`](./RAILWAY.md) ou [`FLY.md`](./FLY.md)
2. Criar conta na plataforma
3. Fazer primeiro deploy
4. Configurar variáveis de ambiente
5. Monitorar logs

### Dia 4+: Manutenção
1. Adicionar novos endpoints
2. Criar migrations (Flyway)
3. Publicar eventos (RabbitMQ)
4. Escrever testes
5. Deploy contínuo

---

## 🛠️ Troubleshooting Rápido

### Docker não inicia?
```bash
# Limpar containers antigos
docker compose down -v

# Reconstruir sem cache
docker compose build --no-cache coffee-app

# Tentar novamente
docker compose up -d
```

### MySQL não conecta?
```bash
# Verificar se está healthy
docker compose ps

# Testar conexão manualmente
docker compose exec mysql mysqladmin ping -h localhost
```

### Build falha?
```bash
# Limpar Maven cache
mvn clean

# Recriar imagem
docker compose build --no-cache coffee-app
```

### App não inicia?
```bash
# Ver logs detalhados
docker compose logs coffee-app --tail=100

# Verificar profile correto
echo "SPRING_PROFILES_ACTIVE=docker"
```

---

## 📞 Suporte & Documentação Externa

### Oficial
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Spring Security: https://spring.io/projects/spring-security
- Kotlin: https://kotlinlang.org/docs/
- Docker: https://docs.docker.com

### Deployment
- Railway: https://railway.app/docs
- Fly.io: https://fly.io/docs
- AWS: https://aws.amazon.com/docs/
- GCP: https://cloud.google.com/docs

### Extras
- RabbitMQ: https://www.rabbitmq.com/documentation.html
- Flyway: https://flywaydb.org/documentation/
- JWT: https://jwt.io/
- OpenAPI: https://swagger.io/specification/

---

## ✅ Checklist de Implementação

- [x] Dockerfile otimizado (multi-stage)
- [x] docker-compose para dev
- [x] application-docker.yml (novo profile)
- [x] .dockerignore correto
- [x] .env.docker template
- [x] docker-helper scripts (Windows + Unix)
- [x] Makefile
- [x] README.md (atualizado)
- [x] DOCKER.md (completo)
- [x] RAILWAY.md (setup passo a passo)
- [x] FLY.md (setup passo a passo)
- [x] ARCHITECTURE.md (detalhado)
- [x] Healthchecks no docker-compose
- [x] Security no Dockerfile (non-root user)
- [x] JWT secret generation guide

---

## 🎉 Parabéns!

Seu projeto está **100% pronto para**:

✅ Desenvolvimento local (Docker)
✅ CI/CD automático (Railway/Fly)
✅ Deployment em múltiplas plataformas
✅ Escalabilidade horizontal
✅ Monitoramento e observabilidade

**Próximos passos:**
1. Fazer deploy em Railway ou Fly.io
2. Adicionar novos endpoints
3. Escrever testes
4. Configurar alertas
5. Começar a escalar!

---

## 📝 Versão & Changelog

**Versão:** 1.0 (Docker & Deployment)
**Data:** Janeiro 2026

### O que foi adicionado:

- ✨ Dockerfile multi-stage otimizado
- ✨ Docker Compose com MySQL + RabbitMQ
- ✨ application-docker.yml profile
- ✨ Scripts helper (Windows + Unix)
- ✨ Makefile shortcuts
- ✨ Documentação completa (4 guias)
- ✨ Railway.app setup
- ✨ Fly.io setup
- ✨ Troubleshooting guide
- ✨ Architecture documentation

---

**Dúvidas?** Consulte os documentos específicos ou abra uma issue no GitHub.

**Boa sorte com seu Coffee PDV!** ☕

