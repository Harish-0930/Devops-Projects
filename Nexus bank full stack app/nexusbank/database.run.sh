#!/bin/bash

echo "[*] Starting NexusBank database..."

cd "$(dirname "$0")/devops/docker" || {
    echo "[X] Failed to change to devops/docker directory"
    exit 1
}

docker compose up -d mysql

if [ $? -ne 0 ]; then
    echo "[X] Database startup failed"
    exit 1
fi

echo "[OK] MySQL is running on the internal Docker network as mysql:3306."
echo "     It is intentionally not exposed on localhost."