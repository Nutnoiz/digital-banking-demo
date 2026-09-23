# Digital Banking Demo

A full-stack digital banking demo application built with **Angular** and **Spring Boot**.

This project demonstrates a complete end-to-end application and deployment flow:

```text
Angular Frontend
       |
       | HTTP / REST API
       v
Spring Boot Backend
       |
       v
H2 Database
```

The application supports authentication, account management, transaction history, and money transfers using real backend APIs and persistent database data.

The project also demonstrates containerization, Kubernetes deployment, and a production-oriented CI/CD pipeline:

```text
Source Code
    |
    v
GitHub
    |
    v
GitHub Actions
    |
    +--> Backend Test
    |
    +--> Frontend Build
    |
    +--> Docker Image Build
    |
    +--> Push Images to GHCR
    |
    v
Kubernetes Development
    |
    +--> Deployment
    +--> Rollout Verification
    +--> Health Check
    |
    v
Production Approval
    |
    v
Kubernetes Production
    |
    +--> Deployment
    +--> Rollout Verification
    +--> Health Check
    +--> Automatic Rollback
```

---

## Features

### Authentication

* Username/password login
* JWT-based authentication
* JWT token stored in browser session storage
* Angular HTTP interceptor automatically attaches JWT
* Protected routes using Angular route guard
* Automatic logout when a protected API returns HTTP 401
* Login authentication errors are displayed to the user
* Demo credentials are pre-filled on the login page

### Dashboard

* Welcome message with authenticated username
* Total account balance
* Number of accounts
* Account summary
* Recent transactions
* Real API data

### Accounts

* View all accounts
* Account number
* Account type
* Customer name
* Current balance
* Currency
* Account status
* View account transaction history

### Transactions

* View transactions for each account
* Transaction type
* Amount
* Balance before transaction
* Balance after transaction
* Reference number
* Description
* Transaction timestamp

### Money Transfer

* Select source account
* Select destination account
* Validate source and destination accounts
* Validate transfer amount
* Validate decimal places
* Validate maximum amount
* Optional transfer description
* Execute transfer through backend API
* Update source and destination balances
* Create transfer-out transaction
* Create transfer-in transaction
* Display transfer reference number

### Error Handling

Centralized frontend HTTP error handling for:

* HTTP 400
* HTTP 401
* HTTP 403
* HTTP 404
* HTTP 409
* HTTP 422
* HTTP 500
* HTTP 502
* HTTP 503
* HTTP 504
* Network errors

---

## Demo Account

The application provides a demo account so that GitHub visitors can run and test the application immediately.

```text
Username: demo
Password: Demo123!
```

The login page is pre-filled with the demo credentials.

> This is a demo application. Do not use real banking credentials or real financial data.

---

## Technology Stack

### Frontend

* Angular
* TypeScript
* Angular Router
* Angular HttpClient
* Angular Forms
* RxJS
* HTML
* CSS
* Nginx

### Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* Jakarta Bean Validation
* REST API
* Spring Boot Actuator

### Database

* H2 Database
* Flyway Database Migration

### Containerization

* Docker
* Docker Compose
* Docker Images
* Nginx reverse proxy

### Kubernetes

* Kubernetes
* Docker Desktop Kubernetes
* Kubernetes Deployments
* Kubernetes Services
* Kubernetes ConfigMaps
* Kubernetes Secrets
* Kubernetes PersistentVolumeClaims
* Kubernetes Probes
* Kubernetes Ingress
* Kustomize
* Development and Production overlays

### CI/CD

* GitHub Actions
* Self-hosted GitHub Actions Runner
* GitHub Container Registry (GHCR)
* Immutable Git SHA image tags
* Development deployment
* Production deployment
* Production approval gate
* Deployment verification
* Automatic rollback

### Development Tools

* Node.js
* npm
* Angular CLI
* Maven
* Docker CLI
* kubectl
* Kustomize
* Git

---

## Project Structure

```text
digital-banking-demo/
│
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── frontend/
│   └── digital-banking-web/
│       │
│       ├── src/
│       │   └── app/
│       │       │
│       │       ├── core/
│       │       │   ├── auth/
│       │       │   ├── guards/
│       │       │   ├── http/
│       │       │   └── interceptors/
│       │       │
│       │       ├── features/
│       │       │   ├── login/
│       │       │   ├── dashboard/
│       │       │   ├── accounts/
│       │       │   ├── transactions/
│       │       │   └── transfer/
│       │       │
│       │       ├── layout/
│       │       │
│       │       ├── app.config.ts
│       │       ├── app.routes.ts
│       │       └── app.html
│       │
│       ├── Dockerfile
│       ├── nginx.conf
│       └── proxy.conf.json
│
├── backend/
│   └── digital-banking-api/
│       │
│       ├── src/
│       │   └── main/
│       │       ├── java/
│       │       └── resources/
│       │           └── db/
│       │               └── migration/
│       │
│       ├── pom.xml
│       └── data/
│           └── banking
│
├── k8s/
│   ├── base/
│   │   ├── backend-configmap.yml
│   │   ├── backend-deployment.yml
│   │   ├── backend-service.yml
│   │   ├── banking-data-pvc.yml
│   │   ├── frontend-deployment.yml
│   │   ├── frontend-service.yml
│   │   ├── ingress.yml
│   │   └── kustomization.yml
│   │
│   └── overlays/
│       ├── dev/
│       │   └── kustomization.yml
│       │
│       └── prod/
│           └── kustomization.yml
│
├── docker-compose.yml
│
└── README.md
```

---

## Application Architecture

The application consists of an Angular frontend, Spring Boot backend, and H2 database.

```text
                 ┌─────────────────────┐
                 │    Angular Web      │
                 │                     │
                 │  Login              │
                 │  Dashboard          │
                 │  Accounts           │
                 │  Transactions       │
                 │  Transfer           │
                 └──────────┬──────────┘
                            │
                       HTTP / REST
                            │
                            ▼
                 ┌─────────────────────┐
                 │    Spring Boot      │
                 │                     │
                 │  Security           │
                 │  Controllers        │
                 │  Services           │
                 │  JPA / Hibernate    │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │    H2 Database      │
                 │                     │
                 │  Customers          │
                 │  Accounts           │
                 │  Transactions       │
                 └─────────────────────┘
```

---

## Container Architecture

The application can also be packaged as Docker containers.

```text
                 ┌───────────────────────┐
                 │   Angular Frontend     │
                 │   Docker + Nginx       │
                 └───────────┬───────────┘
                             │
                             │ /api/
                             ▼
                 ┌───────────────────────┐
                 │   Spring Boot API     │
                 │   Docker Container    │
                 └───────────┬───────────┘
                             │
                             ▼
                 ┌───────────────────────┐
                 │     Banking Data      │
                 │       Volume          │
                 └───────────────────────┘
```

The frontend Nginx container provides the Angular application and reverse-proxies `/api/` requests to the backend service.

---

## Kubernetes Architecture

The application is deployed to Kubernetes using Kustomize.

Two Kubernetes environments are provided:

```text
Development
digital-banking-dev

Production
digital-banking-prod
```

Each environment is built from the shared Kubernetes base configuration.

```text
k8s/
│
├── base/
│   │
│   ├── Backend Deployment
│   ├── Backend Service
│   ├── Backend ConfigMap
│   ├── Banking Data PVC
│   ├── Frontend Deployment
│   ├── Frontend Service
│   ├── Ingress
│   └── Kustomization
│
└── overlays/
    │
    ├── dev/
    │   └── Development configuration
    │
    └── prod/
        └── Production configuration
```

### Kubernetes Components

#### Backend

The Spring Boot backend runs as a Kubernetes Deployment.

```text
backend-deployment
    |
    +-- 1 replica
    |
    +-- Readiness Probe
    |
    +-- Liveness Probe
    |
    +-- ConfigMap
    |
    +-- Kubernetes Secret
    |
    +-- PersistentVolumeClaim
```

The backend uses one replica because the demo database is an H2 file database backed by a `ReadWriteOnce` PersistentVolumeClaim.

The deployment strategy is `Recreate` to avoid multiple backend pods simultaneously accessing the same H2 file database.

#### Frontend

The Angular application is served by Nginx.

```text
frontend-deployment
    |
    +-- 2 replicas
    |
    +-- Nginx
    |
    +-- /api/ reverse proxy
```

The frontend is stateless and therefore runs with two replicas.

#### Services

The backend is exposed internally through a Kubernetes ClusterIP service:

```text
backend:8080
```

The frontend is exposed through a NodePort service.

Development:

```text
NodePort: 30081
```

Production:

```text
NodePort: 30082
```

#### Persistent Storage

Production uses:

```text
PersistentVolumeClaim
Name: banking-data
Size: 1Gi
Access Mode: ReadWriteOnce
Storage Class: standard
```

Development uses a separate PVC:

```text
dev-banking-data
```

This keeps development and production database storage separated.

---

## Kubernetes Health Checks

The backend exposes Spring Boot Actuator health endpoints.

```text
/actuator/health
/actuator/health/readiness
/actuator/health/liveness
```

Kubernetes uses readiness and liveness probes to monitor the backend container.

The CI/CD pipeline also performs application-level health verification after deployment.

Backend:

```text
/actuator/health
```

Frontend-to-backend:

```text
/api/health
```

A successful production deployment requires both health checks to report `UP`.

---

## Kubernetes Security

Sensitive backend configuration is not stored in Git.

The JWT secret is stored in a Kubernetes Secret and injected into the backend using `secretKeyRef`.

The repository intentionally does not commit:

```text
k8s/app/backend-secret.yml
```

or other local Kubernetes secret manifests.

The backend receives configuration through:

```text
ConfigMap
    |
    +-- DEMO_USERNAME
    +-- DEMO_PASSWORD

Secret
    |
    +-- JWT_SECRET
```

The Kubernetes secret configuration is managed outside the Git repository.

---

## Kustomize

Kustomize is used to manage Kubernetes environments without duplicating the entire manifest set.

The base configuration contains common resources.

Development and Production overlays modify environment-specific settings such as:

* Namespace
* Container image tag
* Frontend NodePort
* Ingress host
* PersistentVolumeClaim name

Example:

```text
k8s/base
      |
      +-------------------+
      |                   |
      v                   v
k8s/overlays/dev    k8s/overlays/prod
      |                   |
      v                   v
digital-banking-dev  digital-banking-prod
```

---

## CI/CD Pipeline

The project includes a GitHub Actions CI/CD pipeline.

The workflow is defined in:

```text
.github/workflows/ci.yml
```

The pipeline is triggered by:

```text
Push to main
Pull Request to main
```

Pull requests run CI validation but do not deploy to Kubernetes.

A push to `main` runs the full deployment pipeline.

---

## CI Pipeline

The CI stage validates both frontend and backend.

```text
Git Push / Pull Request
          |
          +--------------------+
          |                    |
          v                    v
     Backend CI           Frontend CI
          |                    |
          |                    +--> npm ci
          |                    |
          |                    +--> npm run build
          |
          +--> Java 21
          |
          +--> ./mvnw clean verify
```

The backend is compiled and tested using Maven.

The frontend is installed and built using npm and Angular.

---

## Docker Image Build

When code is pushed to `main`, successful CI builds Docker images for both applications.

```text
Backend
    |
    v
Docker Build
    |
    v
GHCR

Frontend
    |
    v
Docker Build
    |
    v
GHCR
```

Images are tagged using the Git commit SHA.

Example:

```text
ghcr.io/nutnoiz/digital-banking-api:<git-sha>

ghcr.io/nutnoiz/digital-banking-web:<git-sha>
```

This provides immutable image references for Kubernetes deployments.

The workflow also maintains a `latest` image tag for convenience.

---

## GitHub Container Registry

Docker images are published to GitHub Container Registry (GHCR).

Backend image:

```text
ghcr.io/nutnoiz/digital-banking-api
```

Frontend image:

```text
ghcr.io/nutnoiz/digital-banking-web
```

Kubernetes deployments use the Git commit SHA rather than relying on `latest`.

This makes it possible to identify exactly which source commit is running in each environment.

---

## Continuous Deployment

After successful CI and image publication, the deployment pipeline proceeds to Kubernetes Development.

```text
CI PASS
   |
   v
Docker Images
   |
   v
GHCR
   |
   v
Deploy Development
   |
   v
Verify Development
   |
   v
Production Approval
   |
   v
Deploy Production
   |
   v
Verify Production
```

---

## Development Environment

The Development Kubernetes namespace is:

```text
digital-banking-dev
```

The frontend is exposed through:

```text
NodePort: 30081
```

The development environment uses its own persistent storage:

```text
dev-banking-data
```

The CI/CD pipeline automatically deploys successful pushes to `main` into Development.

There is no manual approval required for Development.

---

## Production Environment

The Production Kubernetes namespace is:

```text
digital-banking-prod
```

The frontend is exposed through:

```text
NodePort: 30082
```

Production uses:

```text
banking-data
```

The Production deployment is protected by a GitHub Environment approval gate.

The GitHub Environment is:

```text
production
```

A required reviewer must approve the deployment before the Production deployment job can continue.

This provides a controlled promotion process:

```text
Development
    |
    | verification PASS
    v
Production Approval
    |
    | approved
    v
Production Deployment
```

---

## CI/CD Deployment Verification

The deployment pipeline performs multiple verification steps.

### Kubernetes Context

The deployment runner verifies that Kubernetes is available and that the expected context is active.

```text
docker-desktop
```

### Kustomize Validation

The pipeline renders the Kustomize overlays before deployment.

Development:

```text
k8s/overlays/dev
```

Production:

```text
k8s/overlays/prod
```

### Image Verification

The pipeline verifies that the Kubernetes Deployment references the exact Git commit SHA.

Example:

```text
ghcr.io/nutnoiz/digital-banking-api:<github-sha>

ghcr.io/nutnoiz/digital-banking-web:<github-sha>
```

### Rollout Verification

The pipeline waits for Kubernetes deployments to successfully roll out.

```text
backend-deployment
frontend-deployment
```

### Pod Verification

The pipeline verifies that expected pods are running and ready.

### Service Verification

The pipeline verifies the expected Kubernetes Services.

### Application Health Verification

Backend:

```text
/actuator/health
```

Frontend-to-backend:

```text
/api/health
```

The deployment is considered successful only after these checks pass.

---

## Automatic Rollback

The CI/CD pipeline includes automatic rollback protection.

Before deployment, the current Kubernetes image references are captured.

If a deployment starts successfully but a later rollout or verification step fails, the pipeline can restore the previous image versions.

```text
Previous Version
      |
      v
New Version
      |
      +--> Deployment
      |
      +--> Rollout
      |
      +--> Image Verification
      |
      +--> Health Check
      |
      +--> PASS
      |
      └--> FAIL
             |
             v
        Automatic Rollback
             |
             v
        Previous Version
```

Rollback is performed independently for Development and Production.

The rollback process verifies the restored deployment and application health.

A manual rollback has also been tested using Kubernetes rollout history.

---

## Deployment History

Kubernetes deployments are annotated with the Git commit SHA as the deployment change cause.

This allows deployment revisions to be inspected using Kubernetes rollout history.

Example:

```powershell
kubectl rollout history deployment/backend-deployment `
  -n digital-banking-prod
```

Rollback can be performed when necessary:

```powershell
kubectl rollout undo deployment/backend-deployment `
  -n digital-banking-prod
```

---

## Self-Hosted GitHub Actions Runner

Kubernetes deployment jobs run on a self-hosted GitHub Actions runner.

The runner is connected to the local Docker Desktop Kubernetes cluster.

The deployment architecture is:

```text
GitHub Actions
      |
      v
Self-Hosted Runner
      |
      v
kubectl
      |
      v
Docker Desktop Kubernetes
```

The runner uses a dedicated Kubernetes configuration for deployment access.

This allows GitHub Actions to execute Kubernetes deployment commands against the development and production namespaces.

---

## Local Development

### Running the Backend

Open PowerShell:

```powershell
cd D:\digital-banking-demo\backend\digital-banking-api
```

Run:

```powershell
.\mvnw.cmd spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

---

## Running the Frontend

Open another PowerShell:

```powershell
cd D:\digital-banking-demo\frontend\digital-banking-web
```

Install dependencies if needed:

```powershell
npm install
```

Run Angular:

```powershell
ng serve --proxy-config proxy.conf.json
```

The frontend runs on:

```text
http://localhost:4200
```

Open:

```text
http://localhost:4200/login
```

---

## Frontend Proxy

Development API requests use Angular's proxy configuration.

```text
Angular
http://localhost:4200
        |
        | /api
        v
Spring Boot
http://localhost:8080
```

Example API requests:

```text
/api/auth/login

/api/accounts

/api/accounts/1/transactions

/api/transfers
```

The browser communicates with the Angular development server while the Angular proxy forwards `/api` requests to Spring Boot.

---

## Running with Docker

The project can also be run using Docker.

The containerized architecture consists of:

```text
Frontend Container
      |
      | Nginx
      |
      | /api/
      v
Backend Container
      |
      v
Banking Data
```

Docker Compose can be used for local container orchestration.

Example:

```powershell
cd D:\digital-banking-demo

docker compose up -d
```

Check containers:

```powershell
docker compose ps
```

Stop containers:

```powershell
docker compose down
```

---

## Database

The application uses an H2 file database.

```text
jdbc:h2:file:./data/banking
```

The database is persisted under the backend project during local development.

Flyway manages database migrations.

Migration files are located under:

```text
src/main/resources/db/migration/
```

Example:

```text
V1__...
V2__...
V3__...
```

Spring Boot validates the migration state during startup.

In Kubernetes, the H2 database file is stored on a Kubernetes PersistentVolumeClaim.

---

## H2 Console

During local development, the H2 console is available at:

```text
http://localhost:8080/h2-console
```

Database URL:

```text
jdbc:h2:file:./data/banking
```

Use the username and password configured by the backend application.

---

## Authentication Flow

```text
User

 |

 | username + password

 v

Angular Login

 |

 | POST /api/auth/login

 v

Spring Security

 |

 | authentication

 v

JWT Access Token

 |

 v

Angular Session Storage

 |

 v

HTTP Interceptor

 |

 | Authorization: Bearer <JWT>

 v

Protected API
```

Protected Angular routes use an authentication guard.

Protected HTTP requests automatically receive the JWT through the Angular HTTP interceptor.

### Invalid Session

If a protected API returns HTTP 401:

```text
HTTP 401
   |
   v
Auth Interceptor
   |
   +--> Clear session
   |
   +--> Redirect to /login
```

A 401 returned from the login endpoint is handled differently:

```text
Login API
   |
   v
401 Unauthorized
   |
   v
Login Component
   |
   v
Display:

"Invalid username or password"
```

---

## API Endpoints

### Authentication

```text
POST /api/auth/login
```

Example request:

```json
{
  "username": "demo",
  "password": "Demo123!"
}
```

---

### Accounts

```text
GET /api/accounts
```

Get all accounts.

```text
GET /api/accounts/{id}
```

Get a specific account.

---

### Transactions

```text
GET /api/accounts/{id}/transactions
```

Get transactions for an account.

---

### Transfers

```text
POST /api/transfers
```

Example request:

```json
{
  "sourceAccountId": 1,
  "destinationAccountId": 2,
  "amount": 5000,
  "description": "Demo transfer"
}
```

The backend performs the transfer and creates the corresponding transaction records.

---

### Health

Backend health:

```text
GET /actuator/health
```

Application health through the frontend:

```text
GET /api/health
```

---

## Backend Transfer Flow

```text
POST /api/transfers
        |
        v
TransferController
        |
        v
TransferService
        |
        +----------------------+
        |                      |
        v                      v
Source Account         Destination Account
        |                      |
        v                      v
Decrease Balance       Increase Balance
        |                      |
        +----------+-----------+
                   |
                   v
          Create Transactions
                   |
                   v
          TransferResponse
```

The transfer operation is performed by the backend so that account balances and transaction records remain consistent.

---

## Example User Flow

### 1. Login

```text
Demo Account
      |
      v
Login
      |
      v
JWT Authentication
```

### 2. Dashboard

```text
Dashboard

   |
   +-- Total Balance

   |
   +-- Accounts

   |
   +-- Recent Transactions
```

### 3. Accounts

```text
Accounts

   |
   +-- Account 1
   +-- Account 2
   +-- Account 3
   +-- Account 4
```

### 4. Transactions

```text
Account
   |
   v
View Transactions
   |
   v
Transaction History
```

### 5. Transfer

```text
Transfer

   |
   +-- Source Account

   |
   +-- Destination Account

   |
   +-- Amount

   |
   +-- Description

   |
   v
Submit
   |
   v
Backend Transaction
   |
   v
Updated Balances
   |
   v
Transaction History
```

---

## Validation

### Frontend Validation

The Angular frontend validates:

* Required source account
* Required destination account
* Source and destination must be different
* Transfer amount must be greater than zero
* Maximum transfer amount
* Maximum 2 decimal places
* Description length

### Backend Validation

The Spring Boot backend also validates incoming requests using Jakarta Bean Validation.

Frontend validation improves the user experience, while backend validation remains the authoritative validation boundary.

---

## Error Handling

The application uses centralized HTTP error handling on the frontend.

Example:

```text
Backend

   |

   | HTTP 401

   v

Angular HTTP Interceptor

   |

   +--> Protected API
   |       |
   |       +--> Logout
   |       +--> Redirect to Login
   |
   +--> Login API
           |
           +--> Keep login page
           +--> Display error message
```

This prevents login failures from incorrectly logging the user out or redirecting them away from the login page.

---

## Security Notes

This repository is a learning and portfolio project.

The demo credentials are intentionally exposed so that GitHub visitors can test the application.

This project should **NOT** be deployed as-is for real banking or production financial services.

A production banking application would require additional security controls, including:

* HTTPS
* Secure secret management
* Strong password hashing
* Token lifecycle management
* Refresh token strategy
* CSRF considerations
* Rate limiting
* Audit logging
* Role-based authorization
* Database encryption
* Secure session management
* Monitoring and alerting
* Production database
* Automated security testing
* CI/CD security
* Infrastructure security
* Container image scanning
* Kubernetes security hardening
* Network policies
* Secret rotation
* Backup and disaster recovery

The current Kubernetes environment is designed for demonstration and learning rather than real financial production workloads.

---

## Project Goals

This project demonstrates practical full-stack, containerization, Kubernetes, and CI/CD skills including:

### Application Development

* Angular application architecture
* Spring Boot REST API development
* JWT authentication
* HTTP interceptor design
* Route guards
* REST API integration
* Database persistence
* Transaction processing
* Input validation
* Error handling
* Frontend state management
* End-to-end application flow

### Containerization

* Docker image creation
* Multi-stage frontend Docker build
* Nginx container
* Docker Compose
* Container networking
* Persistent application data

### Kubernetes

* Kubernetes Deployments
* Kubernetes Services
* ConfigMaps
* Secrets
* PersistentVolumeClaims
* Readiness probes
* Liveness probes
* NodePort
* ClusterIP
* Ingress
* Kustomize
* Environment overlays
* Development namespace
* Production namespace
* Kubernetes rollout history
* Kubernetes rollback

### CI/CD

* GitHub Actions
* Self-hosted GitHub Actions runner
* Automated backend validation
* Automated frontend build
* Docker image build
* GitHub Container Registry
* Immutable Git SHA image tags
* Automated Development deployment
* Production approval gate
* Automated Production deployment
* Deployment verification
* Application health checks
* Automatic rollback

---

## Deployment Flow

The complete delivery pipeline is:

```text
Developer
    |
    | git push
    v
GitHub
    |
    v
GitHub Actions
    |
    +-------------------------+
    |                         |
    v                         v
Backend CI              Frontend CI
    |                         |
    +------------+------------+
                 |
                 v
          Docker Build
                 |
                 v
              GHCR
                 |
                 v
       Development Deploy
                 |
                 v
        Kubernetes Dev
                 |
                 v
      Rollout + Health Check
                 |
              PASS
                 |
                 v
        Production Approval
                 |
              APPROVE
                 |
                 v
       Production Deploy
                 |
                 v
       Kubernetes Production
                 |
                 v
      Rollout + Health Check
                 |
                 +------ PASS ------> Production
                 |
                 +------ FAIL ------> Rollback
```

---

## Current Kubernetes Deployment Model

The current environment uses Docker Desktop Kubernetes for local Kubernetes learning and CI/CD demonstration.

```text
GitHub
   |
   v
GitHub Actions
   |
   v
Self-Hosted Runner
   |
   v
Docker Desktop Kubernetes
   |
   +-----------------------------+
   |                             |
   v                             v
digital-banking-dev       digital-banking-prod
   |                             |
   |                             |
   v                             v
Frontend 2 replicas       Frontend 2 replicas
Backend 1 replica         Backend 1 replica
PVC                       PVC
NodePort 30081            NodePort 30082
```

---

## Current Production Verification

The Production environment has been verified using Kubernetes runtime checks.

The verification includes:

```text
Kubernetes Context
        PASS

Backend Deployment
        PASS

Frontend Deployment
        PASS

Backend Image
        PASS

Frontend Image
        PASS

Backend Rollout
        PASS

Frontend Rollout
        PASS

Backend Pods
        PASS

Frontend Pods
        PASS

Backend Service
        PASS

Frontend NodePort
        PASS

Backend Health
        PASS

Frontend -> Backend Health
        PASS

Production PVC
        PASS
```

Production images are deployed using Git commit SHA tags.

Example:

```text
ghcr.io/nutnoiz/digital-banking-api:<git-sha>

ghcr.io/nutnoiz/digital-banking-web:<git-sha>
```

This allows the running Production version to be traced back to the source commit that produced the Docker images.

---

## Future Improvements

Possible future improvements include:

### Application

* Automated frontend tests
* Automated backend tests
* PostgreSQL or SQL Server
* Role-based authorization
* OpenAPI / Swagger
* Production configuration profiles
* Centralized logging
* Application monitoring

### Docker

* Image vulnerability scanning
* Image signing
* Smaller production images
* Container security hardening

### Kubernetes

* Production-grade ingress
* TLS / HTTPS
* Network policies
* Resource requests and limits
* Horizontal Pod Autoscaling
* ConfigMap and Secret management improvements
* External secret management
* Production database
* Database backup and restore
* Persistent storage backup
* Disaster recovery

### CI/CD

* Automated security scanning
* Dependency vulnerability scanning
* Container image scanning
* Deployment notifications
* Release versioning
* GitHub Releases
* Deployment metrics
* GitOps workflow
* Separate staging environment
* Cloud Kubernetes deployment

### Observability

* Prometheus
* Grafana
* Centralized logging
* Distributed tracing
* Alerting
* SLI / SLO monitoring

---

## Portfolio Scope

This project is intentionally designed as a practical full-stack and DevOps portfolio application.

The project demonstrates the complete lifecycle from application development to containerization, Kubernetes deployment, and CI/CD automation.

```text
Application Development
        |
        v
Angular + Spring Boot
        |
        v
REST API + Database
        |
        v
Docker
        |
        v
Docker Compose
        |
        v
Kubernetes
        |
        v
Kustomize
        |
        v
GitHub Actions
        |
        v
GHCR
        |
        v
Development
        |
        v
Production Approval
        |
        v
Production
        |
        v
Health Verification
        |
        v
Rollback
```

The main application business flow remains:

```text
Login
  ↓
JWT Authentication
  ↓
Dashboard
  ↓
Accounts
  ↓
Transactions
  ↓
Money Transfer
  ↓
Database Update
  ↓
Updated Account Balance
  ↓
Updated Transaction History
```

All major banking screens use backend API data rather than static frontend mock data.

---

## License

This project is intended for educational and portfolio demonstration purposes.
