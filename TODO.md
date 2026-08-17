# Digital Banking Platform — TODO

> A secure, modular retail-banking platform demonstrating end-to-end full-stack engineering, microservice architecture, JWT security, transaction processing, idempotency, concurrency handling, auditability, React/TypeScript development, analytics, and automated testing.

---

# 0. Project Status

## Current Overall State

```text
Phase 1 — Authentication & Security       ✅ Complete
Phase 2 — Account Management              ✅ Complete
Phase 3 — Transaction Engine              ✅ Complete
Phase 4 — Frontend Integration            ✅ Complete
Phase 5 — Docker / Deployment / Final Docs ⏳ Remaining / Optional
```

The core application is complete and the Phase 4 frontend branch has been pushed and raised as a PR to `develop`.

---

# 1. Project Definition

## Project Name

**Digital Banking Platform**

## One-Line Description

A secure digital banking platform where customers can register, manage customer profiles and bank accounts, manage beneficiaries, transfer money, view transaction history, analyze financial activity, while administrators can monitor platform-wide statistics.

## Primary Goal

Demonstrate:

- Java + Spring Boot backend development
- React + TypeScript frontend development
- JWT authentication and authorization
- Role-based access control
- RESTful API design
- Microservice architecture
- PostgreSQL database-per-service ownership
- Financial transaction processing
- Idempotent operations
- Concurrency-safe balance updates
- Audit logging
- Data visualization
- Automated backend and frontend testing
- API Gateway integration
- Swagger/OpenAPI documentation

## Project Philosophy

Focus on:

- Correctness
- Business logic
- Security
- API design
- Transaction consistency
- Error handling
- Testing
- Clear service boundaries

Avoid:

- Unnecessary technologies
- Overengineering
- Complex visual effects
- Technology for technology's sake

> **Principle:** Build a banking system whose important functionality can be explained deeply instead of a large system with shallow features.

---

# 2. Technology Stack

## Backend

- [x] Java 21
- [x] Spring Boot
- [x] Spring Security
- [x] JWT
- [x] Spring Security OAuth2 Resource Server
- [x] Spring Data JPA
- [x] Hibernate
- [x] Spring Cloud Gateway
- [x] RESTful APIs
- [x] Bean Validation
- [x] Maven
- [x] RestClient for service-to-service communication

## Frontend

- [x] React
- [x] TypeScript
- [x] Vite
- [x] Axios
- [x] React Router
- [x] Material UI (MUI)
- [x] Recharts
- [x] Responsive application layout

## Database

- [x] PostgreSQL
- [x] pgAdmin

Logical databases:

```text
auth_db
account_db
transaction_db
```

## Testing

- [x] JUnit 5
- [x] Mockito
- [x] Spring Boot Test
- [x] Postman for API validation
- [x] Vitest
- [x] React Testing Library
- [x] @testing-library/user-event
- [ ] Testcontainers

## DevOps / Tooling

- [x] Git
- [x] GitHub
- [x] Feature branches
- [x] Pull requests
- [x] Environment variable configuration
- [ ] Docker
- [ ] Docker Compose
- [ ] GitHub Actions

## Documentation

- [x] Swagger / OpenAPI
- [x] README
- [ ] Final architecture diagram
- [x] Postman/API validation documentation
- [ ] Final setup/deployment documentation

---

# 3. Current Architecture

```text
                         React + TypeScript
                              :5173
                                |
                                v
                         API Gateway
                              :8080
                                |
             +------------------+------------------+
             |                  |                  |
             v                  v                  v
       Auth Service       Account Service     Transaction Service
           :8081              :8082                :8083
             |                  |                    |
             v                  v                    v
          auth_db           account_db         transaction_db
                                                 |
                                                 v
                                             audit_logs
```

## Service Responsibilities

### Auth Service

- [x] User registration
- [x] Password hashing with BCrypt
- [x] Login
- [x] JWT access tokens
- [x] Refresh tokens
- [x] Logout / refresh-token revocation
- [x] JWT Resource Server validation
- [x] `ROLE_USER`
- [x] `ROLE_ADMIN`
- [x] `/api/auth/me`
- [x] Admin statistics API
- [x] Authentication/authorization error handling
- [x] Swagger/OpenAPI

### Account Service

- [x] Customer profile
- [x] Bank account creation
- [x] Savings accounts
- [x] Current accounts
- [x] Account status
- [x] Balance ownership
- [x] Account ownership enforcement
- [x] Beneficiary management
- [x] Internal transfer endpoint
- [x] Internal service-secret protection
- [x] Admin account statistics API
- [x] JWT Resource Server validation
- [x] Swagger/OpenAPI
- [x] Service-layer automated tests

### Transaction Service

- [x] Transfer initiation
- [x] Transaction references
- [x] PENDING / COMPLETED / FAILED lifecycle
- [x] Account Service integration
- [x] Internal service authentication
- [x] Idempotency
- [x] Concurrent-request protection
- [x] Transaction history
- [x] Audit logging
- [x] Business vs infrastructure error handling
- [x] Admin transaction statistics API
- [x] JWT Resource Server validation
- [x] Swagger/OpenAPI
- [x] Automated service tests

### API Gateway

- [x] Single entry point for frontend
- [x] Auth routing
- [x] Account/customer/beneficiary routing
- [x] Transaction routing
- [x] Admin routing
- [x] CORS configuration
- [x] Browser preflight handling

---

# 4. Core Architecture Principles

- [x] Each service owns its own data
- [x] No direct cross-service database access
- [x] Services communicate through APIs
- [x] Frontend communicates through API Gateway
- [x] DTOs are used instead of exposing entities directly
- [x] Business logic resides in services
- [x] API validation is enforced at boundaries
- [x] Ownership checks are enforced server-side
- [x] Service-to-service endpoints use an internal secret
- [x] Financial transfers are idempotent
- [x] Balance updates are concurrency-safe
- [x] Audit events are persisted

---

# PHASE 1 — AUTHENTICATION & SECURITY ✅

## 1.1 Authentication

- [x] Registration
- [x] Login
- [x] BCrypt password hashing
- [x] JWT access token
- [x] Refresh token
- [x] Logout
- [x] Refresh-token revocation
- [x] JWT validation
- [x] Role-based authorization
- [x] Protected endpoints
- [x] `/api/auth/me`
- [x] Authentication error handling
- [x] Authorization error handling

### Implemented APIs

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
GET  /api/auth/me
```

## 1.2 Roles

```text
ROLE_USER
ROLE_ADMIN
```

## 1.3 Security Validation

- [x] Request without token → 401
- [x] Invalid JWT → 401
- [x] Valid JWT → protected resource access
- [x] User-only access
- [x] Admin-only access
- [x] Fresh JWT required after role changes

## 1.4 Swagger

- [x] Swagger UI
- [x] OpenAPI specification
- [x] Bearer JWT Authorize button
- [x] Swagger endpoints permitted in security configuration

### Phase 1 Exit Criteria

- [x] User can register
- [x] User can log in
- [x] JWT is issued and validated
- [x] Refresh flow works
- [x] Logout/revocation works
- [x] USER/ADMIN access rules work
- [x] Auth endpoints documented

---

# PHASE 2 — ACCOUNT MANAGEMENT ✅

## 2.1 Customer Profile

- [x] Create customer profile
- [x] View current customer
- [x] Ownership enforced through authenticated user ID

APIs:

```text
POST /api/customers
GET  /api/customers/me
```

## 2.2 Account Management

- [x] Create account
- [x] View accounts
- [x] View account details
- [x] View balance
- [x] View status
- [x] Savings account
- [x] Current account
- [x] Ownership enforcement
- [x] Prevent unauthorized access
- [x] Prevent invalid account use in transfers

## 2.3 Beneficiary Management

- [x] Add beneficiary
- [x] View beneficiaries
- [x] View beneficiary
- [x] Delete beneficiary
- [x] Validate beneficiary account
- [x] Prevent duplicate beneficiary
- [x] Prevent adding own account
- [x] Prevent inactive beneficiary account
- [x] Expose actual beneficiary account ID to frontend

APIs:

```text
GET    /api/beneficiaries
POST   /api/beneficiaries
GET    /api/beneficiaries/{id}
DELETE /api/beneficiaries/{id}
```

## 2.4 Account Security

- [x] JWT Resource Server
- [x] User-to-account ownership enforcement
- [x] Internal Account Service endpoint
- [x] Internal service-secret protection

## 2.5 Account Service Testing

- [x] Customer service tests
- [x] Account service tests
- [x] Beneficiary service tests
- [x] Spring context test
- [x] Maven test/build validation

## 2.6 Account UI

- [x] Accounts page
- [x] Customer profile creation UI
- [x] Savings creation UI
- [x] Current account creation UI
- [x] Account summary
- [x] Account balance display
- [x] Account status display
- [x] Loading/error/empty states

### Phase 2 Exit Criteria

- [x] Customer profile works
- [x] Customers can create/view accounts
- [x] Account ownership works
- [x] Beneficiaries work
- [x] Account APIs work through Gateway
- [x] Account Swagger available
- [x] Account automated service tests pass

---

# PHASE 3 — TRANSACTION ENGINE ✅

> **Most important engineering phase.**

## 3.1 Money Transfer

- [x] Account-to-account transfer
- [x] Source account validation
- [x] Destination account validation
- [x] Account status validation
- [x] Sender != receiver validation
- [x] Amount > 0 validation
- [x] Two-decimal amount validation
- [x] Sufficient-balance validation
- [x] Transaction reference generation
- [x] PENDING state
- [x] COMPLETED state
- [x] FAILED state
- [x] Completion timestamp
- [x] Balance debit/credit

API:

```text
POST /api/transactions/transfers
```

## 3.2 Idempotency

- [x] `Idempotency-Key` header
- [x] Unique database constraint
- [x] Fast-path existing transaction lookup
- [x] Concurrent duplicate protection
- [x] Existing result returned for repeated requests

Expected:

```text
Request 1 -> transfer executed
Request 2 -> existing transaction returned
```

## 3.3 Concurrency & Consistency

- [x] Database transaction boundaries
- [x] Pessimistic locking for balance updates
- [x] Concurrent transfer protection
- [x] Balance consistency
- [x] Failure handling
- [x] Idempotency race handling

## 3.4 Transaction History

- [x] Account history
- [x] Ownership enforcement
- [x] Sender sees transactions
- [x] Receiver sees transactions
- [x] Newest-first sorting

API:

```text
GET /api/transactions/account/{accountId}
```

Not currently implemented:

- [ ] Pagination
- [ ] Search
- [ ] Advanced date filtering
- [ ] Advanced type/status filtering

## 3.5 Audit Logging

- [x] `TRANSFER_INITIATED`
- [x] `TRANSFER_COMPLETED`
- [x] `TRANSFER_FAILED`
- [x] User ID recorded
- [x] Transaction reference recorded
- [x] Status recorded
- [x] Message recorded
- [x] Timestamp recorded
- [x] Audit data persisted in `audit_logs`

## 3.6 Error Handling

- [x] Global exception handling
- [x] Validation errors
- [x] Authentication errors
- [x] Authorization errors
- [x] Business rule violations
- [x] Resource-not-found errors
- [x] Account Service business failures
- [x] Account Service unavailable → 503
- [x] Consistent error responses

## 3.7 Transaction Service Testing

- [x] Successful transfer
- [x] Business failure
- [x] Account Service unavailable
- [x] Idempotency
- [x] Missing idempotency key
- [x] Same-account validation
- [x] Zero amount
- [x] Invalid decimal precision
- [x] Spring context test
- [x] Maven package/test validation

## 3.8 Gateway Integration

- [x] Transaction routes through Gateway
- [x] Auth/Account/Transaction services accessible via `:8080`
- [x] Frontend uses Gateway only
- [x] CORS configuration
- [x] OPTIONS preflight support

### Phase 3 Exit Criteria

- [x] Successful transfer works
- [x] Invalid transfers rejected
- [x] Insufficient funds rejected
- [x] Duplicate transfer requests prevented
- [x] Concurrent transfer behavior handled
- [x] Transaction history works
- [x] Audit logs generated
- [x] Errors are structured
- [x] Gateway integration works
- [x] Swagger documents Transaction Service

---

# PHASE 4 — FRONTEND INTEGRATION, DASHBOARD, ADMIN & TESTING ✅

## 4.1 Frontend Foundation

- [x] React + TypeScript + Vite
- [x] MUI
- [x] React Router
- [x] Axios
- [x] Environment configuration
- [x] MUI theme
- [x] Application folder structure
- [x] API client
- [x] Protected route structure

## 4.2 Frontend Authentication

- [x] Register UI
- [x] Login UI
- [x] Logout UI
- [x] AuthContext
- [x] JWT persistence
- [x] `/api/auth/me` integration
- [x] Access token storage
- [x] Refresh token storage
- [x] 401 → refresh → retry Axios interceptor
- [x] Failed refresh → login redirect
- [x] ProtectedRoute
- [x] AdminRoute
- [x] ROLE_USER UI behavior
- [x] ROLE_ADMIN UI behavior

## 4.3 Customer Dashboard & Accounts

- [x] Responsive MUI application layout
- [x] Sidebar navigation
- [x] Mobile navigation drawer
- [x] Active navigation state
- [x] Customer profile creation
- [x] Account creation
- [x] Savings/current account selection
- [x] Account summary
- [x] Balance display
- [x] Account status display
- [x] Loading states
- [x] Error states
- [x] Empty states

## 4.4 Beneficiary UI

- [x] Beneficiary list
- [x] Add beneficiary
- [x] Duplicate handling
- [x] Own-account rejection
- [x] Invalid-account handling
- [x] Delete confirmation dialog
- [x] Delete operation
- [x] User ownership isolation
- [x] API error messages

## 4.5 Transfer UI

- [x] Source account selector
- [x] Beneficiary selector
- [x] Amount input
- [x] Description input
- [x] Client-side amount validation
- [x] Real beneficiary account ID integration
- [x] Unique idempotency key per logical transfer attempt
- [x] Transfer result UI
- [x] Success state
- [x] Failure state
- [x] Updated account balances after transfer

## 4.6 Transaction History UI

- [x] Account selector
- [x] Transaction list
- [x] Newest-first display
- [x] Sender transaction visibility
- [x] Receiver transaction visibility
- [x] Status chips
- [x] Empty history state
- [x] Error/loading states

## 4.7 Dashboard Analytics

- [x] Total balance
- [x] Total transactions
- [x] Completed transaction count
- [x] Failed transaction count
- [x] Transaction volume
- [x] Recent transactions
- [x] Credits/debits analytics
- [x] Recharts integration
- [x] Empty chart state
- [x] Real backend data

## 4.8 Admin Dashboard

- [x] Admin-only route
- [x] Admin user statistics
- [x] Customer statistics
- [x] Account statistics
- [x] Active account count
- [x] Total active balance
- [x] Transaction statistics
- [x] Completed transaction count
- [x] Failed transaction count
- [x] Transaction volume
- [x] Backend `ROLE_ADMIN` enforcement

Admin APIs:

```text
GET /api/admin/user-stats
GET /api/admin/account-stats
GET /api/admin/transaction-stats
```

## 4.9 Frontend Polish

- [x] Responsive layout
- [x] Active navigation
- [x] MUI confirmation dialog
- [x] Consistent loading states
- [x] Consistent error states
- [x] Informative empty states
- [x] Centralized API error message helper
- [x] Transfer form reset after success
- [x] Admin navigation visible only to admins
- [x] Favicon reference cleanup

## 4.10 Frontend Automated Tests

Testing stack:

```text
Vitest
React Testing Library
@testing-library/user-event
```

Critical tests implemented:

- [x] AuthContext tests
- [x] ProtectedRoute tests
- [x] AdminRoute tests
- [x] Login tests
- [x] Transfer tests
- [x] Dashboard tests
- [x] Transfer idempotency-key behavior
- [x] Transfer beneficiary-account-ID mapping
- [x] Role-based route behavior
- [x] Dashboard rendering/analytics states

## 4.11 Frontend Build

- [x] `npm run build`
- [x] `npm run test:run`

### Phase 4 Exit Criteria

- [x] Customer dashboard works
- [x] Accounts work
- [x] Beneficiaries work
- [x] Transfers work end-to-end
- [x] Transaction history works
- [x] Charts use backend data
- [x] Admin dashboard works
- [x] RBAC works in practice
- [x] Critical frontend tests pass
- [x] Production frontend build passes
- [x] Frontend integrated through API Gateway

---

# PHASE 5 — DOCKER, DEPLOYMENT & FINAL DOCUMENTATION ⏳

> Core application is complete. Phase 5 is packaging, deployment readiness, and final presentation.

## 5.1 Docker

- [ ] Dockerfile for Auth Service
- [ ] Dockerfile for Account Service
- [ ] Dockerfile for Transaction Service
- [ ] Dockerfile for API Gateway
- [ ] Dockerfile for frontend
- [ ] PostgreSQL container(s)
- [ ] Docker Compose
- [ ] Environment variable configuration
- [ ] Inter-service networking
- [ ] Startup/dependency configuration
- [ ] Verify full stack with `docker compose up`

## 5.2 Deployment

- [ ] Choose deployment platform
- [ ] Deploy frontend
- [ ] Deploy backend services
- [ ] Deploy PostgreSQL
- [ ] Configure production environment variables
- [ ] Configure production secrets
- [ ] Configure production CORS
- [ ] Test production authentication
- [ ] Test production transfer flow
- [ ] Test production database connectivity

## 5.3 README

Complete and verify:

- [ ] Project overview
- [ ] Features
- [ ] Architecture
- [ ] Service responsibilities
- [ ] Technology stack
- [ ] Database layout
- [ ] Authentication flow
- [ ] Transaction flow
- [ ] Idempotency explanation
- [ ] Concurrency strategy
- [ ] Audit logging
- [ ] API overview
- [ ] Local setup
- [ ] Environment variables
- [ ] Test instructions
- [ ] Swagger/OpenAPI links
- [ ] Screenshots
- [ ] Architecture diagram
- [ ] Future enhancements

## 5.4 API Documentation

- [x] Auth Swagger/OpenAPI
- [x] Account Swagger/OpenAPI
- [x] Transaction Swagger/OpenAPI
- [ ] Add final README links/examples
- [ ] Document Gateway URLs
- [ ] Document representative request/response examples
- [ ] Document error response conventions

## 5.5 Git / Repository Cleanup

- [x] Feature branches used
- [x] Meaningful commits
- [x] Pull requests used
- [x] Frontend branch pushed and PR raised to `develop`
- [x] `.env` contains only public local frontend URL
- [ ] Final `.gitignore` review
- [ ] Secret scan / repository review
- [ ] Remove obsolete files
- [ ] Remove unused dependencies
- [ ] Remove dead code
- [ ] Confirm clean `git status`

## 5.6 Screenshots / Portfolio Assets

- [ ] Login
- [ ] Dashboard
- [ ] Accounts
- [ ] Beneficiaries
- [ ] Transfer result
- [ ] Transaction history
- [ ] Analytics
- [ ] Admin dashboard
- [ ] Swagger
- [ ] Architecture diagram

---

# 6. Optional Advanced Features

> Add these only after the core application and Phase 5 packaging are complete.

## Notifications

- [ ] Email after successful transfer
- [ ] Email after failed transfer
- [ ] Notification history
- [ ] Notification service

## Redis

Potential uses:

- [ ] Caching
- [ ] Rate limiting
- [ ] Temporary data
- [ ] Frequently accessed read data

## Event-Driven Architecture

Potential flow:

```text
Transaction Service
        |
        v
  Transaction Event
      /       \
     v         v
Notification   Analytics
Service        /Reporting
```

Possible technology:

- [ ] Apache Kafka

## Rate Limiting

- [ ] Login attempt limits
- [ ] Transfer request limits
- [ ] Sensitive endpoint limits

## Account Statements

- [ ] Monthly statement
- [ ] CSV export
- [ ] PDF export

## Scheduled Transfers

- [ ] Schedule transfer
- [ ] View scheduled transfers
- [ ] Cancel scheduled transfer
- [ ] Execute scheduled transfer

---

# 7. Features Deliberately Excluded from MVP

Do not add these merely to increase the technology list:

- [ ] Kubernetes
- [ ] Complex cloud infrastructure
- [ ] Large-scale observability stack
- [ ] Multiple messaging systems
- [ ] Multiple caching systems
- [ ] External payment gateway
- [ ] Fraud-detection ML model
- [ ] Complex banking integrations
- [ ] Excessive microservices

> The goal is depth, not technology count.

---

# 8. Current API Surface

## Auth Service

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
GET  /api/auth/me
GET  /api/admin/user-stats
```

## Account Service

```text
POST /api/customers
GET  /api/customers/me

POST /api/accounts
GET  /api/accounts
GET  /api/accounts/{id}

POST   /api/beneficiaries
GET    /api/beneficiaries
GET    /api/beneficiaries/{id}
DELETE /api/beneficiaries/{id}

GET /api/admin/account-stats
```

Internal service endpoints remain service-to-service and are not exposed through the Gateway.

## Transaction Service

```text
POST /api/transactions/transfers
GET  /api/transactions/account/{accountId}
GET  /api/admin/transaction-stats
```

---

# 9. Important End-to-End Scenarios

## Scenario 1 — Registration & Login

```text
Register
   ↓
Login
   ↓
JWT + Refresh Token
   ↓
/api/auth/me
   ↓
Protected dashboard
```

- [x] Verified

## Scenario 2 — Account Creation

```text
Login
   ↓
Create customer profile
   ↓
Create account
   ↓
Account + balance displayed
```

- [x] Verified

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
Generate Idempotency-Key
   ↓
Gateway
   ↓
Transaction Service
   ↓
Account Service
   ↓
Debit sender
   ↓
Credit receiver
   ↓
COMPLETED
   ↓
Transaction + Audit records
```

- [x] Verified through UI

## Scenario 4 — Failed Transfer

```text
Transfer request
      ↓
Validation
      ↓
Business rule failure
      ↓
FAILED
      ↓
No incorrect balance update
      ↓
Failure audited
```

- [x] Verified

## Scenario 5 — Duplicate Request

```text
Request + Idempotency-Key
          ↓
     Transfer executed
          ↓
       Retry same key
          ↓
Existing transaction returned
          ↓
No duplicate debit
```

- [x] Verified

## Scenario 6 — Role-Based Admin Monitoring

```text
ROLE_ADMIN
    ↓
Admin Dashboard
    ↓
Users / Customers / Accounts / Transactions
```

- [x] Verified through UI
- [x] Backend admin APIs protected by role

## Scenario 7 — Frontend Token Refresh

```text
Expired access token
        ↓
401
        ↓
Refresh token
        ↓
New access token
        ↓
Retry original request
```

- [x] Verified

---

# 10. Definition of Done

## Core Application

- [x] Authentication works
- [x] JWT protection works
- [x] USER/ADMIN roles work
- [x] Customer profile works
- [x] Accounts work
- [x] Beneficiaries work
- [x] Transfers work
- [x] Balance validation works
- [x] Duplicate transfers are prevented
- [x] Concurrent transfer behavior is handled
- [x] Transaction lifecycle is stored
- [x] Transaction history works
- [x] Audit logs work
- [x] Admin monitoring works
- [x] Charts display real backend data
- [x] Backend tests pass
- [x] Frontend critical-path tests pass
- [x] Swagger is available
- [x] API Gateway integration works

## Remaining Definition-of-Done Items

- [ ] Docker Compose runs the complete system
- [ ] README fully documents architecture/setup
- [ ] Architecture diagram added
- [ ] Screenshots added
- [ ] Deployment-ready configuration reviewed

---

# 11. Core Interview Topics to Master

## Authentication & Security

- [x] JWT authentication
- [x] Access token vs refresh token
- [x] Password hashing
- [x] Spring Security filters/resource server
- [x] Authentication vs authorization
- [x] RBAC
- [x] Protected endpoints
- [x] Frontend token refresh flow
- [x] Service-to-service secret protection

## Transaction Processing

- [x] Database transactions
- [x] ACID principles
- [x] Balance validation
- [x] Rollback/error behavior
- [x] Transaction statuses
- [x] Failed transactions
- [x] Idempotency
- [x] Duplicate requests
- [x] Audit events

## Concurrency

- [x] Race conditions
- [x] Concurrent transfers
- [x] Pessimistic locking
- [x] Optimistic/version-aware update strategy
- [x] Maintaining correct balances

## Microservices

- [x] Why services were separated
- [x] Service responsibilities
- [x] Service-to-service REST calls
- [x] API Gateway
- [x] Database-per-service ownership
- [x] Security boundaries
- [x] Microservice trade-offs

## REST APIs

- [x] HTTP methods
- [x] HTTP status codes
- [x] DTOs
- [x] Validation
- [x] Error response design
- [x] Swagger/OpenAPI

## Testing

- [x] Unit testing
- [x] Mockito
- [x] Spring Boot tests
- [x] API testing with Postman
- [x] Frontend component testing
- [x] Route testing
- [x] Form testing
- [x] Critical-path testing
- [ ] Testcontainers
- [ ] Full end-to-end automated browser testing

---

# 12. Resume-Focused Outcome

The completed core project demonstrates:

- Secure JWT-based authentication
- Refresh-token based session continuation
- Role-based access control
- Spring Boot microservices
- API Gateway architecture
- PostgreSQL database ownership per service
- Customer and account management
- Beneficiary management
- Financial transfer processing
- Idempotent API design
- Concurrency-safe balance updates
- Transaction history
- Audit logging
- React + TypeScript + MUI
- Recharts analytics
- Admin monitoring
- Backend automated testing
- Frontend critical-path testing
- Swagger/OpenAPI documentation

## Strong Resume-Level Project Description

> **Digital Banking Platform** — Built a secure full-stack banking platform using Java, Spring Boot, React, TypeScript, JWT, PostgreSQL, and microservices, supporting customer/account management, beneficiaries, money transfers, transaction history, audit logging, role-based admin monitoring, and financial analytics. Implemented idempotent transfer processing, concurrency-safe balance updates, service-to-service authentication, automated backend/frontend testing, API Gateway routing, and Swagger/OpenAPI documentation.

---

# 13. Final Priority Order

## Priority 1 — Must Be Excellent

1. Authentication & Spring Security ✅
2. Account management ✅
3. Transfer business logic ✅
4. Balance validation ✅
5. Transaction lifecycle ✅
6. Idempotency ✅
7. Concurrency/consistency ✅
8. REST API quality ✅
9. Testing ✅

## Priority 2 — Completed Core Product

10. React dashboard ✅
11. Admin dashboard ✅
12. Transaction history ✅
13. Audit logging ✅
14. Recharts analytics ✅
15. API Gateway ✅
16. Swagger/OpenAPI ✅

## Priority 3 — Remaining Packaging / Optional Work

17. Docker Compose ⏳
18. Final README + screenshots ⏳
19. Deployment ⏳
20. Redis
21. Notifications
22. Kafka
23. Rate limiting
24. Statements
25. Scheduled transfers
26. GitHub Actions

---

# 14. Golden Rule for This Project

> **Do not add technology unless it solves a real requirement.**

The strongest version of this project is not the one with the most tools.

It is the one where you can confidently explain:

> **“Here is the problem, here is why I designed the system this way, here is how the transaction is processed, here is how I keep the data consistent, here is how I secure it, here is how I test it, and here is how the services interact.”**
