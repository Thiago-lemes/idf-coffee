@echo off
REM Coffee PDV - Docker Startup Script
REM Run this file to start the application

cd /d "%~dp0coffee"

echo.
echo ========================================
echo Coffee PDV - Docker Startup
echo ========================================
echo.

if not exist ".env.docker" (
    echo ERROR: .env.docker file not found!
    echo.
    echo Please create .env.docker file first.
    pause
    exit /b 1
)

echo Starting Docker Compose...
echo.

docker compose --env-file .env.docker up -d

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Failed to start Docker Compose
    pause
    exit /b 1
)

echo.
echo ========================================
echo Docker containers started successfully!
echo ========================================
echo.
echo Access the application at:
echo   http://localhost:8080
echo.
echo Swagger UI at:
echo   http://localhost:8080/swagger-ui.html
echo.
echo RabbitMQ Management at:
echo   http://localhost:15672 (guest/guest)
echo.
echo MySQL at:
echo   localhost:3306 (coffee_user/coffeepwd123)
echo.
echo View logs with:
echo   docker compose logs -f
echo.
echo Stop containers with:
echo   docker compose down
echo.
pause

