#!/bin/bash
set -e

echo "========================================="
echo "  NexusBank - Build Script"
echo "========================================="

for service in infrastructure/eureka-server infrastructure/config-server infrastructure/api-gateway customer-service admin-service; do
    echo "Building $service..."
    cd $service
    mvn clean package -DskipTests
    cd ../..
done

echo "All services built successfully!"
