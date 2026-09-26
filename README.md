**# digital-banking-platform**

Full-stack banking platform with secure authentication, microservices, realistic banking workflows, transaction processing, audit logging, reliability handling, and financial analytics.

\> **\*\*Note:\*\*** This project is an educational application and is not intended for production banking or financial transactions.

**## Overview**

Digital Banking Platform is a full-stack banking application built to demonstrate practical software engineering across secure authentication, account management, financial transaction processing, microservice communication, concurrency handling, auditability, and React-based financial dashboards.

The application is designed around clear service boundaries:

\`\`\`text

React + TypeScript :5173

          |

          v

API Gateway :8080

          |

    +-----+-------------+----------------+

    |                   |                |

    v                   v                v

Auth Service       Account Service   Transaction Service

   :8081               :8082              :8083

    |                   |                |

    v                   v                v

 auth_db            account_db       transaction_db

                                      |

                                      v

                                  audit_logs

\`\`\`

**## Key Features**

**### Authentication & Security**

\- User registration and login

\- BCrypt password hashing

\- JWT access tokens

\- Refresh tokens

\- Logout and refresh-token revocation

\- JWT Resource Server validation

\- \`ROLE_USER\` and \`ROLE_ADMIN\`

\- Protected APIs and role-based authorization

\- Frontend protected routes

\- Automatic \`401 → refresh → retry\` flow

\- Service-to-service authentication using an internal service secret

**### Customer & Account Management**

\- Customer profile creation and retrieval

\- Savings and Current account creation

\- Account balance and status

\- Account ownership enforcement

\- User-specific account access

\- Beneficiary creation, viewing, and deletion

\- Duplicate beneficiary protection

\- Own-account beneficiary protection

\- Beneficiary account validation

**### Transaction Engine**

\- Account-to-account transfers

\- Transaction reference generation

\- \`PENDING\`, \`COMPLETED\`, and \`FAILED\` transaction states

\- Insufficient-balance validation

\- Account status validation

\- Same-account transfer prevention

\- Idempotency using \`Idempotency-Key\`

\- Duplicate-request protection

\- Concurrency-safe balance updates

\- Transaction history

\- Ownership-protected transaction history

\- Structured error handling

**### Auditability**

Transfers generate audit records for:

\`\`\`text

TRANSFER_INITIATED

TRANSFER_COMPLETED

TRANSFER_FAILED

\`\`\`

Audit records include relevant information such as:

\- User ID

\- Action

\- Transaction reference

\- Status

\- Message

\- Timestamp

**### Frontend**

\- React + TypeScript

\- Material UI

\- Responsive application layout

\- Login and registration

\- Customer dashboard

\- Account management

\- Beneficiary management

\- Transfer flow

\- Transaction history

\- Recharts-based transaction analytics

\- Admin dashboard

\- Loading, error, and empty states

\- Centralized Axios API client

\- Automatic JWT handling

**### V2 Workflow & Reliability**

- Guided customer onboarding
- Account funding/opening-balance workflow
- Account lifecycle handling and operational restrictions
- Reachable and auditable failed-transaction flow
- Transaction detail, filtering, and pagination capabilities
- Improved service-unavailable and cold-start handling
- Duplicate submission protection for critical operations
- Consistent success, validation, loading, and error feedback

**### Admin Dashboard**

Admin users can view platform-level statistics including:

\- Total users

\- Total customers

\- Total accounts

\- Active accounts

\- Total active balance

\- Total transactions

\- Completed transactions

\- Failed transactions

\- Transaction volume

Admin statistics APIs are protected server-side with \`ROLE_ADMIN\`.

**## Technology Stack**

**### Backend**

\- Java 21

\- Spring Boot

\- Spring Security

\- Spring Security OAuth2 Resource Server

\- JWT

\- Spring Data JPA

\- Hibernate

\- Spring Cloud Gateway

\- REST APIs

\- Bean Validation

\- Maven

\- PostgreSQL

**### Frontend**

\- React

\- TypeScript

\- Vite

\- Axios

\- React Router

\- Material UI

\- Recharts

**### Testing**

\- JUnit 5

\- Mockito

\- Spring Boot Test

\- Postman

\- Vitest

\- React Testing Library

\- \`@testing-library/user-event\`

**## Services**

**### Auth Service — \`:8081\`**

Owns authentication and identity-related functionality.

\`\`\`text

POST /api/auth/register

POST /api/auth/login

POST /api/auth/refresh

POST /api/auth/logout

GET  /api/auth/me

GET  /api/admin/user-stats

\`\`\`

Responsibilities:

\- Users

\- Roles

\- Password hashing

\- JWT access tokens

\- Refresh tokens

\- Logout/revocation

\- Authentication and authorization

**### Account Service — \`:8082\`**

Owns customer, account, balance, and beneficiary data.

\`\`\`text

POST /api/customers

GET  /api/customers/me

POST /api/accounts

GET  /api/accounts

GET  /api/accounts/{id}

POST   /api/beneficiaries

GET    /api/beneficiaries

GET    /api/beneficiaries/{id}

DELETE /api/beneficiaries/{id}

GET /api/admin/account-stats

\`\`\`

Internal service endpoints are kept separate from public Gateway routes.

**### Transaction Service — \`:8083\`**

Owns transaction processing, transaction history, and audit events.

\`\`\`text

POST /api/transactions/transfers

GET  /api/transactions/account/{accountId}

GET  /api/admin/transaction-stats

\`\`\`

The transfer flow is:

\`\`\`text

React

  ↓

API Gateway

  ↓

Transaction Service

  ↓

Account Service

  ↓

Validate + lock accounts

  ↓

Debit source

  ↓

Credit destination

  ↓

Persist transaction status

  ↓

Write audit event

\`\`\`

**### API Gateway — \`:8080\`**

The frontend uses the Gateway as the single backend entry point.

Responsibilities:

\- Service routing

\- Admin route forwarding

\- CORS configuration

\- Browser preflight handling

\- Service endpoint abstraction

Frontend requests should go through:

\`\`\`text

http\://localhost:8080

\`\`\`

rather than directly calling individual backend services.

**## Database Design**

The application uses logically separate PostgreSQL databases:

\`\`\`text

auth_db

account_db

transaction_db

\`\`\`

This follows the database-per-service ownership model.

**### Auth DB**

\`\`\`text

users

roles

refresh_tokens

user_roles

\`\`\`

**### Account DB**

\`\`\`text

customers

accounts

beneficiaries

\`\`\`

**### Transaction DB**

\`\`\`text

transactions

audit_logs

\`\`\`

Services communicate through APIs rather than directly accessing another service's database.

**## Security Model**

**### JWT**

Protected requests use:

\`\`\`http

Authorization: Bearer \<access-token>

\`\`\`

The frontend automatically attaches the access token through Axios.

**### Role-Based Authorization**

\`\`\`text

ROLE_USER

ROLE_ADMIN

\`\`\`

Frontend routing improves the user experience, but the backend remains the actual security boundary.

For example:

\`\`\`text

ROLE_USER

  ↓

/admin/dashboard

  ↓

403 / redirected UI

ROLE_ADMIN

  ↓

/admin/dashboard

  ↓

allowed

\`\`\`

**### Internal Service Authentication**

Account Service internal transfer/ownership endpoints are protected with an internal service secret and are not exposed as normal frontend endpoints.

**## Idempotency**

Transfers require:

\`\`\`http

Idempotency-Key: \<unique-key>

\`\`\`

Example:

\`\`\`text

Request 1

    ↓

Transfer executed

Request 2 with same key

    ↓

Existing transaction returned

    ↓

No duplicate debit

\`\`\`

The frontend generates one unique key for each logical transfer attempt.

**## Concurrency & Balance Consistency**

The transaction flow uses database transaction boundaries and account locking to prevent race conditions during concurrent balance updates.

The key business requirement is:

\`\`\`text

Two simultaneous transfers

        ↓

Both read the same balance

        ↓

Must NOT both spend the same funds

\`\`\`

Account balance updates are therefore protected at the database/service layer.

**## Transaction History**

Users can view account-specific transaction history through:

\`\`\`text

GET /api/transactions/account/{accountId}

\`\`\`

History is:

\- Ownership protected

\- Sorted newest first

\- Available to both sender and receiver for participating accounts

**## Swagger / OpenAPI**

Swagger/OpenAPI is available for the services.

Typical local URLs:

\`\`\`text

Auth Service:

http\://localhost:8081/swagger-ui/index.html

Account Service:

http\://localhost:8082/swagger-ui/index.html

Transaction Service:

http\://localhost:8083/swagger-ui/index.html

\`\`\`

Swagger supports Bearer JWT authorization for protected endpoints.

**## Frontend**

Run the frontend from the \`frontend\` directory.

**### Install**

\`\`\`bash

npm install

\`\`\`

**### Environment**

Create:

\`\`\`text

frontend/.env

\`\`\`

with:

\`\`\`env

VITE_API_BASE_URL=http\://localhost:8080

\`\`\`

No backend secrets should be placed in the frontend environment file.

**### Development**

\`\`\`bash

npm run dev

\`\`\`

Frontend:

\`\`\`text

http\://localhost:5173

\`\`\`

**### Build**

\`\`\`bash

npm run build

\`\`\`

**### Frontend Tests**

\`\`\`bash

npm run test\:run

\`\`\`

**## Backend Setup**

Each service is an independent Maven application.

Typical commands:

\`\`\`bash

mvn clean test

\`\`\`

and:

\`\`\`bash

mvn spring-boot\:run

\`\`\`

Database credentials and service secrets should be supplied through local environment variables or IDE run configurations.

Important backend environment values include:

\`\`\`text

DB_PASSWORD

JWT_SECRET

INTERNAL_SERVICE_SECRET

\`\`\`

Do not commit real secrets to Git.

**## Suggested Local Startup Order**

\`\`\`text

1\. PostgreSQL

2\. Auth Service       :8081

3\. Account Service    :8082

4\. Transaction Service :8083

5\. API Gateway        :8080

6\. React Frontend     :5173

\`\`\`

**## Testing Strategy**

**### Backend**

Automated service-layer tests cover important transaction/business rules such as:

\- Successful transfer

\- Business failures

\- Account Service unavailable

\- Idempotency

\- Missing idempotency key

\- Same source/destination

\- Invalid transfer amount

\- Decimal precision validation

Additional manual/API verification was performed through Postman for:

\- Registration

\- Login

\- Refresh

\- Logout

\- \`/api/auth/me\`

\- Customer/account operations

\- Beneficiaries

\- Transfers

\- Transaction history

\- Audit logging

\- Admin authorization

**### Frontend**

Critical frontend tests cover:

\- Authentication context

\- Protected routes

\- Admin routes

\- Login behavior

\- Transfer form behavior

\- Beneficiary account ID mapping

\- Idempotency key generation

\- Balance validation

\- Dashboard rendering

\- Analytics empty states

\- Failed transaction display

## Current Project Status

```text
Version 1 — Core Banking MVP                         ✅ Complete
Version 2 — Core Workflow Refinement & Reliability   ✅ Complete

Current integration branch: develop                  ✅ Validated
Vercel develop deployment                            ✅ Running
Production release branch: main                      ⏳ Release/verification
```

Version 2 is considered complete after implementing and validating the planned core workflow refinements, frontend/backend regression coverage, and local/deployed validation. The `develop` branch is the validated V2 integration state. The existing Render/Vercel deployment is retained as a portfolio/demo environment, while localhost/Docker remains the primary development and validation environment.

**## Git Workflow**

The project uses feature branches and pull requests.

Typical flow:

\`\`\`text

feature/\<work>

      ↓ PR

develop

      ↓ PR

main

\`\`\`

Major completed work was developed through feature branches and merged through pull requests.

The Docker/deployment work followed the same process and was merged into \`develop\` before the final production deployment.

**## Screenshots**

**### 1. User Registration**

User sign-up screen with username, email, and password fields under the SecureBank portal.

![User Registration]\(image.png)

**---**

**### 2. User Sign In**

Authentication screen for existing users to log in securely using credentials.

![User Sign In]\(image-1.png)

**---**

**### 3. Customer Dashboard**

Overview screen showing total balance, transaction summary, active accounts, transaction analytics charts, and recent activity.

![Customer Dashboard]\(image-2.png)

**---**

**### 4. Account Management**

View of registered user accounts showing account types (Savings/Current), active status, and available balances with options to open new accounts.

![Account Management]\(image-3.png)

**---**

**### 5. Beneficiary Management**

List of saved payees/beneficiaries with account numbers, creation timestamps, and management options.

![Beneficiary Management]\(image-4.png)

**---**

**### 6. Transfer Flow & Validation**

Money transfer interface showing source account selection, beneficiary destination, and client-side balance validation handling insufficient funds.

![Transfer Flow]\(image-5.png)

**---**

**### 7. Transaction History**

Detailed, sortable list of account-level transfers displaying references, transaction types, descriptions, amounts, and statuses.

![Transaction History]\(image-6.png)

\*\*## Version 2 — Core Workflow Refinement ✅

Version 2 focused on turning the original MVP into a more realistic end-to-end banking workflow while preserving the existing security, transaction, and microservice architecture.

### V2 workflow improvements

- Account funding/opening-balance workflow so normal demos no longer depend on manual SQL balance edits
- Realistic and reachable failed-transaction handling with persisted failure state and auditability
- Guided first-time customer onboarding for incomplete profile/account states
- Meaningful account lifecycle handling, including operational restrictions for non-active accounts
- Improved transaction history with richer details, filtering/pagination capabilities, and user-facing detail views
- Improved backend-unavailable, timeout, and cold-start UX
- Duplicate-submission protection and safe handling of financial write retries
- Banking UI/product polish across funding, onboarding, transaction details, account status, validation, feedback, and responsive layouts
- Expanded frontend and backend regression testing

### V2 validation

Every major V2 workflow was validated across the relevant layers:

```text
Database state
      ↕
Backend APIs / services
      ↕
API Gateway
      ↕
React + TypeScript UI
      ↕
Postman / automated tests
```

Validation included the normal customer flow, failure scenarios, account/balance consistency, authorization boundaries, transaction behavior, and regression testing of existing functionality.

### V2 Definition of Done

The completed V2 workflow supports the intended progression:

```text
Register
   ↓
Login
   ↓
Guided profile setup
   ↓
Create account
   ↓
Fund account without SQL
   ↓
Add beneficiary
   ↓
Transfer money
   ↓
View updated balance
   ↓
View transaction history/details
```

The V2 release also covers:

- Legitimate failed-transaction scenarios
- Failure auditing and balance consistency
- Account lifecycle restrictions
- Service-unavailable/recovery behavior
- Duplicate-submission protection
- Frontend/backend regression coverage
- Local and deployed validation

## Testing & Quality Validation

### Backend

Backend tests cover authentication, authorization, account/customer behavior, beneficiary rules, transfer processing, idempotency, concurrency, transaction failure handling, audit logging, controller behavior, internal-service protection, and gateway configuration.

Typical validation command:

```bash
mvn clean test
```

### Frontend

Frontend tests use:

- Vitest
- React Testing Library
- `@testing-library/user-event`

Coverage includes:

- AuthContext
- Login/Register flows and error states
- Protected/Admin routes
- Dashboard and analytics states
- Accounts and funding workflows
- Beneficiary management
- Transfer validation and idempotency behavior
- Transaction history/error states
- Axios authentication refresh/retry logic
- API clients
- Common UI components and utilities

Commands:

```bash
npm run test:run
npm run build
```

The `develop` branch has been validated with the frontend/backend test and build workflow and is currently running through the Vercel `develop` deployment.

## V2 Git Workflow

```text
feature/<work>
      ↓ PR
   develop
      ↓ release PR
    main
```

V2 feature work was completed through focused branches and merged into `develop`. `develop` is the integration/validation branch, while `main` remains the release branch.

## Version 3 — Future Roadmap

Version 3 should build on the stable V2 core rather than replacing it with additional infrastructure for its own sake.

### Potential V3 areas

#### 1. Production-grade CI/CD

- GitHub Actions for frontend/backend build and test pipelines
- Pull-request quality gates
- Automated release/deployment workflow
- Dependency/security scanning

#### 2. Stronger observability

- Structured application logging
- Correlation/request IDs across services
- Metrics and health/readiness endpoints
- Centralized error monitoring
- Transfer and downstream-service latency visibility

#### 3. Notifications

- Transfer success/failure notifications
- Email notifications
- Notification history
- Notification preferences

#### 4. Statements & exports

- Monthly account statements
- PDF/CSV export
- Date-range statement generation
- Downloadable transaction summaries

#### 5. Scheduled & recurring transfers

- Future-dated transfers
- Recurring transfers
- Scheduling validation
- Retry/idempotency rules for scheduled operations

#### 6. More realistic banking operations

- Withdrawal/debit workflow
- Deposit/withdrawal transaction types
- Account limits and configurable transfer limits
- Beneficiary activation/deactivation
- More detailed transaction receipts

#### 7. Performance & scalability

- Redis where caching or rate limiting has a demonstrated requirement
- Rate limiting for authentication and sensitive APIs
- Database/index tuning
- Load and concurrency testing
- Read-performance improvements for transaction history

#### 8. Event-driven architecture

Introduce Kafka or another event mechanism only where asynchronous processing provides a real benefit, for example:

```text
Transaction Service
        ↓
   Transaction Event
      ↙       ↘
Notifications   Analytics / Reporting
```

#### 9. Advanced automated testing

- Testcontainers for realistic PostgreSQL integration tests
- Full browser end-to-end tests
- Contract testing between services
- Performance/load test automation

### V3 principle

V3 should follow the same project rule:

> **Do not add technology unless it solves a real requirement.**

The objective is to improve production-readiness, observability, reliability, scalability, and user capability without weakening the clarity of the existing service boundaries.

## Project Principle\*\*

The value of this project is in being able to explain:

\> **\*\*Why the services are separated, how authentication works, how a transfer is processed, how duplicate and concurrent requests are handled, how balances remain consistent, how authorization is enforced, how activity is audited, how the frontend integrates with the Gateway, and how the system is tested.\*\***

**---**

**\*\*Educational Disclaimer\*\***

This project is an educational application and is not intended for production banking or financial transactions.
