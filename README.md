# Digital Banking Demo

A full-stack digital banking demo application built with **Angular** and **Spring Boot**.

This project demonstrates a real end-to-end banking application flow:

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

### Backend

* Java 22
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* Jakarta Bean Validation
* REST API

### Database

* H2 Database
* Flyway Database Migration

### Development

* Node.js
* npm
* Angular CLI
* Maven

---

## Project Structure

```text
digital-banking-demo/
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
│       └── proxy.conf.json
│
└── backend/
    └── digital-banking-api/
        │
        ├── src/
        │   └── main/
        │       ├── java/
        │       └── resources/
        │           └── db/
        │               └── migration/
        │
        ├── pom.xml
        └── data/
            └── banking
```

---

## Application Architecture

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
Source Account          Destination Account
        |                      |
        v                      v
Decrease Balance        Increase Balance
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

## Running the Backend

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

## Database

The application uses an H2 file database.

```text
jdbc:h2:file:./data/banking
```

The database is persisted under the backend project.

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

---

## Project Goals

This project demonstrates practical full-stack development skills including:

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

---

## Future Improvements

Planned or possible improvements:

* Automated frontend tests
* Automated backend tests
* Docker
* Docker Compose
* CI/CD pipeline
* PostgreSQL or SQL Server
* Role-based authorization
* OpenAPI / Swagger
* Production configuration profiles
* Centralized logging
* Application monitoring
* Kubernetes deployment

---

## Portfolio Scope

This project is intentionally designed as a practical full-stack portfolio application.

The main focus is demonstrating how a frontend application communicates with a backend API and database through a complete business flow:

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
