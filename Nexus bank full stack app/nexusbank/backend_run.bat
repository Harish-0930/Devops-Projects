@echo off
setlocal
chcp 65001 >nul

echo [*] Building NexusBank backend services...
cd /d "%~dp0"
call "%~dp0run.bat" build
if errorlevel 1 (
    echo [X] Backend build failed
    exit /b 1
)

echo [*] Starting NexusBank backend services...
cd /d "%~dp0devops\docker"
docker-compose up --build -d eureka-server config-server api-gateway customer-service admin-service
if errorlevel 1 (
    echo [X] Backend startup failed
    exit /b 1
)

echo [OK] Backend is starting.
echo      Eureka:      http://localhost:8761
echo      API Gateway: http://localhost:8080
