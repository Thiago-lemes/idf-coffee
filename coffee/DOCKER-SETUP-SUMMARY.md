# RESUMO: Infraestrutura Docker Produção — Opção A (MySQL em Container)

## Arquivos Criados (6 arquivos)

```
D:\workSpace\coffee\coffee\
├── Dockerfile                    # Multi-stage build (produção)
├── .dockerignore                 # Otimizar contexto Docker
├── docker-compose.prod.yml       # Stack: MySQL 8 + App
├── .env.prod                     # Variáveis locais (gerado, NÃO COMMITAR)
├── .env.prod.example             # Template seguro (COMMITAR)
├── README-DOCKER-PROD.md         # Documentação completa
├── DOCKER-TEST-LOCAL.md          # Guia passo-a-passo (este doc)
├── deploy-local-prod.ps1         # Script automatizado (Windows)
└── .gitignore (atualizado)       # Protege .env.prod
```

## Arquitetura Resultante

```
┌─────────────────────────────────────────────┐
│         Docker Compose Stack                │
├─────────────────────────────────────────────┤
│                                             │
│  ┌──────────────────┐  ┌──────────────────┐ │
│  │   coffee-app     │  │   coffee-db      │ │
│  │   (Spring Boot)  │  │   (MySQL 8)      │ │
│  │   Port 8080      │  │   Port 3306      │ │
│  │   Health: /h     │  │   Healthcheck    │ │
│  └──────────────────┘  └──────────────────┘ │
│         ↓                       ↑            │
│    Conexão JDBC                 │            │
│    (via docker network)          │            │
│                         Volume db_data       │
│                         (persistente)        │
│                                             │
└─────────────────────────────────────────────┘
         ↓
   Localhost:8080
   Localhost:3306
```

## Variáveis de Ambiente (Mapeadas)

Arquivo: `.env.prod`

```bash
# MySQL (container db)
MYSQL_ROOT_PASSWORD=root123456
MYSQL_DATABASE=coffee
MYSQL_USER=coffee_user
MYSQL_PASSWORD=coffee123456

# Spring (container app)
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/coffee?...
SPRING_DATASOURCE_USERNAME=coffee_user
SPRING_DATASOURCE_PASSWORD=coffee123456
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=your_super_secret_...
JWT_EXPIRATION_MS=86400000
SERVER_PORT=8080
```

## Fluxo de Execução

```
1. Você roda: .\mvnw.cmd -DskipTests package
   ↓
   Maven compila Kotlin → gera JAR em target/

2. Você roda: docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d --build
   ↓
   a) Docker lê Dockerfile
   b) Copia JAR do Maven build
   c) Constrói imagem coffee:latest
   d) Inicia container MySQL (porta 3306)
   e) Inicia container App (porta 8080)
   f) App aguarda MySQL healthcheck (~20s)
   g) App conecta, roda Flyway migrations, inicia

3. Você testa:
   - curl http://localhost:8080/actuator/health
   - Logs: docker-compose logs -f app
```

## Checklist: O Que Fazer Agora

- [ ] Ler DOCKER-TEST-LOCAL.md (guia passo-a-passo)
- [ ] Rodar `.\mvnw.cmd -DskipTests package` (build Maven)
- [ ] Se sucesso, rodar `docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d --build`
- [ ] Verificar `docker-compose ps` (ambos containers UP)
- [ ] Testar `curl http://localhost:8080/actuator/health` (HTTP 200)
- [ ] Ver logs `docker-compose -f docker-compose.prod.yml logs app`
- [ ] Se tudo OK, parar com `docker-compose -f docker-compose.prod.yml down`
- [ ] Commitar arquivos (exceto .env.prod)
- [ ] Próximo passo: Deploy em VPS (ver README-DOCKER-PROD.md)

## Próximos Passos (Após Teste Local)

1. **VPS Setup** (quando tiver servidor)
   - Instalar Docker + Docker Compose
   - Clonar repositório
   - Criar `.env.prod` com valores reais
   - Rodar docker-compose

2. **Otimizações VPS**
   - Nginx reverse proxy (SSL/TLS)
   - Backup MySQL automático
   - Monitoramento (logs, métricas)
   - Escalabilidade (múltiplas instâncias)

3. **Segurança Futuro**
   - Migrar de `.env.prod` → Docker secrets ou Vault
   - Scans de imagem (Trivy, Grype)
   - Rate limiting, WAF

## Decisões Técnicas

| Decisão | Motivo |
|---------|--------|
| **MySQL em container** | Opção A (VPS sem orçamento) — simples, leve |
| **Multi-stage Dockerfile** | Reduz tamanho final da imagem (~300MB vs 500MB+) |
| **Variáveis env no `.env.prod`** | Fácil local; migrar para secrets após validação |
| **spring.profiles.active=prod** | Separa config prod (no `application-prod.yml`) |
| **Healthcheck MySQL** | Evita race condition (app conecta antes do DB ready) |
| **Volume db_data** | Persiste dados MySQL se containers reiniciam |

## Suporte / Debug

Se der erro durante teste, comente os logs e faço troubleshooting:

```powershell
# Logs completos
docker-compose -f docker-compose.prod.yml logs

# Apenas app
docker-compose -f docker-compose.prod.yml logs app

# Apenas MySQL
docker-compose -f docker-compose.prod.yml logs db

# Status containers
docker-compose -f docker-compose.prod.yml ps

# Inspecionar network
docker network ls
docker network inspect coffee_default

# Executar comando no app
docker exec coffee-app ls -la /app
```

---

**Tudo pronto! Siga DOCKER-TEST-LOCAL.md e avise se der algum problema.**

