# Solução: Erro de Build no Fly.io - "/pom.xml: not found"

## Problema Identificado

Você recebeu este erro ao fazer deploy no Fly.io:

```
ERROR: failed to calculate checksum of ref [...]: "/pom.xml": not found
ERROR: failed to calculate checksum of ref [...]: "/src": not found
```

## Causa Raiz

O Fly.io está tentando copiar arquivos (`COPY pom.xml .` e `COPY src ./src`) mas não consegue encontrá-los porque:

1. **Contexto de build incorreto**: O `flyctl deploy` foi executado de uma pasta que não é a raiz do projeto Maven
2. **Estrutura do repositório**: O projeto Maven está em `D:\workSpace\coffee\coffee\` mas o Fly.io estava buscando os arquivos na raiz

## Solução Implementada

### 1. ✅ Corrigir fly.toml

Alteramos:
```toml
# ANTES
[build]
  builder = "docker"
  dockerfile = "Dockerfile"

# DEPOIS
[build]
  builder = "docker"
  dockerfile = "./Dockerfile"
```

### 2. ✅ Certificar a estrutura do diretório

A estrutura correta deve ser:
```
D:\workSpace\coffee\coffee\
├── pom.xml              ← Arquivo raiz do Maven
├── Dockerfile           ← Dockerfile do projeto
├── fly.toml            ← Configuração do Fly.io
├── mvnw                ← Maven wrapper
├── mvnw.cmd            ← Maven wrapper para Windows
├── src/                ← Código-fonte
│   ├── main/
│   └── test/
├── .dockerignore       ← Arquivos ignorados no Docker build
└── .gitignore          ← Arquivos ignorados no Git
```

## Como Fazer Deploy Corretamente

### Opção 1: Usando o novo script PowerShell (RECOMENDADO)

```powershell
# 1. Navegar para a pasta do projeto
cd D:\workSpace\coffee\coffee

# 2. Executar o script de deploy
.\deploy-flyio.ps1 -AppName coffee-idf

# Ou com nome da app customizado:
.\deploy-flyio.ps1 -AppName seu-nome-da-app
```

O script irá:
- ✅ Validar a estrutura do projeto
- ✅ Verificar se `.env.prod` existe
- ✅ Executar `flyctl deploy`
- ✅ Fornecer dicas de troubleshooting caso falhe

### Opção 2: Manualmente com flyctl

```powershell
# 1. Certificar-se de estar na pasta correta
cd D:\workSpace\coffee\coffee

# 2. Fazer login no Fly.io (se necessário)
flyctl auth login

# 3. Verificar que está configurado corretamente
flyctl info -a coffee-idf

# 4. Fazer deploy (SEM flags --build-only)
flyctl deploy -a coffee-idf

# 5. Ver status
flyctl status -a coffee-idf

# 6. Ver logs
flyctl logs -a coffee-idf
```

## Checklist Pré-Deploy

Antes de fazer deploy, certifique-se de:

- [ ] Estar na pasta `D:\workSpace\coffee\coffee\`
- [ ] `pom.xml` existe na pasta atual
- [ ] `Dockerfile` existe na pasta atual
- [ ] `fly.toml` existe na pasta atual
- [ ] `src/` existe na pasta atual
- [ ] `.env.prod` existe e está configurado
- [ ] `.dockerignore` está presente
- [ ] Fazer commit de todas as mudanças: `git add . && git commit -m "Deploy fix"`

## Variáveis de Ambiente Necessárias

Certifique-se que `.env.prod` contém:

```env
# Database
DATABASE_URL=mysql://user:password@host:3306/coffee_db
MYSQL_ROOT_PASSWORD=seu_senha
MYSQL_DATABASE=coffee_db
MYSQL_USER=coffee_user
MYSQL_PASSWORD=coffee_pass

# Security
JWT_SECRET=sua_chave_secreta_muito_longa_e_segura
SPRING_PROFILES_ACTIVE=prod

# Application
SERVER_PORT=8080
JAVA_TOOL_OPTIONS=-XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport
```

## Troubleshooting

### Erro: "Configuration is valid" mas ainda falha

Limpe o cache do Docker e tente novamente:
```powershell
# Em última instância
flyctl deploy -a coffee-idf --quiet

# Ver logs em tempo real
flyctl logs -a coffee-idf --follow
```

### Erro: "failed to resolve"

O Docker Hub pode estar lento. Espere um pouco e tente novamente.

### Erro: "authentication required"

```powershell
flyctl auth login
flyctl auth token  # Para gerar token de acesso
```

### Erro: "/src not found" persiste

1. Deletar o app no Fly.io:
   ```powershell
   flyctl apps delete coffee-idf
   ```

2. Criar novo app:
   ```powershell
   flyctl launch --skip-deploy
   ```

3. Tentar deploy novamente

## Monitoramento Pós-Deploy

```powershell
# Ver status
flyctl status -a coffee-idf

# Ver processos rodando
flyctl ps -a coffee-idf

# SSH para debug
flyctl ssh console -a coffee-idf

# Ver variáveis de ambiente
flyctl config show -a coffee-idf
```

## Próximos Passos

1. ✅ Execute o script: `.\deploy-flyio.ps1`
2. ✅ Teste a aplicação em `https://coffee-idf.fly.dev`
3. ✅ Verifique os logs com `flyctl logs -a coffee-idf`
4. ✅ Configure health checks se necessário

---

**Data da solução**: Janeiro 2026

