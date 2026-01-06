# Passo a Passo: Teste Local da Stack Docker Prod

## Resumo Rápido

Você tem 4 arquivos novos para deploy produção:
1. **Dockerfile** — constrói a imagem (multi-stage, otimizado)
2. **docker-compose.prod.yml** — orquestra MySQL + app
3. **.env.prod.example** — template de variáveis (seguro commitar)
4. **.env.prod** — arquivo local com valores (NÃO commitar)
5. **README-DOCKER-PROD.md** — documentação completa
6. **deploy-local-prod.ps1** — script automatizado (Windows)

## Teste Local: Passo a Passo

### Passo 1: Abrir Terminal PowerShell

Na pasta `D:\workSpace\coffee\coffee`, abra um terminal PowerShell:
```powershell
# Confirmar que você está na pasta correta
cd D:\workSpace\coffee\coffee
pwd  # deve retornar D:\workSpace\coffee\coffee
```

### Passo 2: Validar .env.prod

Verifique se o arquivo `.env.prod` foi criado:
```powershell
Get-Item .env.prod
# Se não existir, foi criado já (veja em cima na criação de arquivos)
```

Abra em um editor e revise os valores (são teste, então está OK deixar como está):
```powershell
notepad .env.prod
# Editar se necessário, depois Ctrl+S e fechar
```

### Passo 3: Build Maven

```powershell
# Build Maven (gera o JAR)
.\mvnw.cmd -DskipTests package
```

**Esperado:**
- Vê compilação em Kotlin/Java
- Flyway é executado (migrations)
- Último output: `BUILD SUCCESS`
- Tempo: ~2-3 minutos (primeira vez; depois mais rápido)

**Se falhar:**
- Mostrar output do erro aqui
- Posso analisar e corrigir

### Passo 4: Rodar docker-compose

Após Maven terminar com sucesso:

```powershell
# Start stack: MySQL + app
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d --build

# Esperado output:
# [+] Building 2.4s (10/10) FINISHED
# [+] Running 2/2 ✓
```

### Passo 5: Verificar status

```powershell
# Ver containers rodando
docker-compose -f docker-compose.prod.yml ps

# Esperado:
# NAME              STATUS
# coffee-db         Up (healthy)
# coffee-app        Up (healthy)
```

### Passo 6: Ver logs da app

```powershell
# Logs da app (vê startup + erros)
docker-compose -f docker-compose.prod.yml logs app

# Esperado final: "Started CoffeeApplicationKt"
```

### Passo 7: Testar health

```powershell
# Verificar health check (app pronto para requisições)
Invoke-WebRequest http://localhost:8080/actuator/health

# Esperado:
# StatusCode : 200
# Content    : {"status":"UP"}
```

### Passo 8: Parar stack (quando terminar teste)

```powershell
# Parar e remover containers (mas mantém volume db_data)
docker-compose -f docker-compose.prod.yml down

# Para remover tudo incluindo dados:
# docker-compose -f docker-compose.prod.yml down -v
```

## Alternativa: Script Automatizado

Se preferir rodar tudo junto, use o script PowerShell (Passo 1 e 2 acima, depois):

```powershell
# Permitir scripts local
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process

# Rodar script (faz build + docker-compose up + logs)
.\deploy-local-prod.ps1
```

## Troubleshooting Rápido

| Problema | Solução |
|----------|---------|
| **Port 3306 já em uso** | Parar MySQL local ou change port em compose |
| **Port 8080 já em uso** | Mudar em `docker-compose.prod.yml` ou kill processo |
| **Build Maven falha** | Verificar Java 21 instalado (`java -version`) |
| **Docker não encontrado** | Instalar Docker Desktop ou Docker engine |
| **App não conecta no DB** | Esperar healthcheck do MySQL (~20s), ver logs |
| **.env.prod não lido** | Conferir caminho e sintaxe (export não precisa aqui) |

## Próximas Ações

Após teste local com sucesso:

1. **Commit código** (exceto `.env.prod`):
   ```powershell
   git add Dockerfile .dockerignore docker-compose.prod.yml .env.prod.example README-DOCKER-PROD.md deploy-local-prod.ps1
   git commit -m "feat: add Docker production stack with MySQL"
   git push origin main
   ```

2. **Deploy em VPS**:
   - Seguir README-DOCKER-PROD.md → "Deploy em Produção"
   - Criar `.env.prod` na VPS com valores reais
   - Rodar `docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d --build`

3. **Ajustes VPS** (próximo passo depois de teste):
   - Nginx Reverse Proxy (SSL/TLS)
   - Healthcheck aprimorado
   - Backup estratégia

---

**Pronto? Roda os passos acima e me avisa o resultado!**

