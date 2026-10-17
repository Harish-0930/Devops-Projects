@echo off
setlocal
chcp 65001 >nul

echo [*] Building NexusBank frontend...
cd /d "%~dp0devops\docker"
docker-compose build frontend
if errorlevel 1 (
    echo [X] Frontend build failed
    exit /b 1
)

echo [*] Starting NexusBank frontend...
docker-compose up -d --no-deps frontend
if errorlevel 1 (
    echo [X] Frontend startup failed
    exit /b 1
)

echo [OK] Frontend is available at http://localhost:3000
echo      Start database_run.bat and backend_run.bat first.
