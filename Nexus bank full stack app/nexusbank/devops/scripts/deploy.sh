#!/bin/bash
set -e

echo "========================================="
echo "  NexusBank - Deploy Script"
echo "========================================="

# Build Docker images
echo "Building Docker images..."
docker-compose -f devops/docker/docker-compose.yml build

# Start services
echo "Starting services..."
docker-compose -f devops/docker/docker-compose.yml up -d

echo "Deployment complete!"
echo ""
echo "Services:"
echo "  Eureka:    http://localhost:8761"
echo "  Gateway:   http://localhost:8080"
echo "  Frontend:  http://localhost:3000"
