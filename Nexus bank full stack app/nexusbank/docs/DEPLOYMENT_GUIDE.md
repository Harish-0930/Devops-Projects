# NexusBank Deployment Guide

## Local Development

### Prerequisites
- Java 17 JDK
- Maven 3.9+
- Docker & Docker Compose
- Node.js 18+ (for frontend)

### Quick Start with Docker
```bash
./run.sh docker
```

### Manual Start
1. Start Eureka Server
2. Start Config Server (wait 10s)
3. Start API Gateway (wait 10s)
4. Start Customer Service (wait 10s)
5. Start Admin Service
6. Start Frontend (optional)

## Production Deployment

### Docker Swarm
```bash
docker stack deploy -c devops/docker/docker-compose.prod.yml nexusbank
```

### Kubernetes
```bash
kubectl apply -f devops/k8s/
kubectl get pods -n nexusbank
```

### Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| EUREKA_USERNAME | Eureka auth username | admin |
| EUREKA_PASSWORD | Eureka auth password | admin123 |
| JWT_SECRET | JWT signing key | (generated) |
| SPRING_CLOUD_CONFIG_URI | Config server URL | http://config-server:8888 |

## Monitoring
- Eureka Dashboard: http://localhost:8761
- Actuator endpoints: /actuator/health, /actuator/metrics
