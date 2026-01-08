# 📊 Arquitetura do Projeto Coffee PDV

## 🏗️ Arquitetura em Camadas

```
┌─────────────────────────────────────────────────────────────┐
│                     CLIENTE (Web/Mobile)                    │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │           API REST Gateway (Fly.io)                  │  │
│  │  - Load Balancing                                    │  │
│  │  - SSL/HTTPS                                         │  │
│  │  - Rate Limiting                                     │  │
│  └───────────────┬──────────────────────────────────────┘  │
│                  │                                           │
├──────────────────┼───────────────────────────────────────────┤
│                  │    Spring Security                        │
│    ┌─────────────▼─────────────┐                             │
│    │  JWT Token Validation     │                             │
│    │  CORS & HTTPS Check       │                             │
│    └─────────────┬─────────────┘                             │
│                  │                                           │
├──────────────────┼───────────────────────────────────────────┤
│                  │    Controller Layer                       │
│    ┌─────────────▼──────────────┐                            │
│    │  @RestController Classes   │                            │
│    │  - UsuarioController       │                            │
│    │  - ProdutoController       │                            │
│    │  - TransacaoController     │                            │
│    │  - AuthController          │                            │
│    └─────────────┬──────────────┘                            │
│                  │                                           │
├──────────────────┼───────────────────────────────────────────┤
│                  │    Service Layer                          │
│    ┌─────────────▼──────────────┐                            │
│    │  Business Logic Services   │                            │
│    │  - UsuarioService          │                            │
│    │  - ProdutoService          │                            │
│    │  - TransacaoService        │                            │
│    │  - AuthService             │                            │
│    └──┬──────────────┬──────────┘                            │
│       │              │                                       │
│  ┌────▼─────┐   ┌───▼──────────┐                            │
│  │ Database  │   │  RabbitMQ    │                            │
│  │ Queries   │   │  Publisher   │                            │
│  └────┬─────┘   └───┬──────────┘                            │
│       │              │                                       │
├───────┼──────────────┼───────────────────────────────────────┤
│       │              │    Repository Layer (Data Access)    │
│  ┌────▼──────────────▼─────┐                                │
│  │   Spring Data JPA       │                                │
│  │   - UsuarioRepository   │                                │
│  │   - ProdutoRepository   │                                │
│  │   - TransacaoRepository │                                │
│  └────┬────────────────────┘                                │
│       │                                                      │
├───────┼──────────────────────────────────────────────────────┤
│       │          Database Layer                             │
│  ┌────▼────────────────────┐                                │
│  │   MySQL 8.0 (RDS)       │                                │
│  │   - Usuarios             │                                │
│  │   - Produtos             │                                │
│  │   - Categorias           │                                │
│  │   - Insumos              │                                │
│  │   - Transacoes           │                                │
│  │   - Caixa Registradora   │                                │
│  └────────────────────────┘                                │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## 🔄 Fluxo de Dados - Exemplo: Criar Transação

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Cliente (Frontend)                                        │
│    POST /api/transacoes { items: [...] }                   │
└────────────┬────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. API Gateway (Fly.io)                                     │
│    - Validate HTTPS                                         │
│    - Forward request                                        │
└────────────┬────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. Spring Security Filter                                   │
│    - Extract JWT token from Authorization header            │
│    - Validate signature & expiration                        │
│    - Set authenticated user in SecurityContext              │
└────────────┬────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. Controller (TransacaoController)                         │
│    @PostMapping("/transacoes")                              │
│    @PreAuthorize("hasRole('USER')")                         │
│    fun criarTransacao(dto: CreateTransacaoDTO)              │
└────────────┬────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. Service (TransacaoService)                               │
│    - Validar items (preço, quantidade)                     │
│    - Calcular total                                         │
│    - Iniciar transação DB                                  │
└────────────┬────────────────────────────────────────────────┘
             │
      ┌──────┴──────┐
      │             │
      ▼             ▼
┌──────────┐  ┌──────────────────────────────────────────┐
│ Persistir│  │ 6. Publicar Evento (RabbitMQ)            │
│ no DB    │  │    - Queue: fechamento.caixa.queue       │
└──────────┘  │    - Event: TransacaoCriadaEvent         │
      │       │    - Subscribers: Processar assincronico │
      │       └──────────────────────────────────────────┘
      │             │
      ▼             ▼
┌─────────────────────────────────────────────────────────────┐
│ 7. Repository (TransacaoRepository)                         │
│    @Repository extends JpaRepository<Transacao, Long>       │
│    - Save to MySQL                                         │
└────────────┬────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│ 8. MySQL Database                                           │
│    INSERT INTO transacoes (data, total, usuario_id, ...)    │
└────────────┬────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│ 9. Response Back to Client                                  │
│    200 OK { id: 123, total: 45.99, status: "CONCLUIDA" }   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🗂️ Estrutura de Diretórios Detalhada

```
coffee/
│
├── 📄 Configurações & Documentação
│   ├── README.md                          # Documentação completa
│   ├── SECURITY.md                        # Guia de segurança
│   ├── QUICKSTART.md                      # Guia rápido
│   ├── IMPLEMENTATION_SUMMARY.md           # Este resumo
│   ├── pom.xml                            # Maven dependencies
│   ├── fly.toml                           # Fly.io config (antigo)
│   ├── .gitignore                         # Git ignore rules
│   ├── .dockerignore                      # Docker ignore rules
│   ├── Dockerfile                         # Container image
│   ├── docker-compose.yml                 # Local stack
│   ├── .env.example                       # Env template (dev)
│   └── .env.prod                          # Env template (prod)
│
├── 📂 src/main
│   ├── kotlin/org/br/idf/coffee/
│   │   ├── CoffeeApplication.kt           # Spring Boot entry point
│   │   │
│   │   ├── auth/                          # 🔐 Autenticação
│   │   │   ├── controller/
│   │   │   │   └── AuthController.kt
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.kt
│   │   │   │   └── TokenResponse.kt
│   │   │   └── service/
│   │   │       └── AuthService.kt
│   │   │
│   │   ├── usuario/                       # 👥 Usuários
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   │
│   │   ├── produto/                       # 📦 Produtos
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   │
│   │   ├── categoria/                     # 📁 Categorias
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   │
│   │   ├── insumo/                        # 🥛 Insumos/Ingredientes
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   │
│   │   ├── estoque/                       # 📊 Controle de Estoque
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   │
│   │   ├── transacoes/                    # 💰 Transações
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   │
│   │   ├── fluxo_caixa/                   # 💳 Gestão de Caixa
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   │
│   │   ├── rabbit/                        # 🐰 RabbitMQ Integration
│   │   │   ├── config/
│   │   │   │   └── RabbitMQConfig.kt
│   │   │   ├── event/
│   │   │   │   ├── TransacaoCriadaEvent.kt
│   │   │   │   └── CaixaFechadoEvent.kt
│   │   │   ├── listener/
│   │   │   │   └── TransacaoListener.kt
│   │   │   └── publisher/
│   │   │       └── EventPublisher.kt
│   │   │
│   │   ├── security/                      # 🔐 Spring Security Config
│   │   │   ├── JwtTokenProvider.kt
│   │   │   ├── JwtAuthenticationFilter.kt
│   │   │   └── SecurityConfig.kt
│   │   │
│   │   ├── config/                        # ⚙️ Application Config
│   │   │   ├── OpenApiConfig.kt           # Swagger/OpenAPI
│   │   │   ├── CorsConfig.kt
│   │   │   ├── JpaConfig.kt
│   │   │   └── WebConfig.kt
│   │   │
│   │   ├── ultils/                        # 🛠️ Utilitários
│   │   │   ├── Constants.kt
│   │   │   ├── Extensions.kt
│   │   │   └── Validators.kt
│   │   │
│   │   └── exception/                     # ⚠️ Exception Handling
│   │       ├── GlobalExceptionHandler.kt
│   │       ├── ApiException.kt
│   │       └── ErrorResponse.kt
│   │
│   └── resources/
│       ├── application.yml                # Main config
│       ├── application-dev-mySql.yml      # Dev profile
│       ├── application-prod.yml           # Prod profile
│       ├── application-security.yml       # Security config
│       ├── application-h2.test            # Test profile
│       │
│       ├── db/migration/                  # 🛢️ Flyway Migrations
│       │   ├── V1__CREATE_TABLE_USUARIO.SQL
│       │   ├── V2__CREATE_TABLE_CATEGORIA.SQL
│       │   ├── V3__CREATE_TABLE_CAIXA_REGISTRADORA.SQL
│       │   ├── V4__CREATE_TABLE_PRODUTO.SQL
│       │   ├── V5__CREATE_TABLE_TRASACAO.SQL
│       │   ├── V6__CREATE_TABLE_TRANSACAO_ITEM.SQL
│       │   ├── V7__CREATE_TABLE_INSUMO.SQL
│       │   ├── V8__CREATE_TABLE_ESTOQUE_INSUMO.SQL
│       │   └── V9__CREATE_TABLE_PRODUTO_INSUMO.SQL
│       │
│       ├── static/                        # 📁 Static files (CSS, JS)
│       └── templates/                     # 📁 Thymeleaf templates
│
├── 📂 src/test
│   └── kotlin/org/br/idf/coffee/
│       ├── CoffeeApplicationTests.kt
│       ├── auth/
│       ├── usuario/
│       ├── produto/
│       └── ...
│
├── 📂 scripts/                            # 🛠️ Automation Scripts
│   ├── generate-secrets.sh                # Linux/Mac
│   ├── generate-secrets.ps1               # Windows
│   ├── deploy-to-flyio.sh                 # Linux/Mac
│   └── deploy-to-flyio.ps1                # Windows
│
└── 📂 target/                             # 🏗️ Build output (ignore)
    ├── classes/
    ├── generated-sources/
    └── coffee-0.0.1-SNAPSHOT.jar
```

---

## 🔄 Ciclo de Vida da Aplicação

### Inicialização (Startup)

```
1. JVM inicia
2. Spring Boot context loads
3. ActiveProfiles determinado por SPRING_PROFILES_ACTIVE
4. Beans inicializados:
   - DataSource (MySQL connection pool)
   - RabbitTemplate (RabbitMQ client)
   - Security components (JWT provider)
5. Flyway migrations executadas
6. Application pronta para requisições
7. Health check: /actuator/health = UP
```

### Requisição HTTP (Request/Response)

```
1. Cliente envia request com JWT token
2. Spring Security Filter valida token
3. Controller recebe request
4. Service executa lógica
5. Repository acessa banco
6. Response retorna ao cliente
7. Logs são registrados
```

### Shutdown Gracioso

```
1. SIGTERM recebido (Fly.io scale down)
2. Spring inicia shutdown
3. Conexões ativas finalizadas
4. Connection pools fechados
5. RabbitMQ desconectado
6. JVM encerra
```

---

## 📦 Dependências Principais

```
Spring Boot 3.5.5
├── Spring Web
├── Spring Data JPA
├── Spring Security
├── Spring AMQP (RabbitMQ)
├── Spring Cloud (optional)
│
Kotlin 1.9.25
├── kotlin-stdlib
├── kotlin-reflect
└── kotlin-test-junit5

Banco de Dados
├── MySQL Connector (8.0)
├── Flyway (migrations)
└── HikariCP (connection pooling)

RabbitMQ
├── amqp-client (5.28.0)
└── spring-boot-starter-amqp

Segurança
├── java-jwt (4.4.0)
├── spring-security-core
└── spring-security-web

API Documentation
└── springdoc-openapi (2.3.0)

DevOps
├── docker (containerization)
├── docker-compose (orchestration)
└── flyctl (deployment)
```

---

## 🌐 Interações Externas

```
┌──────────────┐
│   Frontend   │◄──────────────────┐
│  (Web/App)   │                    │
└──────┬───────┘                    │
       │                            │
       │ HTTPS JWT                  │ Response JSON
       │                            │
       ▼                            │
┌──────────────┐                    │
│  Fly.io API  │────────────────────┘
│   Gateway    │
└──────┬───────┘
       │
       │ Direct Connection
       │ (Internal network)
       ▼
┌──────────────────────┐
│  Coffee Application  │
├──────────────────────┤
│ - Controllers        │
│ - Services           │
│ - Security           │
└────┬─────────────────┘
     │
     ├────────────────┬──────────────┐
     │                │              │
     ▼                ▼              ▼
┌─────────┐    ┌──────────┐    ┌──────────┐
│ MySQL   │    │ RabbitMQ │    │ Actuator │
│ Database│    │ Broker   │    │ (Metrics)│
└─────────┘    └──────────┘    └──────────┘
```

---

## 🔐 Camadas de Segurança

```
Layer 1: Network
├── Fly.io Auto SSL/TLS
├── Force HTTPS
└── DDoS Protection

Layer 2: API Gateway
├── Rate Limiting
├── CORS Validation
└── Request Validation

Layer 3: Authentication
├── JWT Token
├── Token Validation
└── User Context

Layer 4: Authorization
├── Role-Based Access Control (RBAC)
├── @PreAuthorize annotations
└── Method-level security

Layer 5: Data Protection
├── Parameterized queries (JPA)
├── Input validation
├── SQL injection prevention
└── XSS prevention (Jackson)

Layer 6: Secrets Management
├── Environment variables
├── Fly.io Secrets (production)
├── Never hardcoded
└── Regular rotation
```

---

## 📈 Escalabilidade

```
Atual (Single Instance)
┌──────────────────────┐
│  Coffee App (1x)     │
│  MySQL (RDS)         │
│  RabbitMQ (Managed)  │
└──────────────────────┘

Futuro (Horizontal Scaling)
┌──────────────────────────────────────┐
│  Fly.io Load Balancer                │
├──────┬──────────────────────┬────────┤
│      │                      │        │
▼      ▼                      ▼        ▼
App-1  App-2  App-3    App-N  ...
│      │        │        │
└──────┴────────┴────────┘
       │
       ▼
┌──────────────────┐
│  Shared MySQL    │
│  (Multi-region)  │
└──────────────────┘
       │
       ▼
┌──────────────────┐
│  Shared RabbitMQ │
│  (CloudAMQP)     │
└──────────────────┘
```

---

## 🎯 Métricas Monitoradas

```
Application Health
├── Database Connectivity
├── RabbitMQ Connectivity
├── Disk Space
└── Memory Usage

Performance
├── Request latency (p50, p99)
├── Error rate
├── Request throughput
└── Connection pool utilization

Security
├── Failed auth attempts
├── 403 Forbidden responses
├── Suspicious request patterns
└── Token generation rate

Business Metrics
├── Transaction count
├── Revenue
├── Active users
└── Product inventory
```

---

**Arquitectura finalizada e documentada com sucesso!** ✅

