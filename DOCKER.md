# Docker & Deployment Guide - Coffee PDV

Este guia cobre containerização, desenvolvimento local com Docker Compose e deployment em produção.

## 📋 Índice

1. [Quick Start](#quick-start)
2. [Desenvolvimento Local](#desenvolvimento-local)
3. [Build & Push da Imagem](#build--push)
4. [Segurança & Boas Práticas](#segurança)
5. [Troubleshooting](#troubleshooting)
6. [Deployment em Produção](#produção)

---

## 🚀 Quick Start

### Pré-requisitos

- **Docker**: https://www.docker.com/products/docker-desktop
- **Docker Compose**: Incluído com Docker Desktop
- **Git**: Para clonar o repositório

### Iniciar em 30 segundos

```powershell
# 1. Navegar para o projeto
cd coffee

# 2. Copiar arquivo de variáveis de ambiente
cp .env.example .env.docker  # (ou criar manualmente)

# 3. Iniciar todos os serviços
docker compose --env-file .env.docker up -d

# 4. Acompanhar logs
docker compose logs -f coffee-app
```

✅ Aplicação pronta em http://localhost:8080

---

## 💻 Desenvolvimento Local

### Estrutura de Services

```
┌─────────────────────────────────────────┐
│          Coffee App (Spring Boot)        │
│         Port: 8080 / Profile: docker    │
└────────────┬──────────────┬─────────────┘
             │              │
    ┌────────▼──┐   ┌──────▼────────┐
    │  MySQL 8  │   │   RabbitMQ 3  │
    │ Port 3306 │   │  Port 5672    │
    └───────────┘   │  UI: 15672    │
                    └───────────────┘
```

### Opção A: Docker Compose (Recomendado)

#### Iniciar

```powershell
cd coffee
docker compose --env-file .env.docker up -d
```

#### Monitorar

```powershell
# Ver todos os logs
docker compose logs -f

# Ver apenas app
docker compose logs -f coffee-app

# Ver apenas MySQL
docker compose logs -f mysql

# Ver apenas RabbitMQ
docker compose logs -f rabbitmq
```

#### Parar

```powershell
docker compose down          # Para containers
docker compose down -v       # Para containers + remove volumes (APAGA DADOS!)
```

### Opção B: Scripts Helper (Mais fácil)

#### Windows PowerShell

```powershell
# Ver ajuda
.\docker-helper.bat help

# Iniciar
.\docker-helper.bat start

# Parar
.\docker-helper.bat stop

# Ver logs
.\docker-helper.bat logs-app

# Limpeza completa
.\docker-helper.bat clean
```

#### Linux / macOS

```bash
# Ver ajuda
bash docker-helper.sh help

# Iniciar
bash docker-helper.sh start

# Parar
bash docker-helper.sh stop

# Ver logs
bash docker-helper.sh logs-app
```

### Opção C: Makefile (Linux/Mac)

```bash
make help              # Ver comandos
make docker-up        # Iniciar
make docker-down      # Parar
make docker-logs      # Ver logs
```

---

## 🐳 Build & Push

### Build Local (Desenvolvimento)

```powershell
# Build sem cache (mais lento, mas atualiza tudo)
docker compose build coffee-app

# Build sem cache (forçado)
docker compose build --no-cache coffee-app
```

### Build para Produção

```powershell
# Build com tags
docker build -t coffee:latest .
docker build -t coffee:latest -t coffee:v1.0.0 .

# Verificar imagem
docker images | findstr coffee
```

### Push para Registry

#### Docker Hub

```powershell
# Login
docker login

# Tag
docker tag coffee:latest seu-usuario/coffee:latest

# Push
docker push seu-usuario/coffee:latest

# Verificar
docker search seu-usuario/coffee
```

#### AWS ECR

```powershell
# Login
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 123456789.dkr.ecr.us-east-1.amazonaws.com

# Tag
docker tag coffee:latest 123456789.dkr.ecr.us-east-1.amazonaws.com/coffee:latest

# Push
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/coffee:latest
```

---

## 🔒 Segurança

### Implementações incluídas no Dockerfile

✅ **Non-root User**
- App roda como `spring` (UID 1000)
- Sem permissões de root

✅ **Multi-stage Build**
- Maven builder stage descartado
- Apenas runtime na imagem final (~180MB)

✅ **Minimal Base Image**
- `eclipse-temurin:21-jre-alpine` (~180MB)
- Vs `eclipse-temurin:21-jre` (~500MB+)

✅ **Signal Handling**
- `dumb-init` para SIGTERM/SIGKILL corretos
- Graceful shutdown

✅ **Network Isolation**
- Docker network privada `coffee_network`
- Services isolados do host

### Variáveis de Ambiente Seguras

#### Desenvolvimento (.env.docker - OK para commitar)

```dotenv
# Valores padrão seguros
MYSQL_PASSWORD=coffeepwd123      # OK - senha padrão de dev
JWT_SECRET=dev-secret-key        # OK - apenas para dev local
```

#### Produção (NUNCA use .env!)

Use plataformas gerenciadas:

**Railway.app:**
```bash
railway variables set JWT_SECRET="$(openssl rand -base64 32)"
railway variables set DB_PASSWORD="senhaforte123!@"
```

**Fly.io:**
```bash
flyctl secrets set JWT_SECRET="$(openssl rand -base64 32)"
flyctl secrets set DB_PASSWORD="senhaforte123!@"
```

**AWS Secrets Manager:**
```bash
aws secretsmanager create-secret --name coffee/jwt-secret --secret-string "..."
```

### Gerar JWT Secret Forte

```bash
# Linux/Mac/Windows PowerShell
openssl rand -base64 32

# Exemplo de output:
# 9rXjK2mL5nQ8vP1sT3uW4xYz6aB7cD0eF2gH3iJ4kL5mN6oP7qR8sT9uV0wX1yZ2
```

---

## 🔧 Troubleshooting

### ❌ "Port 3306 already in use"

```powershell
# Encontrar processo
Get-NetTCPConnection -LocalPort 3306 -ErrorAction SilentlyContinue

# Parar containers antigos
docker compose down
docker ps -a

# Se precisar, parar manualmente
Stop-Process -Id <PID> -Force

# Tentar novamente
docker compose up -d
```

### ❌ "failed to calculate checksum"

```powershell
# Limpar cache Docker
docker builder prune -a

# Reconstruir sem cache
docker compose build --no-cache coffee-app
```

### ❌ App não conecta no MySQL

```powershell
# Verificar se MySQL está pronto
docker compose ps
# Status deve ser "healthy" para mysql

# Testar conexão manualmente
docker compose exec mysql mysqladmin ping -h localhost -u root -pcoffeepwd123

# Ver logs do MySQL
docker compose logs mysql

# Esperar mais: editar docker-compose.yml
# Aumentar retries e interval no healthcheck
```

### ❌ Aplicação não inicia

```powershell
# Ver logs detalhados
docker compose logs coffee-app --tail=100

# Verificar profile está correto (deve ser "docker")
echo "SPRING_PROFILES_ACTIVE=docker"

# Verificar arquivo application-docker.yml existe
docker compose exec coffee-app ls -la /app/

# Recriar imagem
docker compose build --no-cache coffee-app
docker compose down
docker compose up -d
```

### ❌ RabbitMQ não inicia

```powershell
# Verificar logs
docker compose logs rabbitmq

# Limpar volume
docker compose down -v
docker volume rm coffee_rabbitmq_data

# Reconstruir
docker compose up -d
```

### 📊 Performance lenta

```powershell
# Aumentar recursos no docker-compose.yml
# Aumentar memory limit em:
# - docker-compose.yml: limits.memory
# - .env.docker: DB_POOL_SIZE, JAVA_OPTS

# Verificar uso de recursos
docker stats

# Limpar imagens não usadas
docker image prune -a
```

---

## 🌐 Produção

### Railway.app (Recomendado)

**Mais fácil, sem Dockerfile:** Railway detecta automaticamente Spring Boot

```bash
# 1. Criar conta em https://railway.app
# 2. Conectar repositório Git
# 3. Railway auto-detecta Maven + Java
# 4. Adicionar MySQL plugin (add-ons)
# 5. Adicionar RabbitMQ plugin (add-ons)
# 6. Configurar variáveis (Variables tab)
# 7. Deploy automático a cada push!

# CLI alternativo:
railway login
railway link
railway up
```

**Variáveis obrigatórias:**
```
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=<seu-secret-forte>
SPRING_DATASOURCE_URL=<auto-injetado pelo plugin MySQL>
```

### Fly.io

```bash
# 1. Instalar CLI
curl https://fly.io/install.sh | sh

# 2. Login
flyctl auth login

# 3. Criar app
flyctl app create coffee-app
cd coffee

# 4. Atualizar fly.toml com Dockerfile
# (copiar do projeto para raiz)

# 5. Configurar secrets
flyctl secrets set JWT_SECRET="seu-secret-forte"
flyctl secrets set SPRING_DATASOURCE_URL="mysql://..."

# 6. Deploy
flyctl deploy
```

**fly.toml exemplo:**
```toml
app = 'coffee-app'
primary_region = 'gru'  # São Paulo (mais rápido do Brasil)

[build]
  dockerfile = './Dockerfile'

[[services]]
  internal_port = 8080
  protocol = "tcp"
  [[services.ports]]
    port = 80
    handlers = ["http"]
  [[services.ports]]
    port = 443
    handlers = ["tls", "http"]

[[vm]]
  memory = '1gb'
  cpus = 1
```

### AWS ECS/Fargate

```bash
# 1. Push imagem para ECR
aws ecr create-repository --repository-name coffee
aws ecr get-login-password | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com
docker tag coffee:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/coffee:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/coffee:latest

# 2. Criar RDS MySQL (managed)
# 3. Criar ElastiCache RabbitMQ (ou CloudAMQP)
# 4. Criar task definition no ECS apontando para ECR
# 5. Criar service + load balancer
```

### Google Cloud Run

```bash
# 1. Autenticar
gcloud auth login
gcloud config set project PROJECT_ID

# 2. Push imagem
gcloud builds submit --tag gcr.io/PROJECT_ID/coffee:latest

# 3. Deploy
gcloud run deploy coffee \
  --image gcr.io/PROJECT_ID/coffee:latest \
  --region us-central1 \
  --memory 1Gi \
  --cpu 1 \
  --allow-unauthenticated \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod"

# 4. Configurar banco (Cloud SQL MySQL)
# 5. Configurar RabbitMQ (Pub/Sub or external)
```

---

## 📊 Monitoramento

### Health Checks

```bash
# Saúde geral
curl http://localhost:8080/actuator/health

# Saúde do banco
curl http://localhost:8080/actuator/health/db

# Saúde do disco
curl http://localhost:8080/actuator/health/diskSpace

# Métricas
curl http://localhost:8080/actuator/metrics
```

### Logs

#### Local (Docker)
```bash
docker compose logs -f coffee-app | grep ERROR
docker compose logs -f coffee-app | grep WARN
```

#### Produção
- **Railway:** Dashboard integrado
- **Fly.io:** `flyctl logs`
- **AWS CloudWatch:** Logs integrados (sem config)
- **Google Cloud Logging:** Integrado com Cloud Run

### Observabilidade

**Ferramentas recomendadas:**
- Datadog (APM completo)
- New Relic (APM + monitoring)
- Elastic Stack (ELK - self-hosted)
- Splunk (enterprise logging)

---

## 📝 Checklist de Deploy

### Antes de ir para Produção

- [ ] JWT_SECRET gerado com `openssl rand -base64 32`
- [ ] SPRING_DATASOURCE_PASSWORD diferente de produção
- [ ] SPRING_PROFILES_ACTIVE=prod
- [ ] Banco de dados em managed service (não local!)
- [ ] RabbitMQ em managed service
- [ ] HTTPS/SSL configurado (via reverse proxy ou platform)
- [ ] Backups automáticos do banco configurados
- [ ] Monitoring/alerting configurado
- [ ] Rate limiting configurado
- [ ] CORS restringido ao domínio correto
- [ ] Testes passando
- [ ] Imagem Docker buildada e testada

---

## 🔗 Links Úteis

- **Docker Docs:** https://docs.docker.com
- **Spring Boot Docker:** https://spring.io/guides/gs/spring-boot-docker/
- **Railway:** https://railway.app
- **Fly.io:** https://fly.io
- **AWS ECS:** https://aws.amazon.com/ecs/
- **Google Cloud Run:** https://cloud.google.com/run

---

**Dúvidas?** Consulte o README.md principal ou verifique troubleshooting acima.

