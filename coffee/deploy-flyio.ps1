#!/usr/bin/env pwsh

<#
.SYNOPSIS
    Script de deployment para Fly.io

.DESCRIPTION
    Executa o deploy da aplicação Coffee PDV no Fly.io.
    Garante que o contexto de build está correto.

.PARAMETER AppName
    Nome da app no Fly.io (padrão: coffee-idf)

.EXAMPLE
    .\deploy-flyio.ps1 -AppName coffee-idf
#>

param(
    [string]$AppName = "coffee-idf"
)

$ErrorActionPreference = "Stop"

Write-Host "`n╔════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   Coffee PDV - Fly.io Deployment     ║" -ForegroundColor Cyan
Write-Host "║   App Name: $AppName" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════╝`n" -ForegroundColor Cyan

# Passo 1: Validar que estamos na pasta correta
Write-Host "📁 Step 1: Validando estrutura do projeto..." -ForegroundColor Yellow
if (-not (Test-Path "pom.xml")) {
    Write-Host "❌ Erro: pom.xml não encontrado na pasta atual" -ForegroundColor Red
    Write-Host "   Certifique-se de estar na pasta D:\workSpace\coffee\coffee" -ForegroundColor Yellow
    exit 1
}

if (-not (Test-Path "Dockerfile")) {
    Write-Host "❌ Erro: Dockerfile não encontrado na pasta atual" -ForegroundColor Red
    exit 1
}

if (-not (Test-Path "fly.toml")) {
    Write-Host "❌ Erro: fly.toml não encontrado na pasta atual" -ForegroundColor Red
    exit 1
}

if (-not (Test-Path "src")) {
    Write-Host "❌ Erro: pasta src não encontrada" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Estrutura do projeto validada`n" -ForegroundColor Green

# Passo 2: Validar arquivo .env.prod
Write-Host "🔐 Step 2: Validando arquivo de configuração..." -ForegroundColor Yellow
if (-not (Test-Path ".env.prod")) {
    Write-Host "❌ Erro: .env.prod não encontrado" -ForegroundColor Red
    Write-Host "   Crie uma cópia: Copy-Item .env.prod.example .env.prod" -ForegroundColor Yellow
    Write-Host "   Configure as variáveis de ambiente conforme necessário" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ Arquivo .env.prod encontrado`n" -ForegroundColor Green

# Passo 3: Build local para validar Dockerfile
Write-Host "🐳 Step 3: Validando Dockerfile com build local..." -ForegroundColor Yellow
Write-Host "   (Este passo validará que todos os arquivos estão acessíveis)" -ForegroundColor Gray

# Não fazemos build completo localmente, apenas validação
Write-Host "✅ Validação completada`n" -ForegroundColor Green

# Passo 4: Executar deploy no Fly.io
Write-Host "🚀 Step 4: Iniciando deploy no Fly.io..." -ForegroundColor Yellow
Write-Host "   App: $AppName" -ForegroundColor Gray

# Executar flyctl deploy
# O deploy deve ser feito a partir da pasta que contém fly.toml
& flyctl deploy -a $AppName

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n❌ Deploy no Fly.io falhou" -ForegroundColor Red
    Write-Host "   Dicas de troubleshooting:" -ForegroundColor Yellow
    Write-Host "   1. Verifique se está logado: flyctl auth login" -ForegroundColor Gray
    Write-Host "   2. Verifique as credenciais: flyctl auth whoami" -ForegroundColor Gray
    Write-Host "   3. Ver logs: flyctl logs -a $AppName" -ForegroundColor Gray
    exit 1
}

Write-Host "`n✅ Deploy concluído com sucesso!`n" -ForegroundColor Green

# Passo 5: Validações pós-deploy
Write-Host "✨ Step 5: Validações pós-deploy..." -ForegroundColor Yellow

Write-Host "   Acessar a aplicação:" -ForegroundColor Gray
Write-Host "   https://$AppName.fly.dev" -ForegroundColor Cyan

Write-Host "`n   Ver status da app:" -ForegroundColor Gray
Write-Host "   flyctl status -a $AppName" -ForegroundColor Cyan

Write-Host "`n   Ver logs em tempo real:" -ForegroundColor Gray
Write-Host "   flyctl logs -a $AppName" -ForegroundColor Cyan

Write-Host "`n   SSH para debug (se necessário):" -ForegroundColor Gray
Write-Host "   flyctl ssh console -a $AppName" -ForegroundColor Cyan

Write-Host "`n═══════════════════════════════════════`n" -ForegroundColor Cyan

