# 🚀 Guia Rápido - Coffee PDV

Bem-vindo ao projeto Coffee PDV! Este guia rápido ajudará você a começar em poucos minutos.

---

## ⚡ Quick Start (5 minutos)

### 1️⃣ Clone e Configure

```bash
git clone https://github.com/seu-usuario/coffee.git
cd coffee

# Copie o template de variáveis de ambiente
cp .env.example .env

# Edite com suas credenciais locais
nano .env  # ou use seu editor favorito
```

### 2️⃣ Start com Docker Compose

```bash
# Inicia MySQL, RabbitMQ e a aplicação
docker-compose up -d

# Verifique os logs
docker-compose logs -f coffee-app
```

### 3️⃣ Acesse a Aplicação

```
API:     http://localhost:8080/api
Swagger: http://localhost:8080/api/swagger-ui.html
Health:  http://localhost:8080/api/actuator/health
RabbitMQ UI: http://localhost:15672 (guest/guest)
```

---

## 📁 Estrutura de Arquivos Criados

```
coffee/
├── 📄 README.md                         ← Documentação completa
├── 🔒 SECURITY.md                       ← Guia de segurança
├── 📋 QUICKSTART.md                     ← Este arquivo
├── 📄 .env.example                      ← Template de variáveis
├── 🔐 .env.prod                         ← Template para produção
├── 🐳 Dockerfile                        ← Imagem Docker (atualizado)
├── 🐳 docker-compose.yml                ← Stack local (NOVO)
├── 📝 .gitignore                        ← Atualizado com regras de segurança
│
├── src/main/resources/
│   ├── application.yml                  ← Config principal (atualizado)
│   ├── application-dev-mySql.yml        ← Profile dev (inalterado)
│   ├── application-prod.yml             ← Profile prod (ATUALIZADO)
│   └── application-security.yml         ← Config de segurança (NOVO)
│
└── scripts/
    ├── generate-secrets.sh              ← Gerar secrets (Linux/Mac)
    ├── generate-secrets.ps1             ← Gerar secrets (Windows)
    ├── deploy-to-flyio.sh               ← Deploy automatizado (Linux/Mac)
    └── deploy-to-flyio.ps1              ← Deploy automatizado (Windows)
```

---

## 🔐 Gerenciamento de Secrets (MUITO IMPORTANTE!)

### ⚠️ Nunca Faça Isso:

```bash
# ❌ Commitar .env com credenciais reais
git add .env
git commit -m "add env"

# ❌ Usar passwords padrão em produção
SPRING_DATASOURCE_PASSWORD=root
JWT_SECRET=my-secret-key-123
```

### ✅ Faça Isso:

#### **Para Desenvolvimento**

```bash
# 1. Gerar secrets aleatórios
./scripts/generate-secrets.ps1  # Windows
./scripts/generate-secrets.sh   # Linux/Mac

# 2. Copiar para .env
cp .env.example .env
# Edite e adicione os secrets gerados

# 3. NUNCA commitar .env
# Já está em .gitignore ✅
```

#### **Para Produção (Fly.io)**

```bash
# 1. Gerar secrets
JWT_SECRET=$(openssl rand -base64 32)
DB_PASSWORD=$(openssl rand -base64 32)

# 2. Configurar no Fly.io (via web UI ou CLI)
flyctl secrets set JWT_SECRET="$JWT_SECRET"
flyctl secrets set SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD"
# ... outros secrets

# 3. Verificar
flyctl secrets list  # Mostra valores mascarados ✅

# 4. Deploy
flyctl deploy
```

---

## 🐳 Docker Compose - Guia de Uso

### Iniciar Todos os Serviços

```bash
docker-compose up -d

# Verificar status
docker-compose ps

# Ver logs em tempo real
docker-compose logs -f coffee-app
```

### Parar Serviços

```bash
# Parar mas manter dados
docker-compose stop

# Parar e remover containers (mas mantém volumes)
docker-compose down

# CUIDADO: Remover tudo incluindo banco de dados
docker-compose down -v
```

### Conectar ao MySQL

```bash
docker-compose exec mysql mysql -u coffee -p
# Password: coffee
```

### Acessar RabbitMQ Management

```
http://localhost:15672
Usuário: guest
Senha: guest
```

---

## 🏗️ Variáveis de Ambiente Principais

| Variável | Descrição | Obrigatória? |
|----------|-----------|------------|
| `SPRING_PROFILES_ACTIVE` | dev-mysql, test, prod | ✅ |
| `SPRING_DATASOURCE_URL` | URL do banco | ✅ |
| `SPRING_DATASOURCE_USERNAME` | Usuário DB | ✅ |
| `SPRING_DATASOURCE_PASSWORD` | Senha DB | ✅ |
| `JWT_SECRET` | Secret do JWT (32+ chars) | ✅ |
| `RABBIT_HOST` | Host RabbitMQ | ✅ |
| `RABBIT_USER` | Usuário RabbitMQ | ✅ |
| `RABBIT_PASSWORD` | Senha RabbitMQ | ✅ |
| `LOG_LEVEL` | DEBUG, INFO, WARN | ❌ (default: INFO) |
| `CORS_ALLOWED_ORIGINS` | Domínios permitidos | ❌ |

---

## 🚀 Deploy em Produção (Fly.io)

### Setup Inicial

```bash
# 1. Instalar Fly.io CLI
# https://fly.io/docs/hands-on/install-flyctl/

# 2. Login
flyctl auth login

# 3. Criar app
flyctl launch --app coffee-pdv

# 4. Configurar arquivo de env (ver SECURITY.md)
```

### Deploy Automático

```powershell
# Windows
.\scripts\deploy-to-flyio.ps1 -Ambiente "prod"

# Linux/Mac
./scripts/deploy-to-flyio.sh prod
```

### Monitoramento Após Deploy

```bash
# Ver logs
flyctl logs

# Status da app
flyctl status

# Métricas
flyctl metrics
```

---

## 🧪 Testando Localmente

### Via Docker Compose (Recomendado)

```bash
# Tudo pronto em um comando
docker-compose up -d

# Testar health
curl http://localhost:8080/api/actuator/health
```

### Via Maven (Sem Docker)

```bash
# Terminal 1: MySQL
docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=coffee -p 3306:3306 mysql:8.0

# Terminal 2: RabbitMQ
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management-alpine

# Terminal 3: App
mvn spring-boot:run

# Testar
curl http://localhost:8080/api/swagger-ui.html
```

---

## 📊 Endpoints Essenciais

### Health Check

```bash
curl http://localhost:8080/api/actuator/health
```

Resposta esperada:
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "rabbitmq": { "status": "UP" }
  }
}
```

### Swagger/OpenAPI

```
http://localhost:8080/api/swagger-ui.html
```

Documentação interativa de todos os endpoints.

---

## 🐛 Troubleshooting Rápido

### Problema: "Connection refused" no MySQL

```bash
# Verificar se MySQL está rodando
docker-compose ps

# Restart
docker-compose restart mysql

# Ver logs
docker-compose logs mysql
```

### Problema: "Cannot connect to RabbitMQ"

```bash
# Verificar porta
netstat -an | grep 5672

# Restart
docker-compose restart rabbitmq
```

### Problema: "JWT token expired"

Aumentar tempo de expiração em `.env`:
```env
JWT_EXPIRATION_MS=7200000  # 2 horas em ms
```

### Problema: Docker/Compose não encontrado

```bash
# Instalar Docker Desktop
# https://www.docker.com/products/docker-desktop

# Ou Docker Engine + Compose
# https://docs.docker.com/engine/install/
# https://docs.docker.com/compose/install/
```

---

## 📚 Próximos Passos

1. **Ler README.md completo** - Documentação detalhada
2. **Ler SECURITY.md** - Boas práticas de segurança
3. **Explorar Swagger** - http://localhost:8080/api/swagger-ui.html
4. **Implementar features** - Consulte a documentação
5. **Deploy em produção** - Seguir guia de Fly.io

---

## 🎯 Checklist para Deploy

- [ ] `SPRING_PROFILES_ACTIVE=prod`
- [ ] `JWT_SECRET` gerado com `openssl rand -base64 32`
- [ ] Database user diferente de root
- [ ] Database password é aleatória e segura
- [ ] RabbitMQ tem credenciais customizadas
- [ ] Nenhum `.env` ou `.env.prod` commitado no Git
- [ ] Secrets estão em Fly.io Secrets
- [ ] `fly.toml` não contém passwords
- [ ] HTTPS está habilitado
- [ ] CORS está restrictivo ao seu domínio

---

## 📞 Suporte

- 📖 **README.md** - Documentação completa
- 🔒 **SECURITY.md** - Questões de segurança
- 🐛 **Issues** - Reporte problemas no GitHub
- 💬 **Discussions** - Faça perguntas

---

## 🎓 Recursos de Aprendizado

- [Spring Boot Guide](https://spring.io/guides)
- [Kotlin for Spring](https://spring.io/guides/tutorials/spring-boot-kotlin/)
- [Fly.io Documentation](https://fly.io/docs/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

---

**Boa sorte! 🚀**

Última atualização: Janeiro 2026

