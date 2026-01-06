# ✅ Checklist de Deploy - Fly.io

## Antes de Fazer Deploy

### Pré-requisitos
- [ ] Fly.io CLI instalado: `flyctl version`
- [ ] Logado no Fly.io: `flyctl auth whoami`
- [ ] Docker Desktop instalado e rodando (para testes locais)
- [ ] Git configurado e repositório atualizado

### Estrutura do Projeto
- [ ] Estou na pasta: `D:\workSpace\coffee\coffee\`
- [ ] `pom.xml` existe aqui
- [ ] `Dockerfile` existe aqui
- [ ] `fly.toml` existe aqui (com `./Dockerfile` corrigido)
- [ ] `mvnw` existe aqui
- [ ] `src/` pasta existe aqui
- [ ] `.dockerignore` existe
- [ ] `.gitignore` existe

### Configuração
- [ ] `.env.prod` existe
- [ ] `.env.prod` contém `DATABASE_URL`
- [ ] `.env.prod` contém `JWT_SECRET`
- [ ] `.env.prod` contém `SPRING_PROFILES_ACTIVE=prod`
- [ ] `.env.prod` está no `.gitignore` (não deve ser commitado)

### Código
- [ ] Última versão do código no git
- [ ] Build local passa: `.\mvnw.cmd clean package -DskipTests`
- [ ] Sem erros de compilação

---

## Durante o Deploy

### Execução
1. [ ] Abrir PowerShell
2. [ ] Navegar para pasta: `cd D:\workSpace\coffee\coffee`
3. [ ] Executar script: `.\deploy-flyio.ps1 -AppName coffee-idf`
4. [ ] Aguardar conclusão (pode levar 5-10 minutos)

### Monitoramento
- [ ] Ver progresso do build: `flyctl status -a coffee-idf`
- [ ] Acompanhar logs: `flyctl logs -a coffee-idf`

---

## Após Deploy

### Validações Imediatas
- [ ] App mostrou status "Running": `flyctl ps -a coffee-idf`
- [ ] Health check passou: `flyctl status -a coffee-idf`
- [ ] Acessar URL: `https://coffee-idf.fly.dev`

### Testes Funcionais
- [ ] Página de health check retorna 200: `https://coffee-idf.fly.dev/actuator/health`
- [ ] Endpoint de login funciona: `POST https://coffee-idf.fly.dev/auth/login`
- [ ] Endpoints protegidos retornam 401 sem token
- [ ] Endpoints protegidos funcionam com token JWT válido

### Monitoramento
- [ ] Ver logs: `flyctl logs -a coffee-idf --follow`
- [ ] Ver uso de recursos: `flyctl status -a coffee-idf`
- [ ] Configurações: `flyctl config show -a coffee-idf`

---

## Troubleshooting Rápido

### Se falhar durante o build
```powershell
# Ver logs detalhados
flyctl logs -a coffee-idf

# Tentar novamente
flyctl deploy -a coffee-idf
```

### Se a app não inicia
```powershell
# SSH para debug
flyctl ssh console -a coffee-idf

# Ver logs internos
flyctl logs -a coffee-idf --all
```

### Se precisar fazer rollback
```powershell
# Ver releases anteriores
flyctl releases list -a coffee-idf

# Voltar para versão anterior
flyctl releases rollback -a coffee-idf
```

### Se precisar recriar tudo
```powershell
# Deletar app
flyctl apps delete coffee-idf

# Recriar
flyctl launch --skip-deploy
cd D:\workSpace\coffee\coffee
.\deploy-flyio.ps1 -AppName coffee-idf
```

---

## Variáveis de Ambiente Obrigatórias

Coloque no `.env.prod`:

```env
# ========== DATABASE ==========
# Para banco em outro lugar (AWS RDS, etc)
DATABASE_URL=mysql://username:password@host:3306/coffee_db

# Para banco local/Docker
MYSQL_ROOT_PASSWORD=root_senha_segura
MYSQL_DATABASE=coffee_db
MYSQL_USER=coffee_user
MYSQL_PASSWORD=coffee_pass

# ========== SECURITY ==========
JWT_SECRET=uma_chave_muito_longa_e_segura_com_mais_de_32_caracteres_minusculas_e_maiusculas_numeros_e_simbolos

# ========== APPLICATION ==========
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
JAVA_TOOL_OPTIONS=-XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport

# ========== OPTIONAL ==========
# Log level
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_ORG_BR_IDF_COFFEE=DEBUG

# Timezone
TZ=America/Sao_Paulo
```

---

## URLs Úteis

| Recurso | URL |
|---------|-----|
| Dashboard Fly.io | `https://fly.io/apps` |
| App Dashboard | `https://fly.io/apps/coffee-idf` |
| Documentação Fly | `https://fly.io/docs/` |
| Status da App | `flyctl status -a coffee-idf` |
| Logs em Tempo Real | `flyctl logs -a coffee-idf --follow` |

---

## Dicas de Sucesso

✅ **Sempre faça commit antes de deploy**
```powershell
git add .
git commit -m "Seu mensagem de commit"
```

✅ **Teste localmente primeiro**
```powershell
.\mvnw.cmd clean package -DskipTests
docker-compose up -d
# Acessar http://localhost:8080
```

✅ **Mantenha `.env.prod` secreto**
- Nunca commitar `.env.prod`
- Usar variáveis de ambiente do Fly.io
- Gerar `JWT_SECRET` novo e seguro

✅ **Monitore logs regularmente**
```powershell
flyctl logs -a coffee-idf --follow
```

---

## Próximas Ações Após Deploy

1. **Configurar Domínio Customizado** (opcional)
   ```powershell
   flyctl certs add seu-dominio.com -a coffee-idf
   ```

2. **Configurar Auto-Deploy via Git**
   ```powershell
   # Ativa deploy automático quando faz push
   flyctl deploy -a coffee-idf
   ```

3. **Monitorar Performance**
   - Usar Fly.io Dashboard
   - Configurar alertas

4. **Backup de Dados**
   - Se usar banco gerenciado do Fly.io, configurar backups
   - Considerar banco externo para produção

---

**Status**: ✅ Pronto para Deploy
**Última atualização**: Janeiro 2026
**Próximo passo**: Execute `.\deploy-flyio.ps1` 🚀

