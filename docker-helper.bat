@echo off
REM Coffee PDV - Docker Helper Script for Windows
REM Usage: docker-helper.bat <command>

setlocal enabledelayedexpansion

REM Colors (limited in Windows CMD)
set GREEN=[32m
set YELLOW=[33m
set RED=[31m
set NC=[0m

:main
if "%1"=="" goto help
if /i "%1"=="start" goto start_services
if /i "%1"=="stop" goto stop_services
if /i "%1"=="restart" goto restart_services
if /i "%1"=="logs" goto view_logs
if /i "%1"=="logs-app" goto logs_app
if /i "%1"=="build" goto build_image
if /i "%1"=="clean" goto clean_all
if /i "%1"=="health" goto health_check
if /i "%1"=="help" goto help
echo [ERROR] Unknown command: %1
goto help

:start_services
echo [INFO] Starting Coffee PDV services...
if not exist ".env.docker" (
    echo [WARN] .env.docker not found. Please create it from .env.example
)
docker compose --env-file .env.docker up -d
echo [INFO] Services started successfully!
echo [INFO] App URL: http://localhost:8080
echo [INFO] MySQL: localhost:3306
echo [INFO] RabbitMQ Management: http://localhost:15672
goto end

:stop_services
echo [INFO] Stopping Coffee PDV services...
docker compose down
echo [INFO] Services stopped
goto end

:restart_services
echo [INFO] Restarting Coffee PDV services...
docker compose down
timeout /t 2
docker compose --env-file .env.docker up -d
echo [INFO] Services restarted
goto end

:view_logs
docker compose logs -f
goto end

:logs_app
docker compose logs -f coffee-app
goto end

:build_image
echo [INFO] Building Docker image...
docker compose build coffee-app
echo [INFO] Build completed
goto end

:clean_all
echo [WARN] This will remove all containers and volumes!
set /p response="Continue? (y/n): "
if /i "%response%"=="y" (
    docker compose down -v
    docker compose rm -f
    echo [INFO] Cleanup completed
) else (
    echo [INFO] Cleanup cancelled
)
goto end

:health_check
echo [INFO] Checking service health...
echo.
echo MySQL Health:
docker compose exec mysql mysqladmin ping -h localhost
echo.
echo RabbitMQ Health:
docker compose exec rabbitmq rabbitmq-diagnostics ping
echo.
echo App Health:
curl -f http://localhost:8080/actuator/health
goto end

:help
echo Coffee PDV - Docker Helper
echo.
echo Usage: docker-helper.bat ^<command^>
echo.
echo Commands:
echo   start       - Start all services
echo   stop        - Stop all services
echo   restart     - Restart all services
echo   logs        - View all logs
echo   logs-app    - View app logs only
echo   build       - Build Docker image
echo   clean       - Remove all containers and volumes
echo   health      - Check service health
echo   help        - Show this help message
echo.
goto end

:end
endlocal

