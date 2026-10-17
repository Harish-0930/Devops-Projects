#!/bin/bash

echo "[*] Building NexusBank backend services..."

cd "$(dirname "$0")" || {
    echo "[X] Failed to change to project directory"
    exit 1
}

./run.sh build

if [ $? -ne 0 ]; then
    echo "[X] Backend build failed"
    exit 1
fi

echo "[*] Starting NexusBank backend services..."

cd devops/docker || {
    echo "[X] Failed to change to devops/docker directory"
    exit 1
}

docker compose up --build -d \
    eureka-server \
    config-server \
    api-gateway \
    customer-service \
    admin-service

if [ $? -ne 0 ]; then
    echo "[X] Backend startup failed"
    exit 1
fi

echo "[OK] Backend is starting."
echo "     Eureka:      http://localhost:8761"
echo "     API Gateway: http://localhost:8080"