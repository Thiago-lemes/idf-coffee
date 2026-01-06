#!/usr/bin/env pwsh

# Script de deployment do projeto Coffee PDV
# Uso: .\deploy.ps1 -Environment dev
# ou: .\deploy.ps1 -Environment prod

param(
    [ValidateSet("dev", "prod")]
    [string]$Environment = "dev"
)

$ErrorActionPreference = "Stop"

$composeFile = if ($Environment -eq "prod") { "docker-compose.prod.yml" } else { "docker-compose.yml" }
$envFile = if ($Environment -eq "prod") { ".env.prod" } else { ".env" }

Write-Host "`n╔════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   Coffee PDV - Docker Deployment      ║" -ForegroundColor Cyan
Write-Host "║   Environment: $Environment" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════╝`n" -ForegroundColor Cyan

# Verificar se arquivo de env existe
if (-not (Test-Path $envFile)) {
    Write-Host "❌ Erro: Arquivo $envFile não encontrado" -ForegroundColor Red
    Write-Host "   Crie uma cópia: Copy-Item .env.example $envFile" -ForegroundColor Yellow
    exit 1
}

Write-Host "📦 Step 1: Building Maven package..." -ForegroundColor Yellow
if ($Environment -eq "dev") {
    & .\mvnw.cmd -DskipTests package -q
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Maven build falhou" -ForegroundColor Red
        exit 1
    }
    Write-Host "✅ Maven build completo`n" -ForegroundColor Green
} else {
    Write-Host "⏭️  Pulando Maven (será feito no Docker)`n" -ForegroundColor Gray
}

Write-Host "🐳 Step 2: Starting Docker Compose..." -ForegroundColor Yellow
docker-compose -f $composeFile --env-file $envFile up -d
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Docker Compose falhou" -ForegroundColor Red
    exit 1
}

Write-Host "`n⏳ Step 3: Waiting for services to be healthy..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# Verificar MySQL
Write-Host -NoNewline "   Checking MySQL... " -ForegroundColor Gray
for ($i = 1; $i -le 30; $i++) {
    $result = docker-compose -f $composeFile exec -T mysql mysqladmin ping -h 127.0.0.1 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅" -ForegroundColor Green
        break
    }
    if ($i -eq 30) {
        Write-Host "❌ (Timeout)" -ForegroundColor Red
        exit 1
    }
    Write-Host -NoNewline "." -ForegroundColor Gray
    Start-Sleep -Seconds 2
}

# Verificar App
Write-Host -NoNewline "   Checking App... " -ForegroundColor Gray
for ($i = 1; $i -le 60; $i++) {
    $result = docker-compose -f $composeFile exec -T app curl -f http://localhost:8080/actuator/health 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅" -ForegroundColor Green
        break
    }
    if ($i -eq 60) {
        Write-Host "⚠️  (Still starting)" -ForegroundColor Yellow
        break
    }
    Write-Host -NoNewline "." -ForegroundColor Gray
    Start-Sleep -Seconds 2
}

Write-Host "`n╔════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║   ✅ Deployment Successful!            ║" -ForegroundColor Green
Write-Host "╚════════════════════════════════════════╝`n" -ForegroundColor Green

Write-Host "🌐 Application: http://localhost:8080" -ForegroundColor Cyan
Write-Host "💾 Database: localhost:3306`n" -ForegroundColor Cyan

Write-Host "📊 Useful commands:" -ForegroundColor Yellow
Write-Host "   View logs:        docker-compose -f $composeFile logs -f app" -ForegroundColor Gray
Write-Host "   Stop services:    docker-compose -f $composeFile down" -ForegroundColor Gray
Write-Host "   Status:           docker-compose -f $composeFile ps`n" -ForegroundColor Gray

