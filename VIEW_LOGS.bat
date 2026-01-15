@echo off
REM Coffee PDV - Docker Logs Script
REM Run this file to see Docker logs

cd /d "%~dp0coffee"

echo.
echo ========================================
echo Coffee PDV - Docker Logs
echo ========================================
echo.
echo Showing logs from all containers...
echo Press Ctrl+C to stop
echo.

docker compose logs -f

pause

