# deploy-to-flyio.ps1
# Script para fazer deploy seguro no Fly.io (Windows PowerShell)
# Uso: .\scripts\deploy-to-flyio.ps1 -Ambiente "prod"

param(
    [string]$Ambiente = "prod"
)

$AppName = "coffee-pdv"

Write-Host "🚀 Iniciando deploy para Fly.io - Ambiente: $Ambiente" -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray

# 1. Validar branch
Write-Host "📌 Verificando branch..." -ForegroundColor Cyan
$Branch = git rev-parse --abbrev-ref HEAD
if ($Branch -ne "main" -and $Branch -ne "master") {
    Write-Host "⚠️  Você está na branch '$Branch'. Deploy deve ser feito de 'main' ou 'master'." -ForegroundColor Yellow
    $Continue = Read-Host "Continuar mesmo assim? (s/n)"
    if ($Continue -ne "s") {
        exit 1
    }
}

# 2. Verificar mudanças não-commitadas
Write-Host "📌 Verificando status do Git..." -ForegroundColor Cyan
$Status = git status --porcelain
if ($Status) {
    Write-Host "❌ Há mudanças não-commitadas. Commit ou discard antes de fazer deploy." -ForegroundColor Red
    git status
    exit 1
}

# 3. Build local
Write-Host "📌 Build local..." -ForegroundColor Cyan
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Build falhou!" -ForegroundColor Red
    exit 1
}

# 4. Validar fly.toml
Write-Host "📌 Validando fly.toml..." -ForegroundColor Cyan
$FlyToml = Get-Content fly.toml
if ($FlyToml -match '(password|secret|token)' -and -not ($FlyToml -match '^\s*#')) {
    Write-Host "❌ fly.toml contém possíveis secrets! Remova antes de fazer deploy." -ForegroundColor Red
    exit 1
}

# 5. Carregar variáveis de .env.prod
Write-Host "📌 Carregando secrets de .env.prod..." -ForegroundColor Cyan
if (-not (Test-Path ".env.prod")) {
    Write-Host "⚠️  Arquivo .env.prod não encontrado." -ForegroundColor Yellow
    $JwtSecret = Read-Host "JWT_SECRET"
    $DbPassword = Read-Host -AsSecureString "SPRING_DATASOURCE_PASSWORD"
} else {
    # Ler .env.prod (simples parsing)
    $EnvVars = @{}
    Get-Content .env.prod | Where-Object { $_ -notmatch '^\s*#' -and $_ -match '=' } | ForEach-Object {
        $Key, $Value = $_ -split '=', 2
        $EnvVars[$Key.Trim()] = $Value.Trim()
    }
}

# 6. Definir secrets no Fly.io
Write-Host "📌 Configurando secrets no Fly.io..." -ForegroundColor Cyan

$Secrets = @{
    "SPRING_PROFILES_ACTIVE" = "prod"
    "LOG_LEVEL" = "WARN"
    "JWT_SECRET" = $EnvVars["JWT_SECRET"]
    "SPRING_DATASOURCE_URL" = $EnvVars["SPRING_DATASOURCE_URL"]
    "SPRING_DATASOURCE_USERNAME" = $EnvVars["SPRING_DATASOURCE_USERNAME"]
    "SPRING_DATASOURCE_PASSWORD" = $EnvVars["SPRING_DATASOURCE_PASSWORD"]
    "DB_POOL_SIZE" = $EnvVars["DB_POOL_SIZE"] ?? "20"
    "RABBIT_HOST" = $EnvVars["RABBIT_HOST"]
    "RABBIT_USER" = $EnvVars["RABBIT_USER"]
    "RABBIT_PASSWORD" = $EnvVars["RABBIT_PASSWORD"]
    "CORS_ALLOWED_ORIGINS" = $EnvVars["CORS_ALLOWED_ORIGINS"]
}

$SecretArgs = @()
$Secrets.GetEnumerator() | ForEach-Object {
    if ($_.Value) {
        $SecretArgs += "$($_.Key)=$($_.Value)"
    }
}

flyctl secrets set @SecretArgs
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erro ao configurar secrets!" -ForegroundColor Red
    exit 1
}

# 7. Deploy
Write-Host "📌 Fazendo deploy no Fly.io..." -ForegroundColor Cyan
flyctl deploy --app $AppName
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Deploy falhou!" -ForegroundColor Red
    exit 1
}

# 8. Monitorar logs
Write-Host "📌 Monitorando logs..." -ForegroundColor Cyan
Write-Host "Aguarde a app iniciar (30-60 segundos)..." -ForegroundColor Gray
Start-Sleep -Seconds 10
flyctl logs --app $AppName | Select-Object -First 50

# 9. Testar health
Write-Host "📌 Testando health endpoint..." -ForegroundColor Cyan
$Domain = (flyctl info --app $AppName | Select-String "hostname").ToString().Split()[-1]
Start-Sleep -Seconds 5

try {
    $Health = Invoke-WebRequest -Uri "https://$Domain/api/actuator/health" -UseBasicParsing
    Write-Host "✅ Health check OK" -ForegroundColor Green
    Write-Host $Health.Content -ForegroundColor Gray
} catch {
    Write-Host "⚠️  Health check retornou erro. Verifique os logs." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "✅ Deploy concluído com sucesso!" -ForegroundColor Green
Write-Host "🔗 URL: https://$Domain" -ForegroundColor Cyan
Write-Host "📊 Dashboard: https://fly.io/apps/$AppName" -ForegroundColor Cyan

