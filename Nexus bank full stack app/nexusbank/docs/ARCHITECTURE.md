# NexusBank Architecture

## Overview
NexusBank is a microservices-based online banking system built with Spring Boot and React.

## Architecture Diagram

```
                    ┌─────────────┐
                    │   React     │
                    │  Frontend   │
                    │   :3000     │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ API Gateway │
                    │   :8080     │
                    │  JWT Auth   │
                    └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   ┌────▼─────┐     ┌─────▼──────┐    ┌─────▼─────┐
   │ Customer │     │   Admin    │    │  Eureka   │
   │ Service  │◄────│  Service   │    │  Server   │
   │  :8081   │     │   :8082    │    │   :8761   │
   │  (H2)    │     │  (H2)      │    │           │
   └──────────┘     └────────────┘    └───────────┘
        │
   ┌────▼─────┐
   │  Config  │
   │  Server  │
   │  :8888   │
   └──────────┘
```

## Service Communication
- **Admin → Customer**: OpenFeign with Resilience4j Circuit Breaker
- **All Services → Eureka**: Service Registration & Discovery
- **All Services → Config**: Centralized Configuration
- **Client → Gateway**: JWT Token Validation

## Security Flow
1. Client sends login request to Gateway
2. Gateway routes to Customer/Admin Service
3. Service validates credentials and returns JWT
4. Client sends JWT in Authorization header
5. Gateway validates JWT before routing
6. Services check role-based access

