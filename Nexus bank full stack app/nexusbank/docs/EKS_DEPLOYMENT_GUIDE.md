# NexusBank EKS Deployment Guide

This guide describes how to deploy the current MySQL-backed NexusBank application to an existing Amazon EKS cluster.

The application consists of:

- React frontend served by NGINX on port 80
- Spring Cloud API Gateway on port 8080
- Customer Service on port 8081
- Admin Service on port 8082
- Eureka Server on port 8761
- Config Server on port 8888
- MySQL 8 database

The backend services use MySQL through `SPRING_DATASOURCE_*` variables. Do not deploy the old H2-based assumptions from older documentation.

## 1. Recommended AWS architecture

```text
Route 53
   |
AWS Load Balancer Controller (ALB, HTTPS)
   |
   +-- /       -> frontend Service
   +-- /api    -> api-gateway Service
                    |
                    +-- customer-service
                    +-- admin-service
                    +-- Eureka Server
                    +-- Config Server

Customer Service and Admin Service -> Amazon RDS/Aurora MySQL
```

Use Kubernetes `ClusterIP` Services for internal components. Expose only the frontend and API Gateway through one ALB Ingress.

For production, use Amazon RDS for MySQL or Aurora MySQL instead of running MySQL as a single-pod Kubernetes workload. RDS provides backups, Multi-AZ availability, patching, encryption, and recovery capabilities that a standalone MySQL pod does not.

## 2. EKS prerequisites

The following should already be available:

- An EKS cluster and managed node group
- `kubectl`, AWS CLI, and Helm
- AWS credentials with EKS, ECR, IAM, and networking permissions
- AWS Load Balancer Controller installed with IRSA
- Amazon ECR repositories
- An RDS/Aurora MySQL instance, or a deliberate non-production decision to run MySQL in Kubernetes
- Route 53 DNS record
- ACM certificate for the application hostname
- EBS CSI Driver if Kubernetes persistent volumes are used

Configure access:

```powershell
aws eks update-kubeconfig `
  --region <AWS_REGION> `
  --name <EKS_CLUSTER_NAME>

kubectl get nodes
```

Ensure the RDS security group allows MySQL port 3306 from the EKS node/pod network, preferably through a narrowly scoped security-group rule rather than `0.0.0.0/0`.

## 3. Build and publish container images

Build these six images from the Dockerfiles in the repository:

```text
frontend
api-gateway
config-server
eureka-server
customer-service
admin-service
```

Create ECR repositories:

```powershell
aws ecr create-repository --repository-name nexusbank/frontend --region <AWS_REGION>
aws ecr create-repository --repository-name nexusbank/api-gateway --region <AWS_REGION>
aws ecr create-repository --repository-name nexusbank/config-server --region <AWS_REGION>
aws ecr create-repository --repository-name nexusbank/eureka-server --region <AWS_REGION>
aws ecr create-repository --repository-name nexusbank/customer-service --region <AWS_REGION>
aws ecr create-repository --repository-name nexusbank/admin-service --region <AWS_REGION>
```

Authenticate Docker:

```powershell
aws ecr get-login-password --region <AWS_REGION> |
  docker login --username AWS --password-stdin <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com
```

Tag every image with an immutable release value, such as a Git commit SHA. Do not use `latest` in EKS manifests.

Example:

```powershell
docker build -t nexusbank/customer-service:<VERSION> .\customer-service
docker tag nexusbank/customer-service:<VERSION> <ECR_REGISTRY>/nexusbank/customer-service:<VERSION>
docker push <ECR_REGISTRY>/nexusbank/customer-service:<VERSION>
```

Repeat for each service and replace `<VERSION>` with the same release identifier.

## 4. Frontend API URL

The React frontend currently defaults to `http://localhost:8080` in `frontend/src/services/api.js`. That address is valid only for local development.

Build the frontend image with the production API URL:

```text
https://bank.example.com/api
```

or, when frontend and API use the same host:

```text
/api
```

React environment variables are compiled into the static bundle. Setting `REACT_APP_API_URL` only on the Kubernetes Deployment after the image is built will not update the already-built JavaScript.

The frontend NGINX configuration proxies `/api` to `api-gateway:8080` inside the Docker network. In EKS, either:

1. Keep the frontend NGINX proxy and ensure its upstream resolves to the Kubernetes API Gateway Service, or
2. Build the frontend to call the public `/api` path and let the ALB route `/api` to the gateway.

Choose one approach and test it end to end; do not configure both paths inconsistently.

## 5. Configure secrets and database settings

Do not use the sample credentials currently present in the Compose files or Kubernetes Secret for production.

Store these values in AWS Secrets Manager:

- MySQL username
- MySQL password
- MySQL JDBC URL or RDS endpoint
- Eureka password
- JWT signing secret

Expose them to Kubernetes using External Secrets Operator or the AWS Secrets Store CSI Driver. At minimum, create a Kubernetes Secret with:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
JWT_SECRET
EUREKA_PASSWORD
```

The JDBC URLs should reference the RDS endpoint, for example:

```text
jdbc:mysql://<RDS_ENDPOINT>:3306/nexusbank_customer?createDatabaseIfNotExist=true&useSSL=true&serverTimezone=UTC
jdbc:mysql://<RDS_ENDPOINT>:3306/nexusbank_admin?createDatabaseIfNotExist=true&useSSL=true&serverTimezone=UTC
```

Create both databases and grant the application user access. The local `devops/docker/init.sql` is useful for local MySQL initialization but should be reviewed and applied through a controlled database migration process for production.

Keep non-sensitive service URLs in a ConfigMap:

```text
EUREKA_URL=http://eureka-server:8761/eureka
CONFIG_SERVER_URL=http://config-server:8888
```

Do not store `JWT_SECRET` in a ConfigMap.

## 6. Prepare Kubernetes manifests

The existing manifests are in `devops/k8s/`:

- `namespace.yml`
- `configmap.yml`
- `secret.yml`
- `eureka-deployment.yml`
- `config-server-deployment.yml`
- `gateway-deployment.yml`
- `customer-service-deployment.yml`
- `admin-service-deployment.yml`
- `frontend-deployment.yml`
- `ingress.yml`

Before applying them:

1. Replace `nexusbank/...:latest` with full ECR image URIs.
2. Change `api-gateway` and `frontend` Services from `LoadBalancer` to `ClusterIP`.
3. Add MySQL datasource environment variables to both backend Deployments.
4. Reference Kubernetes Secrets instead of literal passwords.
5. Add readiness and liveness probes to every Spring Boot workload.
6. Add CPU and memory requests and limits.
7. Add rolling-update settings and a PodDisruptionBudget for replicated services.
8. Add security context settings and run containers as non-root where supported.
9. Replace the local hostname `nexusbank.local` in the Ingress with the real DNS hostname.
10. Replace NGINX-specific Ingress annotations with AWS Load Balancer Controller annotations.

The current Kubernetes directory does not include a MySQL Deployment, Service, or PersistentVolume. That is intentional in this plan: use RDS/Aurora MySQL for the database.

## 7. Deploy in dependency order

Create the namespace:

```powershell
kubectl apply -f .\devops\k8s\namespace.yml
```

Apply the non-sensitive ConfigMap and the Secret generated from AWS Secrets Manager:

```powershell
kubectl apply -f .\devops\k8s\configmap.yml
kubectl apply -f <generated-secret-manifest>.yml
```

Deploy service discovery and configuration:

```powershell
kubectl apply -f .\devops\k8s\eureka-deployment.yml
kubectl apply -f .\devops\k8s\config-server-deployment.yml

kubectl rollout status deployment/eureka-server -n nexusbank
kubectl rollout status deployment/config-server -n nexusbank
```

Deploy the application services:

```powershell
kubectl apply -f .\devops\k8s\customer-service-deployment.yml
kubectl apply -f .\devops\k8s\admin-service-deployment.yml
kubectl apply -f .\devops\k8s\gateway-deployment.yml
kubectl apply -f .\devops\k8s\frontend-deployment.yml
```

After the Services have endpoints, apply the ALB Ingress:

```powershell
kubectl apply -f .\devops\k8s\ingress.yml
```

Kubernetes does not guarantee startup order like Docker Compose `depends_on`. Readiness probes and application retry behavior must allow Eureka, Config Server, and MySQL to become ready independently.

## 8. ALB Ingress requirements

The Ingress should configure:

- `alb.ingress.kubernetes.io/scheme: internet-facing`
- HTTPS listener
- ACM certificate ARN
- HTTP-to-HTTPS redirect
- Health-check paths
- Host-based routing for the production hostname

The intended routing is:

```text
/     -> frontend:80
/api  -> api-gateway:8080
```

Do not expose Eureka, Config Server, Customer Service, Admin Service, or MySQL publicly.

Create a Route 53 alias record pointing the application hostname to the ALB hostname returned by:

```powershell
kubectl get ingress nexusbank-ingress -n nexusbank
```

## 9. Validate the deployment

Inspect resources:

```powershell
kubectl get pods,svc,endpoints,ingress -n nexusbank
kubectl get events -n nexusbank --sort-by=.lastTimestamp
```

Check rollout status:

```powershell
kubectl rollout status deployment/eureka-server -n nexusbank
kubectl rollout status deployment/config-server -n nexusbank
kubectl rollout status deployment/customer-service -n nexusbank
kubectl rollout status deployment/admin-service -n nexusbank
kubectl rollout status deployment/api-gateway -n nexusbank
kubectl rollout status deployment/frontend -n nexusbank
```

Check logs:

```powershell
kubectl logs deployment/customer-service -n nexusbank
kubectl logs deployment/admin-service -n nexusbank
kubectl logs deployment/api-gateway -n nexusbank
```

Confirm that the backend logs show a `jdbc:mysql://` connection and that no connection attempts use `jdbc:h2:`.

Functional checks:

1. DNS resolves to the ALB.
2. HTTPS certificate validation succeeds.
3. The frontend loads.
4. Browser requests use `/api` or the configured production API hostname.
5. Login and JWT authentication work.
6. Customer and admin requests reach the correct services.
7. Data remains available after restarting an application pod.
8. Deleting one replica causes Kubernetes to recreate it.
9. The ALB health checks show healthy targets.

## 10. Operations and security

- Enable RDS encryption, automated backups, Multi-AZ, and deletion protection.
- Store secrets in AWS Secrets Manager and rotate them.
- Enable CloudWatch logging and metrics.
- Add HorizontalPodAutoscalers after collecting CPU/memory baselines.
- Add NetworkPolicies to restrict pod-to-pod traffic.
- Use AWS WAF on the public ALB.
- Restrict Kubernetes RBAC and IAM permissions.
- Scan ECR images and enable image retention policies.
- Use database migrations rather than relying on `ddl-auto: update` for production schema changes.
- Back up and test restoration of the MySQL databases.

## 11. Rollback

Deploy a new image by changing the image tag:

```powershell
kubectl -n nexusbank set image deployment/customer-service `
  customer-service=<ECR_REGISTRY>/nexusbank/customer-service:<VERSION>

kubectl rollout status deployment/customer-service -n nexusbank
```

If the release fails:

```powershell
kubectl rollout undo deployment/customer-service -n nexusbank
kubectl rollout history deployment/customer-service -n nexusbank
```

Use the same release tag across compatible application images and keep database schema changes backward compatible during rolling deployments.

## Production readiness note

The current code and Compose setup are MySQL-backed, but the existing Kubernetes manifests are only a baseline. They still contain placeholder image names, sample secrets, public `LoadBalancer` Services, and a local Ingress hostname. Complete those changes before using the manifests in a production EKS cluster.
