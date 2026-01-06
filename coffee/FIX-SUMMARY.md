# Sumário de Correções - Deploy Fly.io

## Problema
Erro ao fazer build no Fly.io:
```
ERROR: failed to calculate checksum of ref [...]: "/pom.xml": not found
ERROR: failed to calculate checksum of ref [...]: "/src": not found
```

## Arquivos Modificados

### 1. `fly.toml` ✅ CORRIGIDO
- **Antes**: `dockerfile = "Dockerfile"`
- **Depois**: `dockerfile = "./Dockerfile"`
- **Motivo**: Caminho relativo explícito garante que o Fly.io encontre o arquivo

## Arquivos Criados

### 1. `deploy-flyio.ps1` ✅ NOVO
Script PowerShell para fazer deploy no Fly.io com validações automáticas:
- Valida estrutura do projeto
- Verifica `.env.prod`
- Fornece dicas de troubleshooting
- Mostra informações de pós-deploy

**Como usar:**
```powershell
cd D:\workSpace\coffee\coffee
.\deploy-flyio.ps1 -AppName coffee-idf
```

### 2. `FLYIO-BUILD-FIX.md` ✅ NOVO
Documentação completa com:
- Explicação do problema
- Causas raiz
- Soluções implementadas
- Checklist pré-deploy
- Variáveis de ambiente necessárias
- Troubleshooting
- Próximos passos

## O Que Fazer Agora

### Passo 1: Validar Estrutura (✅ JÁ FEITO)
Confirmamos que todos os arquivos estão no lugar:
- ✅ `pom.xml` existe
- ✅ `src/` existe
- ✅ `mvnw` existe
- ✅ `Dockerfile` existe
- ✅ `fly.toml` existe

### Passo 2: Configurar Variáveis (⏳ TODO)
Certifique-se que `.env.prod` está configurado com:
- `DATABASE_URL` (seu banco MySQL no Fly.io ou gerenciado)
- `JWT_SECRET` (chave para geração de tokens)
- Outras variáveis conforme necessário

### Passo 3: Fazer Deploy (⏳ TODO)
```powershell
cd D:\workSpace\coffee\coffee
.\deploy-flyio.ps1 -AppName coffee-idf
```

## Checklist Final

- [ ] `fly.toml` foi atualizado com `./Dockerfile`
- [ ] `pom.xml` está na raiz de `coffee/`
- [ ] `src/` está na raiz de `coffee/`
- [ ] `.env.prod` está configurado
- [ ] `.dockerignore` existe e contém `target`, `.git`, etc
- [ ] Fazer commit: `git add . && git commit -m "Fix: Fly.io build context"`
- [ ] Executar: `.\deploy-flyio.ps1`

## Resultado Esperado

Quando tudo está correto, você verá:

```
==> Building image
#7 [build 3/6] COPY pom.xml .
#7 DONE 0.0s  ← SEM ERRO

#9 [build 5/6] COPY src ./src
#9 DONE 0.0s  ← SEM ERRO

...

Successfully tagged coffee-app:latest
==> Pushing image to registry
...
Deploy successful!
```

## Dúvidas?

Consulte `FLYIO-BUILD-FIX.md` para:
- Troubleshooting detalhado
- Variáveis de ambiente necessárias
- Comandos de monitoramento pós-deploy
- Como fazer rollback se necessário

---

**Status**: ✅ Pronto para deploy
**Data**: Janeiro 2026

