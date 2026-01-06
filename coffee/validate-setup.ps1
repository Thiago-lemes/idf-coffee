#!/usr/bin/env pwsh

# Script de validação do setup Docker
# Verifica se tudo está configurado corretamente

$pass = "✓"
$fail = "✗"
$warn = "⚠"
$checkPass = 0
$checkTotal = 0

Write-Host "`n╔════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  Coffee PDV - Docker Setup Validation              ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════╝`n" -ForegroundColor Cyan

# Check 1: Docker
Write-Host "[Check 1/8] Docker Installation..." -ForegroundColor Gray
$dockerCheck = docker --version 2>$null
if ($dockerCheck) {
    Write-Host " $pass Docker is installed" -ForegroundColor Green
    $checkPass++
} else {
    Write-Host " $fail Docker not found - install from https://www.docker.com" -ForegroundColor Red
}
$checkTotal++

# Check 2: Docker Compose
Write-Host "[Check 2/8] Docker Compose..." -ForegroundColor Gray
$composeCheck = docker-compose --version 2>$null
if ($composeCheck) {
    Write-Host " $pass Docker Compose is installed" -ForegroundColor Green
    $checkPass++
} else {
    Write-Host " $fail Docker Compose not found" -ForegroundColor Red
}
$checkTotal++

# Check 3: Maven Wrapper
Write-Host "[Check 3/8] Maven Wrapper..." -ForegroundColor Gray
if (Test-Path "mvnw.cmd") {
    Write-Host " $pass Maven wrapper found" -ForegroundColor Green
    $checkPass++
} else {
    Write-Host " $fail Maven wrapper not found" -ForegroundColor Red
}
$checkTotal++

# Check 4: Dockerfile
Write-Host "[Check 4/8] Dockerfile..." -ForegroundColor Gray
if (Test-Path "Dockerfile") {
    Write-Host " $pass Dockerfile exists" -ForegroundColor Green
    $checkPass++
} else {
    Write-Host " $fail Dockerfile not found" -ForegroundColor Red
}
$checkTotal++

# Check 5: docker-compose.yml
Write-Host "[Check 5/8] docker-compose.yml..." -ForegroundColor Gray
if (Test-Path "docker-compose.yml") {
    $composeValid = docker-compose -f docker-compose.yml config 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host " $pass docker-compose.yml is valid" -ForegroundColor Green
        $checkPass++
    } else {
        Write-Host " $fail docker-compose.yml has syntax errors" -ForegroundColor Red
    }
} else {
    Write-Host " $fail docker-compose.yml not found" -ForegroundColor Red
}
$checkTotal++

# Check 6: docker-compose.prod.yml
Write-Host "[Check 6/8] docker-compose.prod.yml..." -ForegroundColor Gray
if (Test-Path "docker-compose.prod.yml") {
    $composeProdValid = docker-compose -f docker-compose.prod.yml config 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host " $pass docker-compose.prod.yml is valid" -ForegroundColor Green
        $checkPass++
    } else {
        Write-Host " $fail docker-compose.prod.yml has syntax errors" -ForegroundColor Red
    }
} else {
    Write-Host " $fail docker-compose.prod.yml not found" -ForegroundColor Red
}
$checkTotal++

# Check 7: Environment files
Write-Host "[Check 7/8] Environment Files..." -ForegroundColor Gray
if (Test-Path ".env") {
    Write-Host " $pass .env file exists" -ForegroundColor Green
} else {
    Write-Host " $warn .env file not found (run: copy .env.example .env)" -ForegroundColor Yellow
}

if (Test-Path ".env.example") {
    Write-Host " $pass .env.example template found" -ForegroundColor Green
    $checkPass++
} else {
    Write-Host " $fail .env.example not found" -ForegroundColor Red
}
$checkTotal++

# Check 8: pom.xml
Write-Host "[Check 8/8] pom.xml..." -ForegroundColor Gray
if (Test-Path "pom.xml") {
    Write-Host " $pass pom.xml found" -ForegroundColor Green
    $checkPass++
} else {
    Write-Host " $fail pom.xml not found" -ForegroundColor Red
}
$checkTotal++

Write-Host "`n╔════════════════════════════════════════════════════╗" -ForegroundColor Cyan

if ($checkPass -eq $checkTotal) {
    Write-Host "║  ✓ All checks passed! You're ready to deploy.  ║" -ForegroundColor Green
    Write-Host "║                                                 ║" -ForegroundColor Green
    Write-Host "║  Next steps:                                    ║" -ForegroundColor Green
    Write-Host "║    1. copy .env.example .env                    ║" -ForegroundColor Green
    Write-Host "║    2. .\deploy.ps1 -Environment dev             ║" -ForegroundColor Green
    Write-Host "║    3. Open http://localhost:8080                ║" -ForegroundColor Green
} else {
    Write-Host "║  ⚠ Some checks failed. Please review above.    ║" -ForegroundColor Yellow
    Write-Host "║  Failed: $($checkTotal - $checkPass)/$checkTotal checks                        ║" -ForegroundColor Yellow
}

Write-Host "║                                                 ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════╝`n" -ForegroundColor Cyan

if ($checkPass -lt $checkTotal) {
    exit 1
} else {
    exit 0
}

