# 🔒 Guia de Segurança - Coffee PDV

Este documento descreve as melhores práticas de segurança implementadas no projeto Coffee e como configurar de forma segura em diferentes ambientes.

---

## 📋 Sumário

1. [Gerenciamento de Secrets](#gerenciamento-de-secrets)
2. [Variáveis de Ambiente](#variáveis-de-ambiente)
3. [Autenticação e JWT](#autenticação-e-jwt)
4. [Banco de Dados](#banco-de-dados)
5. [Docker Security](#docker-security)
6. [Network Security](#network-security)
7. [Logging e Monitoring](#logging-e-monitoring)
8. [Checklist de Deploy](#checklist-de-deploy)

---

## 🔐 Gerenciamento de Secrets

### ❌ ERRADO - Commitar secrets no Git

```bash
# NÃO faça isso!
git add .env
git commit -m "add env"  # ❌ NUNCA!
```

### ✅ CORRETO - Usar Secrets Manager

#### **Option 1: Fly.io Secrets (Recomendado)**

```bash
# Gerar JWT secret seguro
JWT_SECRET=$(openssl rand -base64 32)

# Definir cada secret
flyctl secrets set JWT_SECRET=$JWT_SECRET
flyctl secrets set SPRING_DATASOURCE_PASSWORD=sua-senha-db
flyctl secrets set RABBIT_PASSWORD=sua-senha-rabbit

# Verificar (valores mascarados)
flyctl secrets list

# Remover secret
flyctl secrets unset JWT_SECRET
```

#### **Option 2: AWS Secrets Manager**

```bash
# Armazenar secret
aws secretsmanager create-secret \
  --name coffee-jwt-secret \
  --secret-string $(openssl rand -base64 32)

# Retriever em tempo de execução via Spring Cloud AWS
```

#### **Option 3: HashiCorp Vault**

```bash
# Armazenar no Vault
vault kv put secret/coffee \
  jwt_secret="$(openssl rand -base64 32)" \
  db_password="sua-senha"

# App retrieves via Spring Cloud Vault
```

#### **Option 4: Azure Key Vault**

```bash
# Criar secret
az keyvault secret set \
  --vault-name coffee-vault \
  --name jwt-secret \
  --value $(openssl rand -base64 32)
```

### 🔄 Rotação de Secrets

```bash
# Gerar novo JWT_SECRET
NEW_SECRET=$(openssl rand -base64 32)

# Atualizar em tempo real (sem downtime)
flyctl secrets set JWT_SECRET=$NEW_SECRET

# A app recarrega automaticamente
flyctl logs | grep "secret updated"
```

---

## 🌍 Variáveis de Ambiente

### Variáveis Sensíveis (NUNCA em valores padrão)

```yaml
# ❌ ERRADO - application-prod.yml
spring:
  datasource:
    password: ${SPRING_DATASOURCE_PASSWORD:root}  # Default inseguro!

# ✅ CORRETO - Sem default
spring:
  datasource:
    password: ${SPRING_DATASOURCE_PASSWORD}  # Obrigatório!
```

### Estrutura de Variáveis por Ambiente

#### **Desenvolvimento (.env)**

```env
SPRING_PROFILES_ACTIVE=dev-mysql
LOG_LEVEL=DEBUG

SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/coffee
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root

JWT_SECRET=dev-only-secret-key-12345678901234567890
JWT_EXPIRATION_MS=86400000

RABBIT_HOST=localhost
RABBIT_USER=guest
RABBIT_PASSWORD=guest

CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
```

#### **Produção (.env.prod / Secrets)**

```bash
# VIA FLYCTL SECRETS
flyctl secrets set SPRING_PROFILES_ACTIVE=prod
flyctl secrets set LOG_LEVEL=WARN
flyctl secrets set SPRING_DATASOURCE_URL=jdbc:mysql://prod-db-host:3306/coffee
flyctl secrets set SPRING_DATASOURCE_USERNAME=db_admin
flyctl secrets set SPRING_DATASOURCE_PASSWORD=$(openssl rand -base64 32)
flyctl secrets set JWT_SECRET=$(openssl rand -base64 32)
flyctl secrets set JWT_EXPIRATION_MS=3600000
flyctl secrets set RABBIT_HOST=prod-rabbitmq-host
flyctl secrets set RABBIT_USER=$(openssl rand -base64 16 | tr -d '\n')
flyctl secrets set RABBIT_PASSWORD=$(openssl rand -base64 32)
flyctl secrets set CORS_ALLOWED_ORIGINS=https://cafe.com,https://www.cafe.com
flyctl secrets set SSL_ENABLED=true
flyctl secrets set SSL_KEYSTORE_PASSWORD=$(openssl rand -base64 32)
```

### Nomeação de Variáveis

Seguir convenção Spring Boot:

```
SPRING_<PROPERTY_PATH_WITH_UNDERSCORES>
```

Exemplos:

```bash
# spring.datasource.url
SPRING_DATASOURCE_URL=...

# spring.jpa.hibernate.ddl-auto
SPRING_JPA_HIBERNATE_DDL_AUTO=validate

# spring.rabbitmq.host
SPRING_RABBITMQ_HOST=...

# custom properties
RABBITMQ_EXCHANGE=...
JWT_SECRET=...
```

---

## 🔑 Autenticação e JWT

### Gerando JWT Secret

```bash
# Opção 1: OpenSSL (Recomendado - 256 bits = 32 bytes base64)
openssl rand -base64 32

# Opção 2: Java
java -cp . -r 32 | base64

# Opção 3: Python
python3 -c "import secrets; print(secrets.token_urlsafe(32))"

# Resultado exemplo:
# aB1cD2eF3gH4iJ5kL6mN7oP8qR9sT0uVwXyZ+/=
```

### Configuração de JWT

```yaml
# application-prod.yml
security:
  jwt:
    secret: ${JWT_SECRET}  # Obrigatório!
    expiration-ms: ${JWT_EXPIRATION_MS:3600000}  # 1 hora
    issuer: ${JWT_ISSUER:https://cafe.com}
    audience: ${JWT_AUDIENCE:https://cafe.com}
```

### Implementação de Refresh Token

```kotlin
// TODO: Implementar refresh token com TTL menor
@PostMapping("/refresh")
fun refreshToken(@RequestBody request: RefreshTokenRequest): TokenResponse {
    val newToken = tokenService.refresh(request.refreshToken)
    return TokenResponse(token = newToken)
}
```

### Token Claims Seguros

```kotlin
// ✅ CORRETO: Incluir apenas dados não-sensíveis
val claims = mapOf(
    "sub" to user.id,           // User ID
    "username" to user.username, // Username
    "roles" to user.roles        // Apenas role names, não dados sensíveis
)

// ❌ ERRADO: Nunca incluir dados sensíveis
// val claims = mapOf(
//     "password" to user.password,  // ❌ NUNCA!
//     "email" to user.email,        // ❌ NUNCA!
//     "creditCard" to "..."         // ❌ NUNCA!
// )
```

---

## 💾 Banco de Dados

### Credenciais Seguras

#### **MySQL em Desenvolvimento**

```bash
# Não usar 'root'
docker run -e MYSQL_ROOT_PASSWORD=root \
           -e MYSQL_USER=coffee \
           -e MYSQL_PASSWORD=coffee123 \
           -e MYSQL_DATABASE=coffee \
           mysql:8.0
```

#### **MySQL em Produção**

```bash
# 1. Usar managed service (AWS RDS, Google Cloud SQL, etc)
# 2. Criar usuário com permissões mínimas

-- Como root
CREATE USER 'coffee_app'@'%' IDENTIFIED BY 'super-senha-aleatoria';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP ON coffee.* TO 'coffee_app'@'%';
GRANT EXECUTE ON coffee.* TO 'coffee_app'@'%';
REVOKE CREATE, ALTER, DROP ON coffee.* FROM 'coffee_app'@'%';  -- Restringir migrations
FLUSH PRIVILEGES;

-- Usuario para Flyway (migrations)
CREATE USER 'coffee_migrations'@'%' IDENTIFIED BY 'outra-senha-aleatoria';
GRANT ALL PRIVILEGES ON coffee.* TO 'coffee_migrations'@'%';
FLUSH PRIVILEGES;
```

### Connection String Segura

```yaml
# ✅ CORRETO
spring:
  datasource:
    url: jdbc:mysql://db.example.com:3306/coffee?useSSL=true&requireSSL=true&serverTimezone=UTC

# ❌ ERRADO
spring:
  datasource:
    url: jdbc:mysql://db.example.com:3306/coffee?useSSL=false&allowPublicKeyRetrieval=true
```

### Connection Pooling

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20      # Produção
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      auto-commit: true
      test-on-borrow: true
```

### Backup Strategy

```bash
# Backup diário
0 2 * * * mysqldump -u coffee_app -p$MYSQL_PASSWORD coffee | gzip > /backups/coffee-$(date +\%Y\%m\%d).sql.gz

# Armazenar em local seguro (AWS S3, Azure Blob, etc)
aws s3 sync /backups s3://coffee-backups --sse AES256
```

---

## 🐳 Docker Security

### Base Image Segura

```dockerfile
# ✅ CORRETO: Alpine Linux (pequeno, seguro)
FROM eclipse-temurin:21-jre-alpine

# ✅ CORRETO: Especificar versão exata
FROM eclipse-temurin:21.0.1-jre-alpine

# ❌ ERRADO: Latest (imprevisível)
FROM eclipse-temurin:latest

# ❌ ERRADO: Full JDK (grande, desnecessário)
FROM eclipse-temurin:21-jdk-alpine
```

### Non-Root User

```dockerfile
# Criar usuário não-root
RUN addgroup -g 1000 appuser && \
    adduser -u 1000 -G appuser -s /bin/sh -D appuser

USER appuser

# ✅ Benefícios:
# - Se container for comprometido, acesso é limitado
# - Não consegue instalar pacotes
# - Não consegue acessar arquivos sensíveis do host
```

### Read-Only Filesystem

```yaml
# docker-compose.yml
services:
  app:
    read_only: true
    tmpfs:
      - /tmp
      - /var/cache
```

### Resource Limits

```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

### Scanning de Vulnerabilidades

```bash
# Com Trivy
trivy image coffee:latest

# Com Docker Scout
docker scout cves coffee:latest

# Com Grype
grype coffee:latest
```

---

## 🌐 Network Security

### CORS Configuration

```yaml
# application-security.yml
spring:
  web:
    cors:
      # ✅ CORRETO: Domínios específicos
      allowed-origins: https://app.cafe.com,https://admin.cafe.com
      
      # ❌ ERRADO: Permitir tudo
      # allowed-origins: "*"
      
      allowed-methods: GET,POST,PUT,DELETE,OPTIONS
      allowed-headers: Content-Type,Authorization
      allow-credentials: true
      max-age: 3600
```

### HTTPS Obrigatório

```yaml
server:
  ssl:
    enabled: true
    key-store: ${SSL_KEYSTORE_PATH}
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    protocol: TLSv1.2
    enabled-protocols: TLSv1.2,TLSv1.3
    
# Redirecionar HTTP para HTTPS
spring:
  security:
    require-https: true
```

### Rate Limiting

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.github.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>7.6.0</version>
</dependency>
```

```kotlin
// RateLimiterInterceptor.kt
@Component
class RateLimiterInterceptor : HandlerInterceptor {
    private val bucket = Bucket4j.builder()
        .addLimit(Bandwidth.simple(100, Refill.intervally(100, Duration.ofMinutes(1))))
        .build()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (!bucket.tryConsume(1)) {
            response.status = 429  // Too Many Requests
            return false
        }
        return true
    }
}
```

### API Gateway / WAF

Para produção, usar:
- **Cloudflare** (WAF, DDoS protection)
- **AWS WAF** + CloudFront
- **Azure Application Gateway**
- **Kong** (auto-hospedado)

---

## 📊 Logging e Monitoring

### Logs Seguros (Sem Dados Sensíveis)

```kotlin
// ✅ CORRETO
logger.info("User login attempt: username=$username")
logger.warn("Failed authentication for user: $username")

// ❌ ERRADO
logger.info("User login: username=$username, password=$password")
logger.error("Database error: $exception")  // Stack trace expõe paths
```

### Structured Logging

```kotlin
logger.info(mapOf(
    "event" to "user.login",
    "username" to username,
    "ip_address" to request.remoteAddr,
    "timestamp" to Instant.now()
))
```

### Monitoramento de Segurança

```bash
# Alertas para:
- Múltiplas tentativas de login falhadas
- Acesso a endpoints não-autorizados (403)
- Exceções de banco de dados
- Consumo anormal de recursos
- Mudanças em dados críticos (usuários, configurações)
```

---

## ✅ Checklist de Deploy em Produção

### Pré-Deploy

- [ ] JWT_SECRET foi gerado com `openssl rand -base64 32`
- [ ] Todas as variáveis sensíveis estão em Secrets Manager (não em fly.toml)
- [ ] Database user é diferente de root
- [ ] Database password tem 12+ caracteres aleatórios
- [ ] RabbitMQ tem credenciais customizadas
- [ ] SSL/HTTPS está habilitado
- [ ] CORS está configurado para domínio específico apenas
- [ ] Rate limiting está implementado
- [ ] Logs não expõem dados sensíveis
- [ ] Health endpoint está protegido ou desabilitado em produção

### Deploy

```bash
# 1. Verificar fly.toml não contém secrets
grep -i "password\|secret" fly.toml  # Não deve retornar nada

# 2. Definir secrets
flyctl secrets set $(cat .env.prod | grep -v '^#' | xargs)

# 3. Deploy
flyctl deploy

# 4. Verificar logs
flyctl logs | head -20

# 5. Testar health
curl https://coffee-pdv.fly.dev/api/actuator/health
```

### Pós-Deploy

- [ ] App iniciou sem erros
- [ ] Health check retorna status UP
- [ ] Database migrations executaram com sucesso
- [ ] RabbitMQ está conectado
- [ ] SSL certificate é válido
- [ ] Logs não contêm warnings não-esperados
- [ ] Endpoints de login funcionam
- [ ] Autenticação JWT funciona
- [ ] Backups estão configurados
- [ ] Monitoramento está ativo

### Manutenção Contínua

- [ ] Rotacionar secrets a cada 90 dias
- [ ] Atualizar dependências (vulnerabilidades)
- [ ] Revisar logs regularmente
- [ ] Testar disaster recovery
- [ ] Auditar acesso a dados sensíveis
- [ ] Atualizar certificados SSL antes da expiração

---

## 🚨 Resposta a Incidentes

### Vazamento de Secret Detectado?

```bash
# 1. IMEDIATAMENTE: Revogar secret antigo
flyctl secrets unset JWT_SECRET

# 2. Gerar novo secret
NEW_SECRET=$(openssl rand -base64 32)

# 3. Atualizar
flyctl secrets set JWT_SECRET=$NEW_SECRET

# 4. App recarrega automaticamente
flyctl logs | grep "secret updated"

# 5. Verificar logs para uso não-autorizado
flyctl logs --tail 1000 | grep "invalid token"

# 6. Revogar tokens antigos (se possível)
# TODO: Implementar token revocation list
```

### Comprometimento de Database

```bash
# 1. Criar snapshot do banco
# (AWS RDS, Google Cloud SQL, etc)

# 2. Reverter para snapshot anterior
# (Antes do ataque)

# 3. Resetar password do database user
ALTER USER 'coffee_app'@'%' IDENTIFIED BY 'nova-senha-aleatoria';

# 4. Atualizar em Fly.io Secrets
flyctl secrets set SPRING_DATASOURCE_PASSWORD='nova-senha-aleatoria'

# 5. Auditar dados comprometidos
SELECT * FROM usuarios WHERE updated_at > '2024-01-10 12:00:00';
```

---

## 📖 Referências de Segurança

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [CWE-522: Insufficiently Protected Credentials](https://cwe.mitre.org/data/definitions/522.html)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8949)
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework/)

---

**Última atualização**: Janeiro 2026  
**Versão**: 1.0  
**Manutenido por**: DevOps Team

