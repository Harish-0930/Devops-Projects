# NexusBank - Enterprise Banking Application

## 🚀 Quick Start (Recommended: Docker)

### Prerequisites
- **Java 17** (JDK)
- **Maven 3.9+**
- **Docker** & **Docker Compose**
- **Node.js 18+** (for frontend only)

### Option 1: One-Command Run (Docker)

```bash
# Linux/Mac
chmod +x run.sh
./run.sh docker

# Windows
run.bat docker
```

This builds all services and starts everything. Access at:
- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8080
- **Eureka**: http://localhost:8761
- **H2 Console**: http://localhost:8081/h2-console

### Option 2: Step-by-Step Manual Run

#### Step 1: Build All Services
```bash
# Build each service
cd infrastructure/eureka-server && mvn clean package && cd ../..
cd infrastructure/config-server && mvn clean package && cd ../..
cd infrastructure/api-gateway && mvn clean package && cd ../..
cd customer-service && mvn clean package && cd ..
cd admin-service && mvn clean package && cd ..
```

#### Step 2: Start Infrastructure (in order)

Open **5 separate terminal windows** and run each in order:

**Terminal 1 - Eureka Server** (Service Discovery):
```bash
cd infrastructure/eureka-server
mvn spring-boot:run
# Wait for: "Started EurekaServerApplication"
```

**Terminal 2 - Config Server** (Centralized Config):
```bash
cd infrastructure/config-server
mvn spring-boot:run
# Wait for: "Started ConfigServerApplication"
```

**Terminal 3 - API Gateway** (wait 10 seconds for Eureka):
```bash
cd infrastructure/api-gateway
mvn spring-boot:run
```

**Terminal 4 - Customer Service** (wait 10 seconds for Config Server):
```bash
cd customer-service
mvn spring-boot:run
```

**Terminal 5 - Admin Service** (wait 10 seconds for Customer Service):
```bash
cd admin-service
mvn spring-boot:run
```

#### Step 3: Start Frontend (Optional)
```bash
cd frontend
npm install
npm start
# Opens at ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
```

---

## 📋 Available Commands

| Command | Description |
|---------|-------------|
| `./run.sh build` | Build all JARs |
| `./run.sh docker` | Full Docker deployment |
| `./run.sh local` | Show local run instructions |
| `./run.sh test` | Run all unit tests |
| `./run.sh status` | Check if services are running |
| `./run.sh logs` | View Docker logs |
| `./run.sh stop` | Stop all Docker containers |
| `./run.sh clean` | Clean builds + Docker |

---

## 🔑 Default Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Customer | `johndoe` | `password123` |

---

## 🌐 Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| Eureka Dashboard | http://localhost:8761 | Service Registry |
| Config Server | http://localhost:8888 | Centralized Config |
| API Gateway | http://localhost:8080 | Entry Point |
| Customer Service | http://localhost:8081 | Banking Operations |
| Admin Service | http://localhost:8082 | Admin Operations |
| Frontend | http://localhost:3000 | React UI |
| H2 Console (Cust) | http://localhost:8081/h2-console | Customer DB |
| H2 Console (Admin) | http://localhost:8082/h2-console | Admin DB |

---

## 🧪 Testing the APIs

### Customer Registration
```bash
curl -X POST http://localhost:8080/api/customers/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Smith",
    "email": "alice@example.com",
    "phoneNumber": "9876543211",
    "username": "alicesmith",
    "password": "password123",
    "address": {
      "street": "456 Oak Ave",
      "city": "Boston",
      "state": "MA",
      "country": "USA",
      "pincode": "02101"
    }
  }'
```

### Customer Login
```bash
curl -X POST http://localhost:8080/api/customers/login \
  -H "Content-Type: application/json" \
  -d '{"username": "johndoe", "password": "password123"}'
```

### Create Account (needs JWT token from login)
```bash
curl -X POST http://localhost:8080/api/accounts/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"accountType": "SAVINGS", "balance": 1000.00}'
```

### Deposit
```bash
curl -X POST http://localhost:8080/api/transactions/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"accountNumber": "NB202406200001001", "amount": 500.00, "remarks": "Salary deposit"}'
```

### Admin Login
```bash
curl -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### View All Customers (Admin)
```bash
curl -X GET http://localhost:8080/api/admin/customers \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

---

## 🐳 Docker Compose Commands

```bash
# Start all services
cd devops/docker
docker-compose up -d

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f customer-service

# Scale customer service to 2 instances
docker-compose up -d --scale customer-service=2

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Rebuild after code changes
docker-compose up -d --build
```

---

## ☸️ Kubernetes Deployment

```bash
# Apply all manifests
kubectl apply -f devops/k8s/

# Check status
kubectl get pods -n nexusbank
kubectl get svc -n nexusbank

# Port forward for local access
kubectl port-forward svc/api-gateway 8080:8080 -n nexusbank
kubectl port-forward svc/frontend 3000:80 -n nexusbank
```

---

## 🛠️ Troubleshooting

### Port Already in Use
```bash
# Find and kill process on port 8080
lsof -ti:8080 | xargs kill -9   # Mac/Linux
netstat -ano | findstr :8080    # Windows
```

### Service Registration Issues
- Make sure Eureka Server starts **first**
- Wait 10-15 seconds between starting services
- Check Eureka dashboard at http://localhost:8761

### Database Connection Issues
- H2 is in-memory, data resets on restart
- Use `data.sql` for seed data
- H2 Console credentials: `sa` / (empty password)

### JWT Token Issues
- Tokens expire after 24 hours
- Re-login to get a new token
- Gateway validates tokens before routing

### Build Failures
```bash
# Clean and rebuild
./run.sh clean
./run.sh build
```

## How to access the databases (examples)

### Using Docker + mysql CLI (recommended for quick access):
```
Connect as root:
docker exec -it nexusbank-mysql mysql -u root -p
(enter rootpass)
Or connect as nexususer to a specific DB:
docker exec -it nexusbank-mysql mysql -u nexususer -p nexusbank_customer
(enter nexuspass)
Using a GUI (MySQL Workbench / DBeaver / HeidiSQL):
New connection:
Host: 127.0.0.1 (or localhost)
Port: 3306
User: nexususer
Password: nexuspass
Default DB: nexusbank_customer or nexusbank_admin
```

### Mysql container Root Creds:
- Username: root
- Password: rootpass

### Mysql container User Creds:
- Username: nexususer
- Password: nexuspass

---

## 📊 Architecture Overview

```
┌─────────────┐     ┌─────────────┐     ┌─────────────────┐
│   React     │────▶│ API Gateway │────▶│  Eureka Server  │
│  Frontend   │     │   :8080     │     │    :8761        │
└─────────────┘     └─────────────┘     └─────────────────┘
                             │
              ┌──────────────┼──────────────┐
              ▼              ▼              ▼
        ┌──────────┐   ┌──────────┐   ┌──────────┐
        │ Customer │   │  Admin   │   │  Config  │
        │ Service  │   │ Service  │   │ Server   │
        │ :8081    │   │ :8082    │   │ :8888    │
        │ (H2 DB)  │   │ (H2 DB)  │   │          │
        └──────────┘   └──────────┘   └──────────┘
              ▲              │
              │              │ (OpenFeign)
              └──────────────┘
```

---

## 📚 Tech Stack

- **Backend**: Java 17, Spring Boot 3.2, Spring Security, JWT, Spring Data JPA
- **Frontend**: React 18, React Router, Axios, Bootstrap 5
- **Database**: SQL container on docker
- **Infrastructure**: Eureka, Config Server, API Gateway
- **Resilience**: Resilience4j Circuit Breaker
- **Communication**: OpenFeign (Admin → Customer)
- **Containerization**: Docker, Docker Compose
- **Orchestration**: Kubernetes
- **CI/CD**: GitHub Actions, Jenkins

---

## 📄 License

MIT License - NexusBank Enterprise Banking System
