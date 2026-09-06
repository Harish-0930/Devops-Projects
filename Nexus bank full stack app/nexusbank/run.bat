@echo off
chcp 65001 >nul
echo =========================================
echo   NexusBank - Build and Run Script (Windows)
echo =========================================
echo.

if "%1"=="" goto :usage
if "%1"=="docker" goto :docker
if "%1"=="build" goto :build
if "%1"=="local" goto :local
if "%1"=="test" goto :test
if "%1"=="clean" goto :clean
if "%1"=="status" goto :status
if "%1"=="stop" goto :stop
goto :usage

:build
echo [*] Building all services...
call :check_prerequisites

set "BASEDIR=%CD%"

for %%s in ("infrastructure\eureka-server:Eureka Server" "infrastructure\config-server:Config Server" "infrastructure\api-gateway:API Gateway" "customer-service:Customer Service" "admin-service:Admin Service") do (
    for /f "tokens=1,2 delims=:" %%a in (%%s) do (
        echo [*] Building %%b...
        cd /d "%BASEDIR%\%%a"
        call mvn clean install -DskipTests -q
        if errorlevel 1 (
            echo [X] %%b build failed
            exit /b 1
        )
        echo [OK] %%b built successfully
    )
)
cd /d "%BASEDIR%"
goto :eof

:docker
call :build
echo [*] Starting with Docker Compose...
cd devops\docker
echo [*] Building frontend first (avoids npm competing with all Java image builds)...
docker-compose build frontend
if errorlevel 1 (
    echo [X] Frontend Docker build failed
    cd ..\..
    exit /b 1
)
docker-compose up --build -d
if errorlevel 1 (
    echo [X] Docker Compose startup failed
    cd ..\..
    exit /b 1
)
cd ..\..
echo.
echo =========================================
echo   NexusBank is now running!
echo =========================================
echo.
echo   Eureka Dashboard:    http://localhost:8761
echo   API Gateway:         http://localhost:8080
echo   Customer Service:    http://localhost:8081
echo   Admin Service:       http://localhost:8082
echo   Frontend:            http://localhost:3000
echo   H2 Console for Customer Service: http://localhost:8081/h2-console
echo   JDBC URL: jdbc:h2:mem:nexusbank_customer
echo   H2 Console for Admin Service:    http://localhost:8082/h2-console
echo   JDBC URL: jdbc:h2:mem:nexusbank_admin
echo =========================================
echo Credentials:
echo   Admin Service:       admin / admin123
echo   Customer Service:    johndoe / password123
echo   Eureka Server:        admin / admin123
echo =========================================
echo.
echo   To stop: docker-compose -f devops/docker/docker-compose.yml down
echo.
goto :eof

:local
echo [*] To run locally, open separate terminals and run:
echo.
echo   Terminal 1: cd infrastructure\eureka-server ^&^& mvn spring-boot:run
echo   Terminal 2: cd infrastructure\config-server ^&^& mvn spring-boot:run
echo   Terminal 3: cd infrastructure\api-gateway ^&^& mvn spring-boot:run
echo   Terminal 4: cd customer-service ^&^& mvn spring-boot:run
echo   Terminal 5: cd admin-service ^&^& mvn spring-boot:run
echo   Terminal 6: cd frontend ^&^& npm start
echo.
goto :eof

:test
cd customer-service && call mvn test
cd ..\admin-service && call mvn test
cd ..
echo [OK] Tests completed!
goto :eof

:clean
echo [*] Cleaning...
for %%d in (infrastructure\eureka-server infrastructure\config-server infrastructure\api-gateway customer-service admin-service) do (
    cd %%d && call mvn clean -q && cd ..\..
)
cd devops\docker
docker-compose down -v 2>nul
cd ..\..
echo [OK] Clean completed!
goto :eof

:status
echo [*] Checking service status...
curl -s http://localhost:8761/actuator/health >nul && (echo [OK] Eureka Server: RUNNING) || (echo [X] Eureka Server: STOPPED)
curl -s http://localhost:8080/actuator/health >nul && (echo [OK] API Gateway: RUNNING) || (echo [X] API Gateway: STOPPED)
curl -s http://localhost:8081/actuator/health >nul && (echo [OK] Customer Service: RUNNING) || (echo [X] Customer Service: STOPPED)
curl -s http://localhost:8082/actuator/health >nul && (echo [OK] Admin Service: RUNNING) || (echo [X] Admin Service: STOPPED)
goto :eof

:stop
echo [*] Stopping services...
cd devops\docker
docker-compose down
cd ..\..
echo [OK] All services stopped!
goto :eof

:usage
echo Usage: run.bat [command]
echo.
echo Commands:
echo   build    Build all services
echo   docker   Build and run with Docker Compose
echo   local    Show local run instructions
echo   test     Run all tests
echo   clean    Clean build artifacts
echo   status   Check service status
echo   stop     Stop all services
echo.
goto :eof

:check_prerequisites
echo [*] Checking prerequisites...
java -version >nul 2>&1 || (echo [X] Java not found. Install Java 21. & exit /b 1)
mvn -version >nul 2>&1 || (echo [X] Maven not found. Install Maven 3.9+. & exit /b 1)
docker --version >nul 2>&1 || (echo [X] Docker not found. Install Docker. & exit /b 1)
echo [OK] All prerequisites met!
goto :eof
