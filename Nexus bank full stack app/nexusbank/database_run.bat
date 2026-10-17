@echo off
setlocal
chcp 65001 >nul

echo [*] Starting NexusBank database...
cd /d "%~dp0devops\docker"
docker-compose up -d mysql
if errorlevel 1 (
    echo [X] Database startup failed
    exit /b 1
)

echo [OK] MySQL is running on the internal Docker network as mysql:3306.
echo      It is intentionally not exposed on localhost.
