@echo off
REM Coffee PDV - Docker Shutdown Script
REM Run this file to stop the application

cd /d "%~dp0coffee"

echo.
echo ========================================
echo Coffee PDV - Docker Shutdown
echo ========================================
echo.

echo Stopping Docker containers...
echo.

docker compose down

echo.
echo ========================================
echo Docker containers stopped!
echo ========================================
echo.
pause

