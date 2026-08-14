# Digital Banking Platform — TODO

> A secure, modular retail-banking platform built to demonstrate end-to-end full-stack engineering, backend design, security, transaction processing, testing, and practical microservice architecture.

---

## 0. Project Definition

### Project Name
**Digital Banking Platform**

### One-Line Description
A secure digital banking platform where customers can manage bank accounts, transfer money, track transactions, and analyze financial activity, while administrators can monitor users, accounts, transactions, and audit events.

### Primary Goal
Build a realistic but manageable banking application that demonstrates:

- Java + Spring Boot backend development
- React + TypeScript frontend development
- JWT authentication and authorization
- Role-based access control
- RESTful API design
- Microservice architecture
- PostgreSQL database design
- Transaction processing and consistency
- Idempotent financial operations
- Audit logging
- Data visualization
- Automated testing
- Dockerized local setup
- API documentation

### Project Philosophy

Focus more on:

- Business logic
- Correctness
- Security
- API design
- Transaction consistency
- Error handling
- Testing
- Architecture

Focus less on:

- Complex visual design
- Animations
- UI polish
- Unnecessary technologies
- Overengineering

> **Principle:** Build a small banking system whose important functionality can be explained deeply instead of a large system with shallow features.

---

# 1. Technology Stack

## Backend

- [ ] Java 21
- [ ] Spring Boot
- [ ] Spring Security
- [ ] JWT
- [ ] Spring Data JPA
- [ ] Hibernate
- [ ] Spring Cloud Gateway
- [ ] RESTful APIs
- [ ] Bean Validation
- [ ] Maven
- [ ] Lombok

## Frontend

- [ ] React
- [ ] TypeScript
- [ ] Axios
- [ ] React Router
- [ ] Chart.js

## Database

- [ ] PostgreSQL
- [ ] pgAdmin

## Testing

- [ ] JUnit 5
- [ ] Mockito
- [ ] Spring Boot Test
- [ ] Testcontainers
- [ ] Jest
- [ ] React Testing Library
- [ ] Postman for API validation

## DevOps / Tooling

- [ ] Git
- [ ] GitHub
- [ ] Docker
- [ ] Docker Compose
- [ ] GitHub Actions (optional after the core project works)

## Documentation

- [ ] Swagger / OpenAPI
- [ ] README.md
- [ ] Architecture diagram
- [ ] API documentation
- [ ] Setup instructions

---

# 2. High-Level Use Case

## Customer

A customer can:

- [ ] Register
- [ ] Log in securely
- [ ] Log out
- [ ] Refresh authentication
- [ ] View profile
- [ ] View bank accounts
- [ ] View account balance
- [ ] View account status
- [ ] Manage beneficiaries
- [ ] Transfer money
- [ ] View transaction history
- [ ] Search and filter transactions
- [ ] Analyze debit/credit activity
- [ ] View transaction status
- [ ] View recent account activity

## Administrator

An administrator can:

- [ ] View customers
- [ ] View accounts
- [ ] Search accounts/users
- [ ] Activate/deactivate accounts
- [ ] View transactions
- [ ] Filter failed transactions
- [ ] Monitor transaction status
- [ ] View audit logs
- [ ] View high-level banking statistics

---

# 3. High-Level Architecture

```text
                    React + TypeScript
                           |
                           v
                 Spring Cloud Gateway
                           |
          +----------------+----------------+
          |                |                |
          v                v                v
     Auth Service     Account Service   Transaction Service
          |                |                |
          v                v                v
       Auth DB         Account DB       Transaction DB
```

## Service Responsibilities

### Auth Service

Responsible for:

- [ ] User registration
- [ ] Login
- [ ] Password hashing
- [ ] JWT generation
- [ ] JWT validation
- [ ] Refresh tokens
- [ ] Logout
- [ ] Roles
- [ ] Authentication-related logic

Roles:

```text
ROLE_USER
ROLE_ADMIN
```

### Account Service

Responsible for:

- [ ] Customer profile
- [ ] Bank accounts
- [ ] Account status
- [ ] Balance
- [ ] Beneficiaries
- [ ] Account-related business rules

### Transaction Service

Responsible for:

- [ ] Transfers
- [ ] Debit transactions
- [ ] Credit transactions
- [ ] Transaction status
- [ ] Balance validation
- [ ] Transaction references
- [ ] Idempotency
- [ ] Failure handling
- [ ] Transaction history
- [ ] Audit-related transaction events

### API Gateway

Responsible for:

- [ ] Single entry point for frontend
- [ ] Request routing
- [ ] Common API concerns
- [ ] Security-related gateway configuration
- [ ] Service endpoint abstraction

---

# 4. Core Architecture Principles

- [ ] Each service owns its own data
- [ ] Do not allow direct cross-service database access
- [ ] Services communicate through APIs
- [ ] Frontend communicates through the API Gateway
- [ ] Use DTOs instead of exposing entities directly
- [ ] Centralize exception handling where appropriate
- [ ] Validate inputs at API boundaries
- [ ] Use consistent HTTP status codes
- [ ] Use clear error responses
- [ ] Keep business logic inside services, not controllers
- [ ] Keep transaction processing explicit and testable

> The system may use one PostgreSQL server/container for local development, while logically maintaining separate databases/schema ownership for each service.

---

# PHASE 1 — FOUNDATION, DESIGN & AUTHENTICATION

## 1.1 Requirements & Scope

- [ ] Finalize project use case
- [ ] Define customer workflows
- [ ] Define admin workflows
- [ ] Identify authentication requirements
- [ ] Identify account rules
- [ ] Identify transaction rules
- [ ] Identify audit requirements
- [ ] Define core MVP
- [ ] Separate optional/advanced features from MVP

### MVP Core

- [ ] Authentication
- [ ] JWT
- [ ] User/Admin roles
- [ ] Account management
- [ ] Beneficiaries
- [ ] Internal money transfer
- [ ] Transaction history
- [ ] Basic dashboard
- [ ] Basic analytics
- [ ] Admin monitoring
- [ ] Audit logging
- [ ] Backend tests
- [ ] Frontend tests
- [ ] Docker Compose
- [ ] Swagger/OpenAPI

---

## 1.2 Define Main Entities

### Auth DB

```text
users
roles
refresh_tokens
```

### Account DB

```text
customers
accounts
beneficiaries
```

### Transaction DB

```text
transactions
transaction_events
idempotency_keys
audit_logs
```

---

## 1.3 Core Transaction Model

Transaction fields should include at least:

```text
transactionId
referenceNumber
senderAccount
receiverAccount
amount
transactionType
status
description
createdAt
completedAt
failureReason
```

Suggested transaction types:

```text
DEBIT
CREDIT
TRANSFER
```

Suggested statuses:

```text
INITIATED
PROCESSING
COMPLETED
FAILED
```

---

## 1.4 Authentication

Implement:

- [ ] Registration
- [ ] Login
- [ ] Password hashing
- [ ] JWT access token
- [ ] Refresh token
- [ ] Logout
- [ ] JWT validation
- [ ] Role-based authorization
- [ ] Protected endpoints
- [ ] Authentication error handling
- [ ] Authorization error handling

### Initial API Endpoints

```text
POST /auth/register
POST /auth/login
POST /auth/refresh
POST /auth/logout
```

### Security Rules

- [ ] Never store plain-text passwords
- [ ] Protect private APIs with JWT
- [ ] Validate JWT before protected operations
- [ ] Restrict ADMIN endpoints to ADMIN users
- [ ] Restrict USER resources to resource owners
- [ ] Do not expose sensitive user information unnecessarily

---

## 1.5 Foundation Deliverables

- [ ] Git repository initialized
- [ ] README skeleton
- [ ] Backend multi-module/service structure
- [ ] React application created
- [ ] PostgreSQL configured
- [ ] Docker Compose skeleton
- [ ] Environment configuration strategy
- [ ] Initial architecture diagram
- [ ] Initial database design
- [ ] Initial API documentation structure

### Phase 1 Exit Criteria

- [ ] User can register
- [ ] User can log in
- [ ] JWT is issued and validated
- [ ] Protected endpoint works
- [ ] USER/ADMIN access rules work
- [ ] Project can run locally

---

# PHASE 2 — ACCOUNT MANAGEMENT & CORE BANKING OPERATIONS

## 2.1 Customer Profile

- [ ] View profile
- [ ] Update profile where appropriate
- [ ] Securely retrieve current user's data

---

## 2.2 Account Management

Implement:

- [ ] Create account
- [ ] View all accounts
- [ ] View account details
- [ ] View account balance
- [ ] View account status
- [ ] Activate/deactivate account where allowed
- [ ] Enforce ownership rules

### Suggested Account States

```text
ACTIVE
INACTIVE
BLOCKED
CLOSED
```

### Account Rules

- [ ] Only valid users can access accounts
- [ ] Only account owners can access customer account data
- [ ] Inactive/blocked accounts cannot initiate transfers
- [ ] Closed accounts cannot transact
- [ ] Balance must not become negative

---

## 2.3 Beneficiary Management

Implement:

- [ ] Add beneficiary
- [ ] View beneficiaries
- [ ] Delete beneficiary
- [ ] Validate beneficiary account
- [ ] Prevent invalid/duplicate beneficiary relationships

### Example APIs

```text
GET    /beneficiaries
POST   /beneficiaries
DELETE /beneficiaries/{id}
```

---

## 2.4 Suggested Account APIs

```text
GET    /accounts
GET    /accounts/{id}
POST   /accounts
PATCH  /accounts/{id}/status
```

---

## 2.5 React Foundation

Implement a simple functional UI:

- [ ] Login page
- [ ] Registration page
- [ ] Protected routes
- [ ] Customer dashboard
- [ ] Account summary
- [ ] Account details
- [ ] Beneficiary page
- [ ] Logout
- [ ] Basic navigation
- [ ] Loading states
- [ ] Error states

### UI Principle

Keep the design simple and readable.

Do not spend significant development time on:

- animations
- visual effects
- complex design systems
- advanced responsive layouts

Prioritize functionality.

---

## 2.6 Phase 2 Exit Criteria

- [ ] Customer can log in
- [ ] Customer can view their accounts
- [ ] Customer can view balance
- [ ] Customer can manage beneficiaries
- [ ] Ownership restrictions work
- [ ] React consumes backend APIs successfully
- [ ] Protected frontend routes work

---

# PHASE 3 — TRANSACTION ENGINE, SECURITY & AUDITABILITY

> **This is the most important phase of the project. Spend the most engineering effort here.**

## 3.1 Money Transfer

Implement:

- [ ] Internal account-to-account transfer
- [ ] Validate sender account
- [ ] Validate receiver account
- [ ] Validate account status
- [ ] Validate sender != receiver
- [ ] Validate amount > 0
- [ ] Validate sufficient balance
- [ ] Create transaction reference
- [ ] Store timestamp
- [ ] Update debit/credit records
- [ ] Store transaction status
- [ ] Return transaction result

### Example API

```text
POST /transactions/transfer
```

Example conceptual request:

```json
{
  "senderAccountId": "...",
  "receiverAccountId": "...",
  "amount": 5000,
  "description": "Monthly transfer"
}
```

---

## 3.2 Transaction Lifecycle

Implement a traceable lifecycle:

```text
INITIATED
    |
    v
PROCESSING
    |
    +-------> FAILED
    |
    v
COMPLETED
```

Store:

- [ ] Current status
- [ ] Created timestamp
- [ ] Completion timestamp
- [ ] Failure reason
- [ ] Reference number

---

## 3.3 Transaction Rules

- [ ] Sender account must exist
- [ ] Receiver account must exist
- [ ] Sender and receiver cannot be identical
- [ ] Sender must be ACTIVE
- [ ] Receiver must be ACTIVE
- [ ] Transfer amount must be positive
- [ ] Sender must have sufficient balance
- [ ] Failed transaction must be traceable
- [ ] Completed transaction must have a reference
- [ ] Transaction history should not be freely editable

---

## 3.4 Idempotency

Implement an idempotency mechanism for transfer requests.

Example:

```text
Idempotency-Key: abc123
```

Expected behavior:

```text
Request 1 -> transfer executed
Request 2 -> existing transaction returned
```

### Requirements

- [ ] Store idempotency key
- [ ] Associate it with the resulting transaction
- [ ] Prevent duplicate transfer execution
- [ ] Return previously created result for repeated request

> Interview concept: Preventing duplicate financial operations when the client retries a request due to timeout/network failure.

---

## 3.5 Concurrency & Consistency

Handle cases such as:

```text
Initial balance = ₹10,000

Transfer A = ₹8,000
Transfer B = ₹7,000
```

The system must not allow both transfers simply because both requests read the same starting balance.

Investigate and implement appropriate:

- [ ] Database transaction boundaries
- [ ] Locking strategy
- [ ] Consistency rules
- [ ] Concurrent update handling
- [ ] Rollback behavior

### Interview Topic

Be able to explain:

> How does the application prevent race conditions and incorrect balances when two transfer requests happen at the same time?

---

## 3.6 Transaction History

Implement:

- [ ] View transaction history
- [ ] Transaction detail
- [ ] Pagination
- [ ] Sorting
- [ ] Filtering
- [ ] Search
- [ ] Filter by type
- [ ] Filter by status
- [ ] Filter by date range

### Example

```text
GET /transactions
GET /transactions/{id}
```

Possible filters:

```text
type=DEBIT
status=COMPLETED
from=2026-08-01
to=2026-08-31
```

---

## 3.7 Audit Logging

Track important events:

```text
LOGIN
ACCOUNT_CREATED
TRANSFER_INITIATED
TRANSFER_COMPLETED
TRANSFER_FAILED
ACCOUNT_BLOCKED
BENEFICIARY_ADDED
```

Audit data should include:

```text
user
action
resource
timestamp
status
reference
```

Example:

```text
User: 1023
Action: TRANSFER_COMPLETED
Transaction: TXN98473
Timestamp: ...
```

---

## 3.8 Error Handling

Implement:

- [ ] Global exception handling
- [ ] Validation errors
- [ ] Authentication errors
- [ ] Authorization errors
- [ ] Business rule violations
- [ ] Transaction failures
- [ ] Resource-not-found errors
- [ ] Consistent error response format

Example:

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "INSUFFICIENT_FUNDS",
  "message": "Insufficient balance",
  "path": "/transactions/transfer"
}
```

---

## 3.9 REST API Quality

Implement:

- [ ] DTOs
- [ ] Request validation
- [ ] Correct HTTP status codes
- [ ] Pagination
- [ ] Filtering
- [ ] Sorting
- [ ] Consistent responses
- [ ] Consistent error structure
- [ ] Swagger/OpenAPI documentation

---

## 3.10 Phase 3 Exit Criteria

- [ ] Successful transfer works
- [ ] Invalid transfers are rejected
- [ ] Insufficient funds are rejected
- [ ] Account status rules are enforced
- [ ] Duplicate transfer requests are prevented
- [ ] Transaction status is stored
- [ ] Transaction history works
- [ ] Audit logs are generated
- [ ] Concurrent balance updates are handled
- [ ] APIs return consistent errors
- [ ] Swagger documents core APIs

---

# PHASE 4 — DASHBOARDS, ANALYTICS, ADMIN & TESTING

## 4.1 Customer Dashboard

Display:

- [ ] Total balance
- [ ] Number of accounts
- [ ] Recent transactions
- [ ] Recent credits
- [ ] Recent debits
- [ ] Completed transaction count
- [ ] Failed transaction count

Keep UI simple.

---

## 4.2 Chart.js Analytics

Implement:

- [ ] Monthly spending chart
- [ ] Debit vs credit chart
- [ ] Transaction volume by day
- [ ] Spending/transactions by category if categories are added

Suggested dashboard metrics:

```text
Total Balance
Monthly Credits
Monthly Debits
Completed Transactions
Failed Transactions
```

---

## 4.3 Admin Dashboard

Implement:

- [ ] View customers
- [ ] Search users
- [ ] View accounts
- [ ] Search/filter accounts
- [ ] View transactions
- [ ] Search transaction by reference
- [ ] Filter failed transactions
- [ ] View transaction status
- [ ] Activate/deactivate accounts
- [ ] View audit logs
- [ ] View high-level system statistics

Suggested APIs:

```text
GET /admin/users
GET /admin/accounts
GET /admin/transactions
GET /admin/audit-logs
```

---

## 4.4 Backend Testing

### Unit Tests

Test:

- [ ] Registration logic
- [ ] Login logic
- [ ] JWT-related logic
- [ ] Authorization rules
- [ ] Account creation
- [ ] Account validation
- [ ] Transfer validation
- [ ] Insufficient funds
- [ ] Invalid account
- [ ] Duplicate transaction
- [ ] Failed transaction
- [ ] Audit log creation

### Important Transfer Tests

```text
Given balance = ₹10,000
Transfer ₹6,000 -> SUCCESS

Given balance = ₹10,000
Transfer ₹5,000 after the first transfer -> FAILURE
```

### Idempotency Test

```text
Same Idempotency-Key
        |
        +--> No duplicate transaction
```

### Integration Tests

- [ ] API endpoint integration tests
- [ ] Database integration tests
- [ ] Security integration tests
- [ ] Transaction integration tests
- [ ] Testcontainers for realistic database testing

---

## 4.5 Frontend Testing

Test:

- [ ] Login
- [ ] Protected routes
- [ ] Dashboard rendering
- [ ] Account display
- [ ] Transaction list
- [ ] Transfer form
- [ ] Form validation
- [ ] Error handling
- [ ] Logout

---

## 4.6 Phase 4 Exit Criteria

- [ ] Customer dashboard works
- [ ] Charts use real backend data
- [ ] Admin dashboard works
- [ ] RBAC is visible in practice
- [ ] Important backend business logic is tested
- [ ] Frontend critical paths are tested
- [ ] Integration tests cover important workflows

---

# PHASE 5 — DOCKER, DEPLOYMENT, DOCUMENTATION & OPTIONAL ADVANCED FEATURES

## 5.1 Docker

Containerize:

```text
postgres
auth-service
account-service
transaction-service
api-gateway
frontend
```

Create:

- [ ] Dockerfile for backend services
- [ ] Dockerfile for frontend
- [ ] docker-compose.yml
- [ ] Environment variables
- [ ] Service networking
- [ ] Database configuration
- [ ] Startup/dependency configuration

### Target Developer Experience

A new developer should eventually be able to run:

```bash
docker compose up
```

and start the complete local system.

---

## 5.2 Git & GitHub

- [ ] Meaningful repository structure
- [ ] `.gitignore`
- [ ] Environment secrets excluded
- [ ] Meaningful commit messages
- [ ] Feature branches where practical
- [ ] Pull requests for meaningful changes
- [ ] Protect main branch if desired
- [ ] Add project screenshots later
- [ ] Add architecture diagram
- [ ] Add API documentation

---

## 5.3 README.md

README should contain:

- [ ] Project overview
- [ ] Use case
- [ ] Key features
- [ ] Architecture
- [ ] Tech stack
- [ ] Service responsibilities
- [ ] Database design
- [ ] API overview
- [ ] Authentication flow
- [ ] Transaction flow
- [ ] Idempotency approach
- [ ] Concurrency/consistency approach
- [ ] Testing
- [ ] Docker setup
- [ ] Local installation
- [ ] Environment variables
- [ ] API documentation
- [ ] Screenshots
- [ ] Future enhancements
- [ ] Interview talking points

---

## 5.4 API Documentation

Document:

- [ ] Auth endpoints
- [ ] Account endpoints
- [ ] Beneficiary endpoints
- [ ] Transaction endpoints
- [ ] Admin endpoints
- [ ] Request/response examples
- [ ] Authentication requirements
- [ ] Error responses

---

## 5.5 Deployment

After local Docker setup works:

- [ ] Choose deployment platform
- [ ] Deploy frontend
- [ ] Deploy backend services
- [ ] Deploy PostgreSQL
- [ ] Configure environment variables
- [ ] Configure CORS
- [ ] Configure production secrets
- [ ] Test production APIs
- [ ] Test authentication
- [ ] Test transaction flow

Deployment is a final step, not a reason to complicate the initial architecture.

---

# 6. Optional Advanced Features

> Add these only after the core project is complete and stable.

## Notifications

- [ ] Notification service
- [ ] Email after successful transfer
- [ ] Email after failed transfer
- [ ] Notification history

Possible flow:

```text
Transaction Service
       |
       v
Transaction Event
       |
       v
Notification Service
       |
       v
Email
```

---

## Redis

Potential use cases:

- [ ] Caching
- [ ] Rate limiting
- [ ] Temporary token/session data
- [ ] Frequently accessed read data

---

## Event-Driven Architecture

Potential future flow:

```text
Transaction Service
        |
        v
   Transaction Event
        |
        +----> Notification Service
        |
        +----> Analytics/Reporting
```

Potential technology:

- [ ] Apache Kafka

---

## Rate Limiting

Implement limits for:

- [ ] Login attempts
- [ ] Transfer requests
- [ ] Sensitive endpoints

---

## Account Statements

- [ ] Monthly statement generation
- [ ] CSV export
- [ ] PDF export

---

## Scheduled Transfers

Example:

```text
Transfer ₹5,000
every month
```

Features:

- [ ] Schedule transfer
- [ ] View scheduled transfers
- [ ] Cancel scheduled transfer
- [ ] Execute scheduled transfer

---

# 7. Features Deliberately Excluded from MVP

Do not add these just to increase the technology list:

- [ ] Kubernetes
- [ ] Complex cloud infrastructure
- [ ] Large-scale observability stack
- [ ] Multiple messaging systems
- [ ] Multiple caching systems
- [ ] External payment gateway
- [ ] Fraud detection ML model
- [ ] Complex banking integrations
- [ ] Excessive microservices

> The goal is depth, not technology count.

---

# 8. Suggested Development Order

```text
1. Finalize requirements
        ↓
2. Design architecture
        ↓
3. Design database/entities
        ↓
4. Design API contracts
        ↓
5. Build Auth Service
        ↓
6. Build Account Service
        ↓
7. Build Transaction Service
        ↓
8. Build API Gateway
        ↓
9. Connect React frontend
        ↓
10. Build customer dashboard
        ↓
11. Build transaction analytics
        ↓
12. Build admin dashboard
        ↓
13. Add audit logging
        ↓
14. Add backend tests
        ↓
15. Add frontend tests
        ↓
16. Dockerize complete application
        ↓
17. Complete Swagger/OpenAPI
        ↓
18. Complete README
        ↓
19. Deploy
        ↓
20. Add optional advanced features
```

---

# 9. Core Interview Topics to Master

The project should allow you to confidently discuss these topics.

## Authentication & Security

- [ ] How JWT authentication works
- [ ] Access tokens vs refresh tokens
- [ ] Password hashing
- [ ] Spring Security filters
- [ ] Authentication vs authorization
- [ ] Role-based access control
- [ ] Protected endpoints

## Transaction Processing

- [ ] Database transactions
- [ ] ACID principles
- [ ] Balance validation
- [ ] Rollback behavior
- [ ] Transaction status
- [ ] Failed transactions
- [ ] Idempotency
- [ ] Duplicate requests

## Concurrency

- [ ] Race conditions
- [ ] Concurrent transfers
- [ ] Database locking
- [ ] Optimistic vs pessimistic locking
- [ ] Maintaining correct balances

## Microservices

- [ ] Why services were separated
- [ ] Service responsibilities
- [ ] Service-to-service communication
- [ ] API Gateway
- [ ] Database-per-service principle
- [ ] Trade-offs of microservices
- [ ] What would change at larger scale

## REST APIs

- [ ] HTTP methods
- [ ] HTTP status codes
- [ ] DTOs
- [ ] Validation
- [ ] Pagination
- [ ] Filtering
- [ ] Sorting
- [ ] Error response design

## Testing

- [ ] Unit testing
- [ ] Mocking
- [ ] Integration testing
- [ ] Testcontainers
- [ ] Frontend testing
- [ ] API testing

---

# 10. Important End-to-End Scenarios

These should work before considering the MVP complete.

## Scenario 1 — Registration & Login

```text
Register
   ↓
Login
   ↓
JWT
   ↓
Access protected dashboard
```

## Scenario 2 — View Account

```text
Login
   ↓
JWT
   ↓
GET /accounts
   ↓
Account + balance displayed
```

## Scenario 3 — Successful Transfer

```text
Login
   ↓
Select account
   ↓
Select beneficiary
   ↓
Enter amount
   ↓
Validate
   ↓
Transfer
   ↓
Debit sender
   ↓
Credit receiver
   ↓
Create transaction
   ↓
COMPLETED
   ↓
Display transaction reference
```

## Scenario 4 — Failed Transfer

```text
Transfer request
      ↓
Validation
      ↓
Insufficient balance
      ↓
FAILED
      ↓
No incorrect balance update
      ↓
Failure recorded
```

## Scenario 5 — Duplicate Request

```text
Request + Idempotency-Key
          ↓
Transfer executed
          ↓
Retry same request
          ↓
Existing transaction returned
          ↓
No duplicate debit
```

## Scenario 6 — Admin Monitoring

```text
ADMIN Login
    ↓
Admin Dashboard
    ↓
Users / Accounts / Transactions / Audit Logs
```

---

# 11. Definition of Done

The core project is complete only when:

- [ ] Authentication works
- [ ] JWT protection works
- [ ] USER/ADMIN roles work
- [ ] Accounts work
- [ ] Beneficiaries work
- [ ] Transfers work
- [ ] Balance validation works
- [ ] Duplicate transfers are prevented
- [ ] Concurrent transfer behavior is handled
- [ ] Transaction lifecycle is stored
- [ ] Transaction history works
- [ ] Audit logs work
- [ ] Admin monitoring works
- [ ] Charts display real backend data
- [ ] Backend tests pass
- [ ] Frontend critical-path tests pass
- [ ] Swagger is available
- [ ] Docker Compose runs the system
- [ ] README explains the architecture and setup
- [ ] Project is deployed or deployment-ready

---

# 12. Resume-Focused Outcome

After completion, the project should demonstrate:

- Secure JWT-based authentication
- Role-based access control
- Spring Boot REST API development
- Microservice architecture
- PostgreSQL database design
- Financial transaction processing
- Idempotent API design
- Concurrency/consistency handling
- Audit logging
- React + TypeScript development
- Chart.js data visualization
- Unit and integration testing
- Dockerized application development
- API documentation

## Strong Resume-Level Project Description

> **Digital Banking Platform** — Built a secure full-stack banking platform using Java, Spring Boot, React, JWT, PostgreSQL, and microservices, supporting account management, beneficiary handling, money transfers, transaction tracking, role-based access, audit logging, and financial analytics. Implemented idempotent transfer processing, validation, concurrency-safe balance updates, automated testing, and Dockerized local deployment.

---

# 13. Final Priority Order

When time is limited, prioritize in this order:

### Priority 1 — Must Be Excellent

1. JWT authentication
2. Spring Security
3. Account management
4. Transfer business logic
5. Balance validation
6. Transaction lifecycle
7. Idempotency
8. Concurrency/consistency
9. REST API quality
10. Testing

### Priority 2 — Must Work

11. React dashboard
12. Admin dashboard
13. Transaction history
14. Audit logs
15. Chart.js analytics
16. Docker Compose
17. Swagger/OpenAPI

### Priority 3 — Nice to Have

18. Redis
19. Notifications
20. Kafka
21. Rate limiting
22. Statements
23. Scheduled transfers
24. GitHub Actions

---

# 14. Golden Rule for This Project

> **Do not add technology unless it solves a real requirement.**

The strongest version of this project is not the one with the most tools.

It is the one where you can confidently explain:

> **“Here is the problem, here is why I designed the system this way, here is how the transaction is processed, here is how I keep the data consistent, here is how I secure it, here is how I test it, and here is how the services interact.”**

That is the level of depth to aim for.
