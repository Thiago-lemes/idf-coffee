# Docker Production Deployment Guide

## Visão Geral

Este guia descreve como fazer deploy da aplicação Coffee em produção usando Docker e Docker Compose com MySQL.

## Arquivos Criados

- `Dockerfile` — Multi-stage build (Maven + JRE, production-ready)
- `docker-compose.prod.yml` — Stack com MySQL 8 e aplicação
- `.env.prod.example` — Template de variáveis de ambiente
- `.env.prod` — Arquivo local (não commitar!)
- `deploy-local-prod.ps1` — Script automatizado para teste local (Windows)

## Pré-requisitos

- Docker e Docker Compose instalados
- Maven (ou use `mvnw.cmd` local)
- Java 21 (para build local)

## Teste Local (Development/Staging)

### 1. Preparar variáveis de ambiente

```powershell
# Copiar template e ajustar valores
Copy-Item .env.prod.example .env.prod
# Editar .env.prod com credenciais apropriadas
```

### 2. Build e start stack

**Opção A: Script automatizado (recomendado)**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process
.\deploy-local-prod.ps1
```

**Opção B: Passos manuais**
```powershell
# Build Maven
.\mvnw.cmd -DskipTests package

# Start services
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d --build

# Ver logs
docker-compose -f docker-compose.prod.yml logs -f app

# Verificar health
curl http://localhost:8080/actuator/health
```

### 3. Acessar a aplicação

- **API**: http://localhost:8080
- **Health**: http://localhost:8080/actuator/health
- **MySQL**: localhost:3306 (use credenciais do .env.prod)

### 4. Parar stack

```powershell
docker-compose -f docker-compose.prod.yml down
```

## Deploy em Produção (VPS)

### 1. Preparar servidor

```bash
# Instalar Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Instalar Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Criar usuário Docker (opcional)
sudo usermod -aG docker $USER
newgrp docker
```

### 2. Clonar/copiar código

```bash
git clone <seu-repositorio> /opt/coffee
cd /opt/coffee/coffee
```

### 3. Configurar segredos

```bash
# NUNCA commitar .env.prod em VCS!
# Em produção, use uma das estratégias:

# Opção A: .env.prod local (simples, menos seguro)
cp .env.prod.example .env.prod
nano .env.prod  # Editar com valores reais, GERAR JWT_SECRET forte

# Opção B: Docker secrets (recomendado para Swarm)
# ou variáveis de ambiente do servidor

# Opção C: Secret manager (Vault, AWS Secrets Manager, etc.)
```

### 4. Start stack em produção

```bash
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d --build

# Ver logs
docker-compose -f docker-compose.prod.yml logs -f

# Status
docker-compose -f docker-compose.prod.yml ps
```

### 5. Backup & Persistência

O volume `db_data` persiste dados MySQL. Faça backup regularmente:

```bash
# Backup manual
docker exec coffee-db mysqldump -u coffee_user -p$MYSQL_PASSWORD coffee > backup_$(date +%Y%m%d_%H%M%S).sql

# Restaurar
docker exec -i coffee-db mysql -u coffee_user -p$MYSQL_PASSWORD coffee < backup.sql
```

### 6. Healthcheck e Monitoramento

A stack inclui:
- **MySQL healthcheck**: pinga o container a cada 10s
- **App healthcheck**: disponível em `/actuator/health`
- **restart policy**: containers reiniciam automaticamente se falhem

Monitorar:
```bash
docker-compose -f docker-compose.prod.yml logs --tail 100 -f
docker stats coffee-app coffee-db
```

## Variáveis de Ambiente (Referência)

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `MYSQL_ROOT_PASSWORD` | Root password MySQL | `prod_strong_pass` |
| `MYSQL_DATABASE` | Database name | `coffee` |
| `MYSQL_USER` | App user | `coffee_user` |
| `MYSQL_PASSWORD` | App password | `app_strong_pass` |
| `SPRING_DATASOURCE_URL` | JDBC URL | `jdbc:mysql://db:3306/coffee?useSSL=false&serverTimezone=UTC` |
| `SPRING_DATASOURCE_USERNAME` | DB user | `coffee_user` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `app_strong_pass` |
| `JWT_SECRET` | JWT signing key (GERAR FORTE!) | `base64_encoded_random_secret` |
| `JWT_EXPIRATION_MS` | Token lifetime (ms) | `86400000` (24h) |
| `SERVER_PORT` | App port | `8080` |

## Gerar JWT_SECRET Forte

```bash
# Linux/Mac
openssl rand -base64 32

# Windows PowerShell
[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

## Troubleshooting

### Erro: "Cannot connect to database"
- Verificar se MySQL está saudável: `docker-compose ps`
- Logs MySQL: `docker-compose logs db`
- Credenciais: conferir `.env.prod` vs `docker-compose.prod.yml`

### Erro: "Port 3306 already in use"
- Mudar em `docker-compose.prod.yml`: `3306:3306` → `3307:3306`
- Ou parar MySQL local: `sudo systemctl stop mysql`

### App não sobe
- Ver logs: `docker-compose logs app`
- Validar build: `.\mvnw.cmd -DskipTests package`
- Profile prod ativo? Verificar `SPRING_PROFILES_ACTIVE=prod` em `.env.prod`

## Próximos Passos

1. **Nginx Reverse Proxy** — adicionar para SSL/TLS e load balancing
2. **CI/CD** — GitHub Actions / GitLab CI para build automático
3. **Secrets Manager** — migrar de `.env.prod` para Vault ou Cloud Secrets
4. **Kubernetes** — escalar para múltiplas instâncias
5. **Database Gerenciado** — migrar MySQL para RDS/CloudSQL quando houver orçamento

## Quick Commands

```bash
# Start
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d --build

# Stop
docker-compose -f docker-compose.prod.yml down

# Restart
docker-compose -f docker-compose.prod.yml restart

# Logs
docker-compose -f docker-compose.prod.yml logs -f app

# Status
docker-compose -f docker-compose.prod.yml ps

# Shell no container
docker exec -it coffee-app /bin/sh
docker exec -it coffee-db mysql -u root -p
```

---

**Pronto para deploy? Rode `.\deploy-local-prod.ps1` ou siga os passos manuais acima!**

