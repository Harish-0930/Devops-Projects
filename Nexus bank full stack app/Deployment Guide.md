# NexusBank — AWS EC2 Deployment Guide

> **Purpose:** Repeatable deployment reference for the NexusBank application using a public frontend EC2 instance, a private backend/database EC2 instance, an internet-facing Application Load Balancer (ALB), and an internal ALB.
>
> **Important:** The CIDRs and resource names below are a reusable reference design, not a claim that every value matches the live AWS account. Confirm the actual VPC, subnet, route table, security group, ALB, and target group values in AWS before applying changes. Replace placeholders such as `<INTERNET_ALB_DNS>` and `<INTERNAL_ALB_DNS>`.
## AWS Architecture Diagram

![NexusBank AWS Architecture Diagram](https://raw.githubusercontent.com/Harish-0930/Devops-Projects/Main/Nexus%20bank%20full%20stack%20app/NexusBank%20AWS%20Architecture%20Diagram.png)
---
## 1. Architecture overview

- **Public entry point:** Users access the application through the internet-facing ALB DNS name.
- **Frontend:** React application and Nginx run on a public EC2 instance. The instance can have a public IP/EIP for administration or package access, but application traffic should enter through the ALB.
- **Backend:** API Gateway and Spring Boot microservices run in Docker on a private EC2 instance.
- **Database:** MySQL runs alongside the backend on that private EC2 instance, attached to a persistent Docker volume.
- **API path:** Browser requests to `/api/*` go to frontend Nginx, which proxies them to the internal ALB. The internal ALB forwards requests to the backend EC2 target on port `8080`.
- **Application access:** The application is accessed directly through the internet-facing ALB DNS name over HTTP in this version of the deployment guide.

## 2. Reference network plan

Region: `us-east-1` (use the same region as the existing ALBs and EC2 instances).

| Component | Reference CIDR / address | Purpose |
|---|---|---|
| VPC | `10.0.0.0/16` | NexusBank network |
| Public subnet A | `10.0.1.0/24` | Internet ALB and frontend EC2 |
| Public subnet B | `10.0.2.0/24` | Internet ALB and NAT Gateway; second AZ |
| Private subnet A | `10.0.3.0/24` | Internal ALB and backend EC2 |
| Private subnet B | `10.0.4.0/24` | Internal ALB / future database or backend capacity |
| Frontend EC2 example | `10.0.1.22` | React + Nginx |
| Backend EC2 example | `10.0.3.10` | API Gateway and Spring Boot services |
| MySQL | Docker service name `mysql:3306` | Same Docker network/host as backend |

**Subnet/AZ note:** An ALB normally needs subnets in at least two Availability Zones. Choose two AZs available in your region and associate the ALBs with the appropriate subnets. Use the actual subnet IDs in the AWS console.

## 3. Create the VPC and subnets

1. Open **VPC → Your VPCs → Create VPC**.
2. Choose a VPC with CIDR `10.0.0.0/16` (or use the existing VPC if its CIDR is different).
3. Enable DNS resolution and DNS hostnames.
4. Create four subnets using the table above, placing subnet A and subnet B in different Availability Zones.
5. Enable auto-assign public IPv4 only for public subnets if that fits your design. An Elastic IP is optional for the frontend if inbound web traffic arrives through the ALB.

Do not create overlapping CIDRs with existing VPCs that need to communicate.

## 4. Internet Gateway, NAT Gateway, and route tables

### Internet Gateway (IGW)

1. Create an Internet Gateway named `nexus-igw`.
2. Attach it to the NexusBank VPC.

### Public route table

Create `nexus-public-rt` and associate it with both public subnets.

| Destination | Target |
|---|---|
| `10.0.0.0/16` | `local` (automatically created) |
| `0.0.0.0/0` | Internet Gateway `nexus-igw` |

### NAT Gateway

1. Allocate an Elastic IP.
2. Create a NAT Gateway in **Public subnet B** and associate the Elastic IP.
3. Wait until its state is **Available**.

A NAT Gateway is for **outbound** internet access from private subnets; it does not permit unsolicited inbound internet connections to private instances.

### Private route table

Create `nexus-private-rt` and associate it with private subnets A and B.

| Destination | Target |
|---|---|
| `10.0.0.0/16` | `local` |
| `0.0.0.0/0` | NAT Gateway `nexus-nat` |

For higher availability, production designs commonly use a NAT Gateway per AZ and route each private subnet to the NAT Gateway in the same AZ. The single-NAT layout above is a lower-cost lab/reference option.

## 5. Security groups

Use security-group references instead of broad CIDR access wherever possible. Security groups are stateful; return traffic for permitted connections is automatically allowed.

| Security group | Inbound rules | Outbound guidance | Attached to |
|---|---|---|---|
| `nexus-internet-alb-sg` | TCP `80` from `0.0.0.0/0`; TCP `443` from `0.0.0.0/0` | Allow traffic to frontend target on TCP `80` | Internet-facing ALB |
| `nexus-frontend-sg` | TCP `80` from `nexus-internet-alb-sg`; SSH `22` from trusted admin IP or jump-host SG only | TCP `8080` to `nexus-internal-alb-sg`; HTTPS `443` and HTTP `80` if needed for updates | Frontend EC2 |
| `nexus-internal-alb-sg` | TCP `8080` from `nexus-frontend-sg` | TCP `8080` to `nexus-backend-sg` | Internal ALB |
| `nexus-backend-sg` | TCP `8080` from `nexus-internal-alb-sg`; SSH `22` from trusted admin/jump-host SG only | Outbound as required for package repositories, AWS APIs, and application dependencies | Backend EC2 |
| Optional `nexus-jump-sg` | SSH `22` from your current trusted public IP only | Outbound SSH to private instances | Bastion/jump host |

### Database port

Because MySQL is running on the **same EC2 instance** as the backend in this deployment, it does not need to expose TCP `3306` publicly or through the EC2 security group. Keep MySQL on the Docker network and use the Compose service name, e.g. `jdbc:mysql://mysql:3306/nexusbank`.

If the database is moved to a separate host later, allow TCP `3306` only from the backend security group, not from `0.0.0.0/0`.

### SSH administration

Do not open SSH (`22`) to the whole internet. Prefer AWS Systems Manager Session Manager or a jump host. If using a public admin IP, restrict the source to `<YOUR_TRUSTED_PUBLIC_IP>/32`.

## 6. Create the public and private Ubuntu EC2 instances and deploy the application

This section creates the two EC2 hosts and starts the application **before** creating the target groups. The load balancers can be configured afterward to route to the already-running instances.

### 6.1 Launch the frontend Ubuntu instance in a public subnet

1. Open **EC2 → Instances → Launch instances**.
2. Name: `nexusbank-frontend-ec2`.
3. Select a supported **Ubuntu Server LTS** AMI.
4. Choose an instance type appropriate for the project workload.
5. Select the NexusBank VPC and **Public subnet A** (reference CIDR `10.0.1.0/24`).
6. Enable a public IPv4 address, or associate an Elastic IP after launch if a stable instance address is specifically needed. Normal web traffic should enter through the internet-facing ALB.
7. Attach `nexus-frontend-sg`.
8. Select or create a key pair and launch the instance.
9. Confirm the subnet's associated route table has `0.0.0.0/0 → Internet Gateway`.
10. Connect using SSH from your trusted IP or a jump host. Avoid opening SSH to the entire internet.

### 6.2 Launch the backend/database Ubuntu instance in a private subnet

1. Open **EC2 → Instances → Launch instances**.
2. Name: `nexusbank-backend-ec2`.
3. Select a supported **Ubuntu Server LTS** AMI.
4. Choose an instance type appropriate for the project workload.
5. Select the NexusBank VPC and **Private subnet A** (reference CIDR `10.0.3.0/24`).
6. Do **not** assign a public IPv4 address.
7. Attach `nexus-backend-sg`.
8. Launch the instance.
9. Connect through AWS Systems Manager Session Manager, a bastion/jump host, or another approved private-access method.
10. If using a jump host, allow SSH to the backend only from the jump-host security group. The private route table should route outbound internet traffic through the NAT Gateway if package installation or repository access is required.

### 6.3 Connect to the backend EC2 and install prerequisites

On the backend instance, go to the project directory. The path below is the repository/project location as provided for this deployment; adjust it to the actual clone or upload location on Ubuntu.

```bash
cd "<PATH_TO_PROJECT>/Nexus bank full stack app/nexusbank"
```

If the scripts are already in the project directory, make them executable:

```bash
chmod +x install-docker-git.sh install-java17-maven.sh run.sh backend_run.sh
```

Install Docker and Git (Git is optional if the project files have already been copied to the instance):

```bash
./install-docker-git.sh
```

Install Java 17 and Maven:

```bash
./install-java17-maven.sh
```

If Docker group permissions were updated by the installer, log out and reconnect before running Docker commands without `sudo`. Otherwise, use `sudo docker ...`.

Verify the prerequisites:

```bash
docker --version
git --version        # optional
java -version        # should show Java 17
mvn -version         # should show Maven
```

If Docker is not yet active:

```bash
sudo systemctl enable --now docker
sudo systemctl status docker --no-pager
```

### 6.4 Start the database first, then the backend services

From the project directory, start the database using the supplied `database_run.sh` script:

```bash
database_run.sh
```

Check the script's output and confirm the MySQL container is running and healthy before starting the services. If `database_run.sh` is not executable, use `database_run.sh`.

Then start the backend services:

```bash
./backend_run.sh
```

If the scripts expect a specific working directory, follow their comments or inspect them with:

```bash
sed -n '1,240p' run.sh
sed -n '1,260p' backend_run.sh
```

Do not publish MySQL port `3306` to the internet. Since the database and backend run on the same EC2 host, backend containers should connect to MySQL over the Docker network using the Compose service hostname (for example, `mysql:3306`) as defined in the application configuration.

### 6.5 Verify backend containers and logs

Run these from the backend project/Compose directory:

```bash
sudo docker ps
sudo docker ps -a
sudo docker compose ps
```

If the scripts use Compose files in a different directory, `cd` into the directory containing the relevant Compose file before running `docker compose` commands.

Inspect recent logs (replace names with the actual container names shown by `docker ps -a`):

```bash
sudo docker logs --tail 100 nexusbank-mysql
sudo docker logs --tail 100 nexusbank-api-gateway
sudo docker logs --tail 100 nexusbank-admin
sudo docker logs --tail 100 nexusbank-customer
```

If a container exited, inspect its logs before restarting it. Confirm the API Gateway is listening on port `8080` and that the admin/customer services can connect to MySQL.

### 6.6 Connect to the frontend EC2 and start the frontend

Connect to `nexusbank-frontend-ec2` using SSH from your trusted IP or jump host. Navigate to the frontend project's directory, for example:

```bash
cd "<PATH_TO_PROJECT>/Nexus bank full stack app/nexusbank"
```

Run the supplied frontend startup script:

```bash
chmod +x frontend_run.sh
./frontend_run.sh
```

If needed, inspect the script first:

```bash
sed -n '1,260p' frontend_run.sh
```

Ensure the frontend container publishes port `80` on the EC2 host (for example, `80:80`) so the internet-facing ALB can reach Nginx. The frontend security group should allow inbound TCP `80` **from `nexus-internet-alb-sg` only**.

### 6.7 Verify frontend containers and local response

On the frontend EC2:

```bash
sudo docker ps
sudo docker ps -a
sudo docker compose ps
sudo docker logs --tail 100 nexusbank-frontend
curl -I http://localhost/
```

Expected result: the frontend container is running and `curl -I http://localhost/` returns a successful HTTP response, normally `HTTP/1.1 200 OK`.

### 6.8 Verify frontend-to-backend connectivity before configuring ALBs

After the internal ALB exists, test connectivity from the frontend EC2 to the internal ALB:

```bash
curl -i http://<INTERNAL_ALB_DNS>:8080/actuator/health
```

Use `/actuator/health` only if the API Gateway actually exposes that path. Otherwise, test a known, unauthenticated health endpoint supported by the gateway/application. A `404` means the chosen path may not be routed; a `503` commonly indicates that no healthy backend targets are available or that the gateway/service is unavailable.

The Nginx `/api/` location should proxy to the internal ALB DNS name on port `8080`; do not point browser-side JavaScript at `localhost:8080`, because browser `localhost` refers to the end user's own machine.

## 7. Create the target groups

### Frontend target group

- Name: `nexus-frontend-tg`
- Target type: **Instances**
- Protocol: `HTTP`
- Port: `80`
- VPC: NexusBank VPC
- Health check protocol: `HTTP`
- Health check path: `/`
- Success codes: `200` (or a narrowly justified range if the app redirects)
- Register the frontend EC2 instance on port `80`

The target should return a successful HTTP response at `/`. Verify from the instance with `curl -I http://localhost/`.

### Backend target group

- Name: `nexus-backend-tg`
- Target type: **Instances**
- Protocol: `HTTP`
- Port: `8080`
- VPC: NexusBank VPC
- Health check protocol: `HTTP`
- Health check path: `/actuator/health` **only if the API Gateway responds successfully on this path**; otherwise use a real unauthenticated health endpoint supported by your gateway/application.
- Success codes: `200`
- Register the private backend EC2 instance on port `8080`

**Health-check caution:** If `/actuator/health` exists only on an individual microservice and is not routed by the API Gateway, it may return `404` at the gateway. Test the endpoint directly against the backend/gateway before setting it as the target-group health check. Do not expose sensitive actuator endpoints publicly.

Useful checks from the frontend EC2:

```bash
curl -i http://<INTERNAL_ALB_DNS>:8080/actuator/health
curl -i http://<INTERNAL_ALB_DNS>:8080/
```

A `200` response is ideal for the configured health-check endpoint. A `401`, `403`, `404`, or `503` indicates that you should validate the chosen path and gateway routing before relying on it for health checks.

## 8. Create the internal ALB

1. Open **EC2 → Load Balancers → Create load balancer → Application Load Balancer**.
2. Name: `nexus-internal-alb`.
3. Scheme: **Internal**.
4. IP address type: IPv4.
5. Select the NexusBank VPC and private subnets in at least two AZs.
6. Attach `nexus-internal-alb-sg`.
7. Create or select listener `HTTP :8080`.
8. Set the default action to forward to `nexus-backend-tg`.
9. Confirm the backend target group uses port `8080` and the private backend instance is registered.
10. Wait for target health to become **Healthy**.

Only the frontend SG should be able to access the internal ALB on TCP `8080`.

## 9. Create the internet-facing ALB

1. Open **EC2 → Load Balancers → Create load balancer → Application Load Balancer**.
2. Name: `nexus-internet-alb`.
3. Scheme: **Internet-facing**.
4. Select the NexusBank VPC and public subnets in at least two AZs.
5. Attach `nexus-internet-alb-sg`.
6. Create listener `HTTP :80` and initially forward to `nexus-frontend-tg`, or configure it to redirect to HTTPS after HTTPS is ready.
7. Confirm `nexus-frontend-tg` is configured for HTTP port `80`, path `/`, success code `200`.
8. Wait until the frontend target is **Healthy**.

## 10. Access the application using the ALB DNS name

This deployment uses the **internet-facing ALB DNS name** directly. Custom-domain setup, DNS validation, and HTTPS certificates are intentionally out of scope for this version of the runbook.

1. Open **EC2 → Load Balancers** and select `nexus-internet-alb`.
2. Copy its **DNS name**.
3. Open the DNS name in a browser using `http://<INTERNET_ALB_DNS>` while the ALB HTTP listener is configured.
4. Confirm the internet ALB forwards to `nexus-frontend-tg` and the frontend target is **Healthy**.

Example format (use the actual value shown in AWS):

`http://nexus-internet-alb-xxxxxxxx.us-east-1.elb.amazonaws.com`

## 11. Frontend Nginx reverse proxy

The frontend Nginx server should serve the React build and forward `/api/` to the **internal ALB**. Keep the `proxy_pass` without a trailing slash so the original URI path is preserved.

```nginx
server {
    listen 80;
    server_name _;

    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://<INTERNAL_ALB_DNS>:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Replace `<INTERNAL_ALB_DNS>` with the actual internal ALB DNS name. Rebuild/recreate the frontend container after changing a configuration copied into the image.

```bash
cd "\project\Nexus bank full stack app\nexusbank\devops\docker"
sudo docker compose build frontend
sudo docker compose up -d --force-recreate frontend
sudo docker logs --tail 100 <FRONTEND_CONTAINER_NAME>
```

If Nginx resolves the ALB hostname only at startup, reload/recreate the container if the ALB DNS resolution changes. Verify that the frontend container can reach the internal ALB on port `8080`.

For a same-origin frontend, prefer a relative API base URL (for example, `/api`) instead of hard-coding `http://localhost:8080` into the React build. A browser's `localhost` refers to the end user's machine, not the AWS server.

## 12. Docker Compose and database initialization

- Backend services and MySQL share a Docker network on the private EC2 host.
- Configure the JDBC URL using the Compose service name, e.g. `jdbc:mysql://mysql:3306/nexusbank`.
- Keep MySQL port `3306` unpublished unless there is a specific, secured administrative requirement.
- Mount persistent storage for MySQL if data must survive container replacement.
- Files under `/docker-entrypoint-initdb.d/` are executed by the official MySQL image only when its data directory is first initialized. Recreating a container while reusing an initialized volume does not rerun `init.sql`.
- For a clean lab reset, deleting the relevant Compose volume causes data loss. Confirm that this is intended before running `docker compose down -v`.

## 13. ALB DNS and traffic flow

1. User requests `http://<INTERNET_ALB_DNS>`.
2. The ALB DNS name resolves to the internet-facing ALB.
3. ALB listener `443` forwards to `nexus-frontend-tg` on frontend EC2 port `80`.
4. Nginx serves the React application.
5. The browser calls `/api/...` on the same domain.
6. Nginx proxies `/api/...` to `<INTERNAL_ALB_DNS>:8080`.
7. Internal ALB forwards to the private backend EC2 on port `8080` (API Gateway).
8. API Gateway routes to admin/customer services; services connect to MySQL through the Docker network.

## 14. Validation checklist

### Network
- [ ] IGW attached to the VPC.
- [ ] Public route table has `0.0.0.0/0 → IGW`.
- [ ] Private route table has `0.0.0.0/0 → NAT Gateway` if private instances need outbound internet access.
- [ ] Public and private subnet associations are correct.
- [ ] ALBs use subnets in at least two AZs.

### Security
- [ ] Internet ALB inbound allows `80` and `443` from the internet.
- [ ] Frontend inbound `80` allows only the internet ALB SG.
- [ ] Internal ALB inbound `8080` allows only the frontend SG.
- [ ] Backend inbound `8080` allows only the internal ALB SG.
- [ ] MySQL `3306` is not exposed publicly.
- [ ] SSH is restricted to a trusted source or replaced with SSM.

### Load balancers and app
- [ ] Frontend target group reports **Healthy** for `/`.
- [ ] Backend target group reports **Healthy** for the verified health endpoint on port `8080`.
- [ ] Internal ALB is reachable from the frontend instance/container.
- [ ] Nginx `/api/` proxy preserves the `/api/...` path.
- [ ] Admin and customer containers remain running and can connect to MySQL.
- [ ] `http://<INTERNET_ALB_DNS>` loads the frontend.
- [ ] API requests return expected application responses rather than `502`/`503`.
### Default Credentials to Application and Database
 -  Eureka Server --> admin:admin123
 - Customer login --> johndoe:password123
- Admin login --> admin:admin123
- Database Root Cred's: root:rootpass
- User Creds --> nexususer:nexuspass
### How to access the Databse
```
Default DB: nexusbank_customer or nexusbank_admin

Using Docker + mysql CLI (recommended for quick access):
Connect as root:
docker exec -it nexusbank-mysql mysql -u root -p
(enter rootpass)
Or connect as nexususer to a specific DB:
docker exec -it nexusbank-mysql mysql -u nexususer -p nexusbank_customer
(enter nexuspass)
```
### Useful commands

```bash
# On frontend EC2
curl -I http://localhost/
curl -i http://<INTERNAL_ALB_DNS>:8080/actuator/health

# On backend EC2
sudo docker ps
sudo docker logs --tail 100 <API_GATEWAY_CONTAINER>
sudo docker logs --tail 100 <ADMIN_SERVICE_CONTAINER>
sudo docker logs --tail 100 <CUSTOMER_SERVICE_CONTAINER>
sudo docker logs --tail 100 <MYSQL_CONTAINER>

# Inspect Compose configuration (run from Compose directory)
sudo docker compose config
```

## 15. Common failures

| Symptom | Likely checks |
|---|---|
| Internet ALB returns `503` | Frontend target health, target port `80`, frontend SG inbound from ALB SG, Nginx/container status |
| API returns `503` | Internal ALB target health, API Gateway running, backend SG allows `8080` from internal ALB SG, service discovery/config |
| API returns `502` | Nginx `proxy_pass`, internal ALB DNS/port, connectivity from frontend container to internal ALB |
| MySQL says host is not allowed | Verify `nexususer` host permissions (`'%'` or appropriately scoped Docker subnet), password, grants, and initialization volume state |
| Root domain doesn't resolve to ALB | Use a DNS provider/feature that supports apex alias/flattening; do not pin ALB IPs in an A record |
| Target health check fails | Confirm path, protocol, port, expected status code, listener rules, and SG path |

## 16. Resource values to record after deployment

Fill this in with actual AWS values to make the document a true environment-specific runbook.

| Resource | Actual value |
|---|---|
| AWS Region | `us-east-1` |
| VPC ID / CIDR | `<VPC_ID>` / `<VPC_CIDR>` |
| Public subnet A/B IDs | `<SUBNET_ID_A>` / `<SUBNET_ID_B>` |
| Private subnet A/B IDs | `<SUBNET_ID_A>` / `<SUBNET_ID_B>` |
| Internet ALB DNS | `<INTERNET_ALB_DNS>` |
| Internal ALB DNS | `<INTERNAL_ALB_DNS>` |
| Frontend target group ARN/name | `<FRONTEND_TG>` |
| Backend target group ARN/name | `<BACKEND_TG>` |
| Frontend/backend EC2 IDs | `<INSTANCE_ID>` / `<INSTANCE_ID>` |

---
**Operational reminder:** This is a reusable reference runbook. Before repeating a deployment, compare the examples with the live AWS resources and update all placeholders. Never delete volumes or recreate production resources without confirming the data-loss and availability impact.
