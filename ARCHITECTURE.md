# Arquitetura e Estrutura do Projeto Coffee PDV

## 📋 Visão Geral

**Coffee** é uma aplicação de PDV (Ponto de Venda) desenvolvida em **Kotlin** com **Spring Boot 3.5.5**.

```
┌────────────────────────────────────────────┐
│         Coffee PDV - Arquitetura           │
│                                            │
│  ┌──────────────┐                         │
│  │  REST API    │  (Controllers)          │
│  │  OpenAPI 3.0 │  Endpoints JSON        │
│  └──────────────┘                         │
│         ▼                                   │
│  ┌──────────────┐                         │
│  │   Services   │  (Business Logic)       │
│  │  Validations │  Orchestration          │
│  └──────────────┘                         │
│         ▼                                   │
│  ┌──────────────────────────────────┐    │
│  │      Data Access Layer           │    │
│  │  ┌─────────────┐  ┌──────────┐  │    │
│  │  │  JPA Repos  │  │  Flyway  │  │    │
│  │  └─────────────┘  └──────────┘  │    │
│  └──────────────────────────────────┘    │
│         ▼                                   │
│  ┌──────────────────────────────────┐    │
│  │    Infrastructure & Persistence  │    │
│  │  ┌────────┐  ┌────────────────┐ │    │
│  │  │ MySQL  │  │   RabbitMQ     │ │    │
│  │  │   DB   │  │   Message Bus  │ │    │
│  │  └────────┘  └────────────────┘ │    │
│  └──────────────────────────────────┘    │
│                                            │
└────────────────────────────────────────────┘
```

---

## 🗂️ Estrutura de Pastas

```
coffee/
│
├── src/
│   ├── main/
│   │   ├── kotlin/org/br/idf/coffee/
│   │   │   ├── auth/                    # 🔐 Autenticação & JWT
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   └── service/
│   │   │   │
│   │   │   ├── categoria/               # 📂 Categorias de Produtos
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── mapper/
│   │   │   │   └── repository/
│   │   │   │
│   │   │   ├── produto/                 # 📦 Produtos
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── mapper/
│   │   │   │   └── repository/
│   │   │   │
│   │   │   ├── insumo/                  # 🥐 Insumos (Ingredients)
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── mapper/
│   │   │   │   └── repository/
│   │   │   │
│   │   │   ├── estoque/                 # 📊 Gestão de Estoque
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── mapper/
│   │   │   │   └── repository/
│   │   │   │
│   │   │   ├── transacoes/              # 💳 Transações (Vendas)
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── mapper/
│   │   │   │   └── repository/
│   │   │   │
│   │   │   ├── fluxo_caixa/             # 💰 Fechamento de Caixa
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── mapper/
│   │   │   │   ├── service/
│   │   │   │   └── repository/
│   │   │   │
│   │   │   ├── usuario/                 # 👤 Usuários
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── mapper/
│   │   │   │   └── repository/
│   │   │   │
│   │   │   ├── rabbit/                  # 🐰 Integração RabbitMQ
│   │   │   │   ├── config/
│   │   │   │   ├── consumer/
│   │   │   │   ├── event/
│   │   │   │   └── producer/
│   │   │   │
│   │   │   ├── security/                # 🔒 Spring Security
│   │   │   │   ├── config/
│   │   │   │   ├── filter/
│   │   │   │   └── provider/
│   │   │   │
│   │   │   ├── config/                  # ⚙️ Configurações
│   │   │   │   ├── datasource/
│   │   │   │   ├── jackson/
│   │   │   │   └── flyway/
│   │   │   │
│   │   │   └── CoffeeApplication.kt     # 🚀 Main Class
│   │   │
│   │   └── resources/
│   │       ├── application.yml           # Profile ativo
│   │       ├── application-dev-mySql.yml # Dev (MySQL local)
│   │       ├── application-docker.yml    # Docker (Compose)
│   │       ├── application-prod.yml      # Produção
│   │       └── db/migration/             # Flyway SQL
│   │           ├── V1__CREATE_TABLE_USUARIO.SQL
│   │           ├── V2__CREATE_TABLE_CATEGORIA.SQL
│   │           └── ... (até V9)
│   │
│   └── test/
│       └── kotlin/org/br/idf/coffee/
│           ├── CoffeeApplicationTests.kt
│           └── (testes futuros)
│
├── pom.xml                              # Maven configuration
├── Dockerfile                           # Multi-stage build
├── docker-compose.yml                   # Dev environment
├── docker-compose.prod.yml              # Prod reference
├── .dockerignore                        # Docker exclusions
├── .env.docker                          # Dev environment vars
├── .env.example                         # Template de vars
├── .env.prod                            # Prod template
├── docker-helper.sh                     # Helper scripts (Unix)
├── docker-helper.bat                    # Helper scripts (Windows)
├── Makefile                             # Makefile shortcuts
├── README.md                            # Documentação principal
├── DOCKER.md                            # Docker & deployment
├── RAILWAY.md                           # Railway.app guide
├── FLY.md                               # Fly.io guide
└── ARCHITECTURE.md                      # Este arquivo
```

---

## 🏗️ Padrão de Camadas (Layered Architecture)

### 1. **Controller Layer** (API)

**Responsabilidade:** Receber requisições HTTP, validar entrada, mapear para DTOs

**Localização:** `<modulo>/controller/`

**Exemplo:**
```kotlin
@RestController
@RequestMapping("/api/v1/produtos")
class ProdutoController(private val produtoService: ProdutoService) {
    
    @GetMapping("/{id}")
    fun getProduto(@PathVariable id: Long): ResponseEntity<ProdutoDTO> {
        val produto = produtoService.buscarPorId(id)
        return ResponseEntity.ok(produto.toDTO())
    }
    
    @PostMapping
    fun criarProduto(@RequestBody dto: CreateProdutoDTO): ResponseEntity<ProdutoDTO> {
        val produto = produtoService.criar(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(produto.toDTO())
    }
}
```

**Validações:**
- `@Valid` para DTOs
- `@NotNull`, `@Size`, `@Pattern` em DTOs
- Spring Validation framework

---

### 2. **DTO/Mapper Layer**

**Responsabilidade:** Transferência de dados entre camadas, conversão Entity ↔ DTO

**Localização:** `<modulo>/dto/` e `<modulo>/mapper/`

**Exemplo:**
```kotlin
// DTO - Input/Output
data class CreateProdutoDTO(
    @field:NotNull val nome: String,
    @field:NotNull val preco: BigDecimal,
    val descricao: String? = null
)

data class ProdutoDTO(
    val id: Long,
    val nome: String,
    val preco: BigDecimal,
    val descricao: String?,
    val ativo: Boolean
)

// Mapper
class ProdutoMapper {
    fun toDTO(entity: Produto): ProdutoDTO = 
        ProdutoDTO(
            id = entity.id,
            nome = entity.nome,
            preco = entity.preco,
            descricao = entity.descricao,
            ativo = entity.ativo
        )
    
    fun toEntity(dto: CreateProdutoDTO): Produto =
        Produto(
            nome = dto.nome,
            preco = dto.preco,
            descricao = dto.descricao
        )
}
```

**Benefícios:**
- Encapsulamento de entidades
- Segurança (não expõe IDs internos, relacionamentos)
- Flexibilidade (alterar DTO sem afetar Entity)

---

### 3. **Service Layer** (Business Logic)

**Responsabilidade:** Lógica de negócio, transações, orquestração

**Localização:** `<modulo>/service/`

**Exemplo:**
```kotlin
@Service
@Transactional
class ProdutoService(
    private val produtoRepository: ProdutoRepository,
    private val categoriaRepository: CategoriaRepository,
    private val produtoMapper: ProdutoMapper,
    private val rabbitmqProducer: RabbitmqProducer
) {
    
    fun criar(dto: CreateProdutoDTO): ProdutoDTO {
        // Validações de negócio
        require(!produtoRepository.existsByNome(dto.nome)) {
            "Produto com este nome já existe"
        }
        
        // Conversão
        val produto = produtoMapper.toEntity(dto)
        
        // Persistência
        val savedProduto = produtoRepository.save(produto)
        
        // Eventos (RabbitMQ)
        rabbitmqProducer.publishEvent(ProdutoCriadoEvent(savedProduto.id))
        
        return produtoMapper.toDTO(savedProduto)
    }
    
    fun buscarPorId(id: Long): ProdutoDTO {
        val produto = produtoRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Produto não encontrado")
        return produtoMapper.toDTO(produto)
    }
}
```

**Características:**
- `@Transactional` para ACID guarantees
- Validações de negócio (não apenas input validation)
- Orquestração entre repositórios e infra

---

### 4. **Repository Layer** (Data Access)

**Responsabilidade:** Persistência e queries no banco

**Localização:** `<modulo>/repository/`

**Exemplo:**
```kotlin
interface ProdutoRepository : JpaRepository<Produto, Long> {
    fun findByNomeIgnoreCase(nome: String): Produto?
    fun existsByNome(nome: String): Boolean
    fun findAllByAtivoTrue(): List<Produto>
    fun findByCategoria(categoria: Categoria): List<Produto>
}
```

**Spring Data JPA:**
- Gera implementação automaticamente
- Query methods derivados do nome
- `@Query` para queries customizadas
- Paginação com `Page<T>`

---

### 5. **Entity Layer** (Domain Model)

**Responsabilidade:** Representação das tabelas do banco

**Localização:** `<modulo>/entity/`

**Exemplo:**
```kotlin
@Entity
@Table(name = "produtos")
class Produto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false, unique = true)
    val nome: String,
    
    @Column(nullable = false)
    val preco: BigDecimal,
    
    @Column(columnDefinition = "TEXT")
    val descricao: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    val categoria: Categoria,
    
    @Column(nullable = false)
    val ativo: Boolean = true,
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    
    @UpdateTimestamp
    @Column(nullable = false)
    val atualizadoEm: LocalDateTime = LocalDateTime.now()
)
```

**Anotações importantes:**
- `@Entity` - Mapeia para tabela
- `@Column` - Define coluna
- `@ManyToOne`, `@OneToMany` - Relacionamentos
- `@CreationTimestamp`, `@UpdateTimestamp` - Auditar

---

### 6. **Infrastructure Layer**

**Responsabilidade:** Integrações externas, configurações

**Localização:** `config/`, `rabbit/`, `security/`

#### RabbitMQ Integration

```kotlin
// Event class
data class ProdutoCriadoEvent(val produtoId: Long)

// Producer
@Component
class ProdutoProducer(private val rabbitTemplate: RabbitTemplate) {
    fun publishProdutoCriado(evento: ProdutoCriadoEvent) {
        rabbitTemplate.convertAndSend(
            EXCHANGE_NAME,
            ROUTING_KEY,
            evento
        )
    }
}

// Consumer
@Component
class ProdutoConsumer {
    @RabbitListener(queues = [QUEUE_NAME])
    fun processProdutoCriado(evento: ProdutoCriadoEvent) {
        // Processar evento
        println("Produto criado: ${evento.produtoId}")
    }
}
```

#### Security Configuration

```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtAuthFilter: JwtAuthFilter) {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
}
```

---

## 📊 Fluxo de uma Requisição

### Exemplo: `POST /api/v1/produtos`

```
1. Cliente envia JSON
   POST /api/v1/produtos
   {
     "nome": "Café Espresso",
     "preco": 5.50,
     "descricao": "Espresso italiano"
   }

2. Spring valida @RequestBody
   → Spring Data Validation valida CreateProdutoDTO
   → Se falhar: retorna 400 Bad Request

3. Controller recebe CreateProdutoDTO válida
   → Controller chama service.criar(dto)

4. Service valida lógica de negócio
   → Verifica se produto já existe
   → Se existe: lança ResourceNotFoundException

5. Service mapeia DTO → Entity
   → produtoMapper.toEntity(dto)

6. Service persiste no banco
   → produtoRepository.save(produto)
   → Flyway já criou tabela
   → JPA/Hibernate gera INSERT

7. Service publica evento
   → rabbitmqProducer.publishProdutoCriado(event)
   → RabbitMQ entrega para subscribers

8. Service retorna DTO
   → Controller recebe DTO

9. Controller retorna 201 Created
   {
     "id": 1,
     "nome": "Café Espresso",
     "preco": 5.50,
     "descricao": "Espresso italiano",
     "ativo": true
   }

10. Consumer RabbitMQ processa evento
    → Atualiza cache
    → Envia notificação
    → Log analytics
```

---

## 🔐 Segurança

### JWT Authentication Flow

```
┌─────────────┐                    ┌──────────────┐
│   Client    │                    │  Coffee API  │
└──────┬──────┘                    └──────┬───────┘
       │                                  │
       │─ POST /api/v1/auth/login ───────→│
       │   { "username": "...", ... }     │
       │                                  │
       │← 200 OK ───────────────────────────│
       │   { "token": "eyJhbGc..." }      │
       │                                  │
       │─ GET /api/v1/produtos ─────────→│
       │   Authorization: Bearer eyJhbGc..│
       │                                  │
       │  [JwtAuthFilter valida token]    │
       │  [Extrai usuário]                │
       │  [Verifica permissões]           │
       │                                  │
       │← 200 OK ───────────────────────→│
       │   { "produtos": [...] }         │
       │                                  │
```

### Componentes de Segurança

1. **JwtAuthFilter**: Valida token JWT em cada requisição
2. **SecurityConfig**: Define endpoints públicos/protegidos
3. **BCryptPasswordEncoder**: Hash de senhas
4. **@PreAuthorize**: Autorização por anotação

---

## 📦 Dependências Principais

| Dependência | Versão | Propósito |
|-------------|--------|----------|
| Spring Boot | 3.5.5 | Framework principal |
| Java | 21 | Runtime |
| Kotlin | 1.9.25 | Linguagem |
| Spring Data JPA | 3.5.5 | ORM |
| Spring Security | 3.5.5 | Autenticação |
| MySQL Connector | latest | Driver JDBC |
| Flyway | latest | Migrações DB |
| RabbitMQ | 5.28.0 | Message broker |
| JWT | 4.4.0 | Token auth |
| SpringDoc OpenAPI | 2.3.0 | Swagger/OpenAPI |

---

## 🧪 Testes

### Estrutura de Testes

```
src/test/kotlin/org/br/idf/coffee/
├── CoffeeApplicationTests.kt
├── auth/
│   └── controller/AuthControllerTest.kt
├── produto/
│   └── service/ProdutoServiceTest.kt
└── ... (testes futuros)
```

### Exemplo de Teste

```kotlin
@SpringBootTest
@ActiveProfiles("test")
class ProdutoServiceTest(
    @Autowired private val produtoService: ProdutoService,
    @Autowired private val produtoRepository: ProdutoRepository
) {
    
    @BeforeEach
    fun setup() {
        produtoRepository.deleteAll()
    }
    
    @Test
    fun `deve criar produto com sucesso`() {
        // Arrange
        val dto = CreateProdutoDTO(
            nome = "Café Premium",
            preco = BigDecimal("9.99")
        )
        
        // Act
        val resultado = produtoService.criar(dto)
        
        // Assert
        assertThat(resultado.id).isNotNull()
        assertThat(resultado.nome).isEqualTo("Café Premium")
        assertThat(resultado.ativo).isTrue()
    }
}
```

---

## 🚀 Deployment

### Ambientes

| Ambiente | Profile | Banco | Config |
|----------|---------|-------|--------|
| Desenvolvimento | `dev-mysql` | MySQL local | `application-dev-mySql.yml` |
| Docker Local | `docker` | MySQL container | `application-docker.yml` |
| Produção | `prod` | Managed RDS/Cloud SQL | `application-prod.yml` |

### CI/CD Automático

Com Railway/Fly.io, cada push para `main`:

```
1. GitHub detecta push
2. CI pipeline clona código
3. Maven compila e testa
4. Docker image é buildada
5. Image é pushed para registry
6. App é redeployed automaticamente
7. Health checks confirmam sucesso
8. Logs são capturados
```

---

## 📈 Escalabilidade

### Horizontal Scaling (Multiple Instances)

```
                    ┌─────────────────┐
                    │  Load Balancer  │
                    └────────┬────────┘
                             │
                ┌────────────┼────────────┐
                ▼            ▼            ▼
            ┌────────┐  ┌────────┐  ┌────────┐
            │ App 1  │  │ App 2  │  │ App 3  │
            └────┬───┘  └────┬───┘  └────┬───┘
                 │            │           │
                 └────────────┬───────────┘
                              ▼
                      ┌───────────────┐
                      │  MySQL (RDS)  │
                      │   Database    │
                      └───────────────┘
```

### Vertical Scaling (Larger Instances)

- Aumentar memória: `flyctl scale memory 2gb`
- Aumentar CPU: `flyctl scale memory 2gb` (ajusta CPU proporcionalmente)
- Monitorar: `flyctl status`

---

## 🔄 Fluxo de Venda (Caso de Uso Complexo)

```
1. Cliente acessa carrinho
   → GET /api/v1/transacoes/{id}

2. Adiciona itens
   → POST /api/v1/transacoes/{id}/itens
   → Service valida estoque
   → Reserva quantidade

3. Aplica desconto
   → PATCH /api/v1/transacoes/{id}
   → Service valida cupom

4. Finaliza venda
   → POST /api/v1/transacoes/{id}/finalizar
   → Service:
     - Valida status
     - Calcula totais
     - Persiste transação
     - Atualiza estoque
     - Publica evento "VendaFinalizada"

5. Consumer RabbitMQ (Caixa)
   → Escuta evento
   → Atualiza fluxo de caixa
   → Registra movimento financeiro

6. Retorna confirmação
   → HTTP 200 OK
   → JSON com dados da venda
```

---

## 📝 Boas Práticas Implementadas

✅ **Separação de Responsabilidades** - Cada camada tem um propósito
✅ **DTOs** - Proteção de entidades
✅ **Transações** - `@Transactional` garante ACID
✅ **Validações** - Input validation + business validation
✅ **Eventos** - RabbitMQ para desacoplamento
✅ **Migrations** - Flyway para versionamento do DB
✅ **JWT** - Autenticação stateless
✅ **Async Processing** - RabbitMQ para operações demoradas
✅ **Logging** - SLF4J + Logback
✅ **Error Handling** - Global exception handler
✅ **OpenAPI** - Documentação automática
✅ **Docker** - Containerização pronta
✅ **Environment Profiles** - Configurações por ambiente

---

## 🔗 Referências

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [RabbitMQ](https://www.rabbitmq.com)
- [Flyway](https://flywaydb.org)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [OpenAPI 3.0](https://swagger.io/specification/)

---

**Última atualização:** Janeiro 2026
**Autor:** Coffee PDV Team

