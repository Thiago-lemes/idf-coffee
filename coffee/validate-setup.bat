@echo off
REM Script de validação do setup Docker
REM Verifica se tudo está configurado corretamente

setlocal enabledelayedexpansion

echo.
echo ╔════════════════════════════════════════════════════╗
echo ║  Coffee PDV - Docker Setup Validation              ║
echo ╚════════════════════════════════════════════════════╝
echo.

set "pass=✓"
set "fail=✗"
set "warn=⚠"
set "check_pass=0"
set "check_total=0"

REM Verificar Docker
echo [Check 1/8] Docker Installation...
docker --version >nul 2>&1
if !errorlevel! equ 0 (
    echo  %pass% Docker is installed
    set /a check_pass=!check_pass! + 1
) else (
    echo  %fail% Docker not found - install from https://www.docker.com
)
set /a check_total=!check_total! + 1

REM Verificar Docker Compose
echo [Check 2/8] Docker Compose...
docker-compose --version >nul 2>&1
if !errorlevel! equ 0 (
    echo  %pass% Docker Compose is installed
    set /a check_pass=!check_pass! + 1
) else (
    echo  %fail% Docker Compose not found
)
set /a check_total=!check_total! + 1

REM Verificar Maven
echo [Check 3/8] Maven Wrapper...
if exist mvnw.cmd (
    echo  %pass% Maven wrapper found
    set /a check_pass=!check_pass! + 1
) else (
    echo  %fail% Maven wrapper not found
)
set /a check_total=!check_total! + 1

REM Verificar Dockerfile
echo [Check 4/8] Dockerfile...
if exist Dockerfile (
    echo  %pass% Dockerfile exists
    set /a check_pass=!check_pass! + 1
) else (
    echo  %fail% Dockerfile not found
)
set /a check_total=!check_total! + 1

REM Verificar docker-compose.yml
echo [Check 5/8] docker-compose.yml...
if exist docker-compose.yml (
    docker-compose -f docker-compose.yml config >nul 2>&1
    if !errorlevel! equ 0 (
        echo  %pass% docker-compose.yml is valid
        set /a check_pass=!check_pass! + 1
    ) else (
        echo  %fail% docker-compose.yml has syntax errors
    )
) else (
    echo  %fail% docker-compose.yml not found
)
set /a check_total=!check_total! + 1

REM Verificar docker-compose.prod.yml
echo [Check 6/8] docker-compose.prod.yml...
if exist docker-compose.prod.yml (
    docker-compose -f docker-compose.prod.yml config >nul 2>&1
    if !errorlevel! equ 0 (
        echo  %pass% docker-compose.prod.yml is valid
        set /a check_pass=!check_pass! + 1
    ) else (
        echo  %fail% docker-compose.prod.yml has syntax errors
    )
) else (
    echo  %fail% docker-compose.prod.yml not found
)
set /a check_total=!check_total! + 1

REM Verificar .env
echo [Check 7/8] Environment Files...
if exist .env (
    echo  %pass% .env file exists
) else (
    echo  %warn% .env file not found (run: copy .env.example .env)
)
if exist .env.example (
    echo  %pass% .env.example template found
    set /a check_pass=!check_pass! + 1
) else (
    echo  %fail% .env.example not found
)
set /a check_total=!check_total! + 1

REM Verificar pom.xml
echo [Check 8/8] pom.xml...
if exist pom.xml (
    echo  %pass% pom.xml found
    set /a check_pass=!check_pass! + 1
) else (
    echo  %fail% pom.xml not found
)
set /a check_total=!check_total! + 1

echo.
echo ╔════════════════════════════════════════════════════╗

if !check_pass! equ !check_total! (
    echo ║  ✓ All checks passed! You're ready to deploy.  ║
    echo ║                                                 ║
    echo ║  Next steps:                                    ║
    echo ║    1. copy .env.example .env                    ║
    echo ║    2. .\deploy.ps1 -Environment dev             ║
    echo ║    3. Open http://localhost:8080                ║
) else (
    echo ║  ⚠ Some checks failed. Please review above.    ║
    echo ║  Failed: %check_total%-%check_pass% checks                        ║
)

echo ║                                                 ║
echo ╚════════════════════════════════════════════════════╝
echo.

if !check_pass! lss !check_total! (
    exit /b 1
) else (
    exit /b 0
)

