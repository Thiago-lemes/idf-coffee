# Script para validar a stack Docker em produção local
# Arquivo: deploy-local-prod.ps1
# Uso: .\deploy-local-prod.ps1

Write-Host "=== Coffee App Local Production Stack ===" -ForegroundColor Green

# Step 1: Build Maven
Write-Host "`n[1/4] Building project with Maven..." -ForegroundColor Cyan
.\mvnw.cmd -DskipTests package
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed! Exiting." -ForegroundColor Red
    exit 1
}
Write-Host "Build successful!" -ForegroundColor Green

# Step 2: Check if .env.prod exists
Write-Host "`n[2/4] Checking environment file..." -ForegroundColor Cyan
if (-Not (Test-Path ".env.prod")) {
    Write-Host ".env.prod not found! Please create from .env.prod.example" -ForegroundColor Red
    exit 1
}
Write-Host ".env.prod found!" -ForegroundColor Green

# Step 3: Start docker-compose
Write-Host "`n[3/4] Starting Docker Compose stack..." -ForegroundColor Cyan
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d --build
if ($LASTEXITCODE -ne 0) {
    Write-Host "Docker Compose failed!" -ForegroundColor Red
    exit 1
}
Write-Host "Docker Compose started!" -ForegroundColor Green

# Step 4: Show logs
Write-Host "`n[4/4] Stack logs (waiting for app to start, press Ctrl+C to stop)..." -ForegroundColor Cyan
Start-Sleep -Seconds 5
docker-compose -f docker-compose.prod.yml logs -f

Write-Host "`n=== Setup Complete ===" -ForegroundColor Green
Write-Host "Access app at: http://localhost:8080" -ForegroundColor Green
Write-Host "MySQL running on: localhost:3306" -ForegroundColor Green
Write-Host "Health check: curl http://localhost:8080/actuator/health" -ForegroundColor Green

