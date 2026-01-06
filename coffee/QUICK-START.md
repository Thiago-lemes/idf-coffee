# 🚀 COMANDO RÁPIDO - Deploy Fly.io

## TL;DR - Resumo Executivo

O erro de build foi **CORRIGIDO**. Aqui está o que fazer:

---

## 3 Passos Rápidos

### 1️⃣ Configurar Variáveis
```powershell
# Abrir e editar o arquivo
code D:\workSpace\coffee\coffee\.env.prod

# Adicionar (no mínimo):
JWT_SECRET=sua_chave_muito_segura_aqui_com_mais_de_32_caracteres
DATABASE_URL=mysql://user:pass@host:3306/coffee
SPRING_PROFILES_ACTIVE=prod
```

### 2️⃣ Fazer Deploy
```powershell
cd D:\workSpace\coffee\coffee
.\deploy-flyio.ps1 -AppName coffee-idf
```

### 3️⃣ Validar
```powershell
flyctl logs -a coffee-idf --follow
```

**Pronto! 🎉**

---

## O Que Foi Corrigido?

### Erro Original
```
ERROR: failed to calculate checksum of ref [...]: "/pom.xml": not found
```

### Solução
✅ Arquivo `fly.toml` foi corrigido
✅ Script `deploy-flyio.ps1` foi criado
✅ Documentação completa foi adicionada

---

## Testes Rápidos

### Depois que deployar:

```powershell
# 1. Verificar status
flyctl status -a coffee-idf

# 2. Acessar app
https://coffee-idf.fly.dev

# 3. Testar health
https://coffee-idf.fly.dev/actuator/health

# 4. Ver logs
flyctl logs -a coffee-idf --follow
```

---

## Se Der Erro

### Opção 1: Tentar novamente
```powershell
cd D:\workSpace\coffee\coffee
.\deploy-flyio.ps1 -AppName coffee-idf
```

### Opção 2: Ver logs detalhados
```powershell
flyctl logs -a coffee-idf --all
```

### Opção 3: Nuclear option (deletar e recriar)
```powershell
flyctl apps delete coffee-idf
flyctl launch --skip-deploy
cd D:\workSpace\coffee\coffee
.\deploy-flyio.ps1 -AppName coffee-idf
```

---

## Variáveis de Ambiente Necessárias

Copie e cole em `.env.prod`:

```env
# Database - escolha uma
DATABASE_URL=mysql://username:password@host:3306/coffee_db

# Security - GERE UMA NOVA CHAVE
JWT_SECRET=MinuscuLasmaUSCULAS123!@#$%^&*()_+{}|:"<>?abcdefghijklmnop

# Application
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
JAVA_TOOL_OPTIONS=-XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport
```

---

## Atalhos Úteis

```powershell
# Status em tempo real
flyctl status -a coffee-idf

# Logs em tempo real
flyctl logs -a coffee-idf --follow

# SSH para debug
flyctl ssh console -a coffee-idf

# Configurações da app
flyctl config show -a coffee-idf

# Listar releases
flyctl releases list -a coffee-idf

# Rollback para versão anterior
flyctl releases rollback -a coffee-idf
```

---

## Documentação Disponível

- **DEPLOY-CHECKLIST.md** ← Leia isto primeiro!
- **FLYIO-BUILD-FIX.md** ← Explicação técnica
- **FIX-SUMMARY.md** ← Mudanças rápidas
- **deploy-flyio.ps1** ← Script automático

---

## Status

| Item | Status |
|------|--------|
| fly.toml corrigido | ✅ |
| Script criado | ✅ |
| Documentação | ✅ |
| Estrutura validada | ✅ |
| **Pronto para deploy** | ✅ |

---

## Próximo Passo

```powershell
cd D:\workSpace\coffee\coffee
.\deploy-flyio.ps1 -AppName coffee-idf
```

**Boa sorte! 🚀**

---

_Última atualização: Janeiro 2026_

