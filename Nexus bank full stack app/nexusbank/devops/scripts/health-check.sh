#!/bin/bash

echo "========================================="
echo "  NexusBank - Health Check"
echo "========================================="

SERVICES=(
    "Eureka Server:8761"
    "Config Server:8888"
    "API Gateway:8080"
    "Customer Service:8081"
    "Admin Service:8082"
)

for service in "${SERVICES[@]}"; do
    IFS=':' read -r name port <<< "$service"
    if curl -s -o /dev/null -w "%{http_code}" http://localhost:$port/actuator/health | grep -q "200\|UP"; then
        echo "✓ $name is running on port $port"
    else
        echo "✗ $name is NOT running on port $port"
    fi
done
