# generate-secrets.ps1
# Script para gerar secrets aleatórios e seguros (Windows PowerShell)
# Uso: .\scripts\generate-secrets.ps1

Write-Host "🔐 Gerando Secrets Aleatórios e Seguros" -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray

# Função para gerar string aleatória
function Get-RandomString {
    param(
        [int]$Length = 32
    )

    $Bytes = [byte[]]::new($Length)
    $RNG = [System.Security.Cryptography.RNGCryptoServiceProvider]::new()
    $RNG.GetBytes($Bytes)
    $RNG.Dispose()

    return [Convert]::ToBase64String($Bytes)
}

# Gerar secrets
Write-Host "Gerando secrets..." -ForegroundColor Gray
$JwtSecret = Get-RandomString -Length 32
$DbPassword = (Get-RandomString -Length 32).Replace('+', '-').Replace('/', '_').Substring(0, 24)
$RabbitPassword = Get-RandomString -Length 32
$KeystorePassword = (Get-RandomString -Length 32).Replace('+', '-').Replace('/', '_').Substring(0, 16)

Write-Host ""
Write-Host "📋 Secrets Gerados (Copie e cole em .env.prod):" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host ""
Write-Host "JWT_SECRET=$JwtSecret" -ForegroundColor Yellow
Write-Host "SPRING_DATASOURCE_PASSWORD=$DbPassword" -ForegroundColor Yellow
Write-Host "RABBIT_PASSWORD=$RabbitPassword" -ForegroundColor Yellow
Write-Host "SSL_KEYSTORE_PASSWORD=$KeystorePassword" -ForegroundColor Yellow
Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray

# Opção de salvar em arquivo
$Response = Read-Host "Salvar em .env.prod.generated? (s/n)"
if ($Response -eq "s" -or $Response -eq "S") {
    $Content = @"
# Secrets Gerados em $(Get-Date)
# NÃO COMMITAR ESTE ARQUIVO NO GIT

JWT_SECRET=$JwtSecret
SPRING_DATASOURCE_PASSWORD=$DbPassword
RABBIT_PASSWORD=$RabbitPassword
SSL_KEYSTORE_PASSWORD=$KeystorePassword

# Outros valores (preencha conforme necessário)
SPRING_PROFILES_ACTIVE=prod
LOG_LEVEL=WARN
SPRING_DATASOURCE_URL=jdbc:mysql://seu-db-host:3306/coffee
SPRING_DATASOURCE_USERNAME=coffee_app
DB_POOL_SIZE=20
RABBIT_HOST=seu-rabbitmq-host
RABBIT_USER=coffee_app
RABBITMQ_EXCHANGE=coffee.exchange.prod
RABBITMQ_QUEUE=fechamento.caixa.queue.prod
RABBITMQ_ROUTING_KEY=fechamento.caixa
CORS_ALLOWED_ORIGINS=https://seu-dominio.com
"@

    $Content | Out-File -FilePath ".env.prod.generated" -Encoding UTF8
    Write-Host "✅ Secrets salvos em .env.prod.generated" -ForegroundColor Green
    Write-Host "⚠️  NÃO COMMITAR este arquivo no Git!" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "✅ Secrets gerados com sucesso!" -ForegroundColor Green
Write-Host ""
Write-Host "💡 Próximos passos:" -ForegroundColor Cyan
Write-Host "   1. Copie os valores acima" -ForegroundColor Gray
Write-Host "   2. Configure em seu secrets manager (Fly.io, AWS, etc)" -ForegroundColor Gray
Write-Host "   3. Nunca commite .env.prod ou .env.prod.generated no Git" -ForegroundColor Gray

