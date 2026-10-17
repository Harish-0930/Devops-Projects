#!/bin/bash

echo "[*] Building NexusBank frontend..."

cd "$(dirname "$0")/devops/docker" || {
    echo "[X] Failed to change to devops/docker directory"
    exit 1
}

docker compose build frontend

if [ $? -ne 0 ]; then
    echo "[X] Frontend build failed"
    exit 1
fi

echo "[*] Starting NexusBank frontend..."

docker compose up -d --no-deps frontend

if [ $? -ne 0 ]; then
    echo "[X] Frontend startup failed"
    exit 1
fi

echo "[OK] Frontend is available at http://<ALB-DNS-NAME>:80 if using AWS ALB or http://localhost:3000 if running locally."
echo "Start database_run.sh and backend_run.sh first."