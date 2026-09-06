#!/bin/bash

# =========================================================
# NexusBank - Build and Run Script (Linux)
# =========================================================

echo "========================================="
echo "  NexusBank - Build and Run Script (Linux)"
echo "========================================="
echo


# =========================================================
# Build all services
# =========================================================

build() {

    echo "[*] Building all services..."

    check_prerequisites || exit 1

    BASEDIR="$(pwd)"

    services=(
        "infrastructure/eureka-server:Eureka Server"
        "infrastructure/config-server:Config Server"
        "infrastructure/api-gateway:API Gateway"
        "customer-service:Customer Service"
        "admin-service:Admin Service"
    )

    for service in "${services[@]}"; do

        SERVICE_DIR="${service%%:*}"
        SERVICE_NAME="${service##*:}"

        echo
        echo "[*] Building $SERVICE_NAME..."
        echo "[*] Directory: $SERVICE_DIR"

        cd "$BASEDIR/$SERVICE_DIR" || {
            echo "[X] Cannot enter $SERVICE_DIR"
            cd "$BASEDIR"
            exit 1
        }

        mvn clean install -DskipTests -q

        if [ $? -ne 0 ]; then
            echo "[X] $SERVICE_NAME build failed"
            cd "$BASEDIR"
            exit 1
        fi

        echo "[OK] $SERVICE_NAME built successfully"
    done

    cd "$BASEDIR" || exit 1

    echo
    echo "[OK] All services built successfully!"
}


# =========================================================
# Docker Build and Run
# =========================================================

docker_cmd() {

    echo "[*] Running Docker build and startup..."

    # Build Java services first
    build || exit 1

    BASEDIR="$(pwd)"

    echo
    echo "[*] Starting with Docker Compose..."

    cd "$BASEDIR/devops/docker" || {
        echo "[X] Docker Compose directory not found!"
        exit 1
    }

    echo
    echo "[*] Building frontend first..."
    echo "[*] This avoids npm competing with Java image builds..."

    docker compose build frontend

    if [ $? -ne 0 ]; then
        echo "[X] Frontend Docker build failed"
        cd "$BASEDIR"
        exit 1
    fi

    echo
    echo "[OK] Frontend Docker image built successfully!"

    echo
    echo "[*] Starting all services with Docker Compose..."

    docker compose up --build -d

    if [ $? -ne 0 ]; then
        echo "[X] Docker Compose startup failed"
        cd "$BASEDIR"
        exit 1
    fi

    cd "$BASEDIR" || exit 1

    echo
    echo "========================================="
    echo "  NexusBank is now running!"
    echo "========================================="
    echo
    echo "  Eureka Dashboard:    http://localhost:8761"
    echo "  API Gateway:         http://localhost:8080"
    echo "  Customer Service:    http://localhost:8081"
    echo "  Admin Service:       http://localhost:8082"
    echo "  Frontend:            http://localhost:3000"
    echo
    echo "  To check status:"
    echo "    ./run.sh status"
    echo
    echo "  To stop:"
    echo "    ./run.sh stop"
    echo
    echo "  Manual Docker Compose stop:"
    echo "    docker compose -f devops/docker/docker-compose.yml down"
    echo
}


# =========================================================
# Local Run Instructions
# =========================================================

local_cmd() {

    echo
    echo "[*] To run locally, open separate terminals and run:"
    echo

    echo "  Terminal 1:"
    echo "    cd infrastructure/eureka-server && mvn spring-boot:run"
    echo

    echo "  Terminal 2:"
    echo "    cd infrastructure/config-server && mvn spring-boot:run"
    echo

    echo "  Terminal 3:"
    echo "    cd infrastructure/api-gateway && mvn spring-boot:run"
    echo

    echo "  Terminal 4:"
    echo "    cd customer-service && mvn spring-boot:run"
    echo

    echo "  Terminal 5:"
    echo "    cd admin-service && mvn spring-boot:run"
    echo

    echo "  Terminal 6:"
    echo "    cd frontend && npm start"
    echo
}


# =========================================================
# Run Tests
# =========================================================

test_cmd() {

    echo "[*] Running tests..."

    BASEDIR="$(pwd)"

    echo
    echo "[*] Testing Customer Service..."

    cd "$BASEDIR/customer-service" || {
        echo "[X] Customer Service directory not found"
        exit 1
    }

    mvn test

    if [ $? -ne 0 ]; then
        echo "[X] Customer Service tests failed"
        cd "$BASEDIR"
        exit 1
    fi

    echo "[OK] Customer Service tests passed!"

    echo
    echo "[*] Testing Admin Service..."

    cd "$BASEDIR/admin-service" || {
        echo "[X] Admin Service directory not found"
        exit 1
    }

    mvn test

    if [ $? -ne 0 ]; then
        echo "[X] Admin Service tests failed"
        cd "$BASEDIR"
        exit 1
    fi

    echo "[OK] Admin Service tests passed!"

    cd "$BASEDIR" || exit 1

    echo
    echo "[OK] Tests completed successfully!"
}


# =========================================================
# Clean
# =========================================================

clean() {

    echo "[*] Cleaning..."

    BASEDIR="$(pwd)"

    services=(
        "infrastructure/eureka-server"
        "infrastructure/config-server"
        "infrastructure/api-gateway"
        "customer-service"
        "admin-service"
    )

    for service in "${services[@]}"; do

        echo
        echo "[*] Cleaning $service..."

        cd "$BASEDIR/$service" || {
            echo "[X] Cannot enter $service"
            cd "$BASEDIR"
            exit 1
        }

        mvn clean -q

        if [ $? -ne 0 ]; then
            echo "[X] Failed to clean $service"
            cd "$BASEDIR"
            exit 1
        fi

        echo "[OK] $service cleaned successfully"
    done

    cd "$BASEDIR/devops/docker" || {
        echo "[X] Docker Compose directory not found"
        cd "$BASEDIR"
        exit 1
    }

    echo
    echo "[*] Removing Docker Compose containers, networks and volumes..."

    docker compose down -v 2>/dev/null

    cd "$BASEDIR" || exit 1

    echo
    echo "[OK] Clean completed!"
}


# =========================================================
# Status
# =========================================================

status() {

    echo
    echo "[*] Checking service status..."
    echo

    # Eureka
    if curl -sf http://localhost:8761/actuator/health > /dev/null 2>&1; then
        echo "[OK] Eureka Server:    RUNNING"
    else
        echo "[X] Eureka Server:    STOPPED"
    fi

    # API Gateway
    if curl -sf http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "[OK] API Gateway:      RUNNING"
    else
        echo "[X] API Gateway:      STOPPED"
    fi

    # Customer Service
    if curl -sf http://localhost:8081/actuator/health > /dev/null 2>&1; then
        echo "[OK] Customer Service: RUNNING"
    else
        echo "[X] Customer Service: STOPPED"
    fi

    # Admin Service
    if curl -sf http://localhost:8082/actuator/health > /dev/null 2>&1; then
        echo "[OK] Admin Service:    RUNNING"
    else
        echo "[X] Admin Service:    STOPPED"
    fi

    echo
}


# =========================================================
# Stop Services
# =========================================================

stop() {

    echo "[*] Stopping services..."

    BASEDIR="$(pwd)"

    cd "$BASEDIR/devops/docker" || {
        echo "[X] Docker Compose directory not found"
        exit 1
    }

    docker compose down

    if [ $? -ne 0 ]; then
        echo "[X] Failed to stop Docker Compose services"
        cd "$BASEDIR"
        exit 1
    fi

    cd "$BASEDIR" || exit 1

    echo
    echo "[OK] All services stopped!"
    echo
}


# =========================================================
# Usage
# =========================================================

usage() {

    echo
    echo "Usage: ./run.sh [command]"
    echo
    echo "Commands:"
    echo "  build    Build all Java services"
    echo "  docker   Build and run with Docker Compose"
    echo "  local    Show local run instructions"
    echo "  test     Run all tests"
    echo "  clean    Clean build artifacts and Docker volumes"
    echo "  status   Check service status"
    echo "  stop     Stop all Docker Compose services"
    echo
}


# =========================================================
# Check Prerequisites
# =========================================================

check_prerequisites() {

    echo "[*] Checking prerequisites..."

    # Java
    if ! command -v java >/dev/null 2>&1; then
        echo "[X] Java not found."
        echo "    Install Java 21."
        return 1
    fi

    # Maven
    if ! command -v mvn >/dev/null 2>&1; then
        echo "[X] Maven not found."
        echo "    Install Maven 3.9+."
        return 1
    fi

    # Docker
    if ! command -v docker >/dev/null 2>&1; then
        echo "[X] Docker not found."
        echo "    Install Docker."
        return 1
    fi

    # Docker Compose
    if ! docker compose version >/dev/null 2>&1; then
        echo "[X] Docker Compose plugin not found."
        echo "    Install Docker Compose plugin."
        return 1
    fi

    # curl
    if ! command -v curl >/dev/null 2>&1; then
        echo "[X] curl not found."
        echo "    Install curl using: sudo apt install curl"
        return 1
    fi

    echo "[OK] All prerequisites met!"
}


# =========================================================
# Main Command Dispatcher
# =========================================================

case "$1" in

    docker)
        docker_cmd
        ;;

    build)
        build
        ;;

    local)
        local_cmd
        ;;

    test)
        test_cmd
        ;;

    clean)
        clean
        ;;

    status)
        status
        ;;

    stop)
        stop
        ;;

    *)
        usage
        ;;

esac