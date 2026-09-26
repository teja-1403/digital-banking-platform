**\*\*# Digital Banking Platform — TODO\*\***

\\> A secure, modular retail-banking platform demonstrating end-to-end full-stack engineering, microservice architecture, JWT security, transaction processing, idempotency, concurrency handling, auditability, React/TypeScript development, analytics, and automated testing.

**\*\*---\*\***

**\*\*# 0. Project Status\*\***

**\*\*## Current Overall State\*\***

\\\`\\\`\\\`text

Phase 1 — Authentication & Security              ✅ Complete

Phase 2 — Account Management                       ✅ Complete

Phase 3 — Transaction Engine                       ✅ Complete

Phase 4 — Frontend Integration                     ✅ Complete

Phase 5 — Docker / Deployment / Final Docs         ✅ Complete for V1

Phase 6 — V2 Core Workflow Refinement ✅ Complete

\\\`\\\`\\\`

V1 is complete and deployed. The production frontend is on Vercel and the backend/services are deployed on Render. V2 implementation and regression work is now complete and has been merged into `develop`. The `develop` deployment on Vercel is running successfully. Render remains a V1 reference/demo environment while available. The remaining release/documentation work is the final README refresh and promotion of the validated `develop` state to `main`.

**\*\*---\*\***

**\*\*# V2 Planning Notes\*\***

- V2 implementation and regression work are complete and merged into `develop`.

\- V1 is the completed baseline.

\- V2 development will use localhost/Docker Compose as the primary environment.

\- Render/Vercel remains a V1 reference/demo deployment while it is available.

\- Every V2 feature will be validated through **\*\*Database + UI + Postman\*\***.

\- No manual SQL balance manipulation should be required for the normal V2 customer demo flow.

\- \`README.md\` will be updated with the final V2 state **\*\*after V2 is completed\*\***.

\- V2 will prioritize core banking workflow quality before optional technologies such as Kafka, Redis, Kubernetes, or additional services.

**\*\*---\*\***

**\*\*# 1. Project Definition\*\***

**\*\*## Project Name\*\***

**\*\*\\\*\\\*Digital Banking Platform\\\*\\\*\*\***

**\*\*## One-Line Description\*\***

A secure digital banking platform where customers can register, manage customer profiles and bank accounts, manage beneficiaries, transfer money, view transaction history, analyze financial activity, while administrators can monitor platform-wide statistics.

**\*\*## Primary Goal\*\***

Demonstrate:

\\- Java + Spring Boot backend development

\\- React + TypeScript frontend development

\\- JWT authentication and authorization

\\- Role-based access control

\\- RESTful API design

\\- Microservice architecture

\\- PostgreSQL database-per-service ownership

\\- Financial transaction processing

\\- Idempotent operations

\\- Concurrency-safe balance updates

\\- Audit logging

\\- Data visualization

\\- Automated backend and frontend testing

\\- API Gateway integration

\\- Swagger/OpenAPI documentation

**\*\*## Project Philosophy\*\***

Focus on:

\\- Correctness

\\- Business logic

\\- Security

\\- API design

\\- Transaction consistency

\\- Error handling

\\- Testing

\\- Clear service boundaries

Avoid:

\\- Unnecessary technologies

\\- Overengineering

\\- Complex visual effects

\\- Technology for technology's sake

\\> **\*\*\\\*\\\*Principle:\\\*\\\*\*\*** Build a banking system whose important functionality can be explained deeply instead of a large system with shallow features.

**\*\*---\*\***

**\*\*# 2. Technology Stack\*\***

**\*\*## Backend\*\***

\\- [x] Java 21

\\- [x] Spring Boot

\\- [x] Spring Security

\\- [x] JWT

\\- [x] Spring Security OAuth2 Resource Server

\\- [x] Spring Data JPA

\\- [x] Hibernate

\\- [x] Spring Cloud Gateway

\\- [x] RESTful APIs

\\- [x] Bean Validation

\\- [x] Maven

\\- [x] RestClient for service-to-service communication

**\*\*## Frontend\*\***

\\- [x] React

\\- [x] TypeScript

\\- [x] Vite

\\- [x] Axios

\\- [x] React Router

\\- [x] Material UI (MUI)

\\- [x] Recharts

\\- [x] Responsive application layout

**\*\*## Database\*\***

\\- [x] PostgreSQL

\\- [x] pgAdmin

Logical databases:

\\\`\\\`\\\`text

auth\\\_db

account\\\_db

transaction\\\_db

\\\`\\\`\\\`

**\*\*## Testing\*\***

\\- [x] JUnit 5

\\- [x] Mockito

\\- [x] Spring Boot Test

\\- [x] Postman for API validation

\\- [x] Vitest

\\- [x] React Testing Library

\\- [x] @testing-library/user-event

\\- [ ] Testcontainers

**\*\*## DevOps / Tooling\*\***

\\- [x] Git

\\- [x] GitHub

\\- [x] Feature branches

\\- [x] Pull requests

\\- [x] Environment variable configuration

\\- [x] Docker

\\- [x] Docker Compose

\\- [ ] GitHub Actions

**\*\*## Documentation\*\***

\\- [x] Swagger / OpenAPI

\\- [x] README

\\- [ ] Final architecture diagram

\\- [x] Postman/API validation documentation

\\- [ ] Final setup/deployment documentation

**\*\*---\*\***

**\*\*# 3. Current Architecture\*\***

\\\`\\\`\\\`text

                         React + TypeScript

                              :5173

                                |

                                v

                         API Gateway

                              :8080

                                |

             +------------------+------------------+

             |                  |                  |

             v                  v                  v

       Auth Service       Account Service     Transaction Service

           :8081              :8082                :8083

             |                  |                    |

             v                  v                    v

          auth\\\_db           account\\\_db         transaction\\\_db

                                                 |

                                                 v

                                             audit\\\_logs

\\\`\\\`\\\`

**\*\*## Service Responsibilities\*\***

**\*\*### Auth Service\*\***

\\- [x] User registration

\\- [x] Password hashing with BCrypt

\\- [x] Login

\\- [x] JWT access tokens

\\- [x] Refresh tokens

\\- [x] Logout / refresh-token revocation

\\- [x] JWT Resource Server validation

\\- [x] \\\`ROLE\\\_USER\\\`

\\- [x] \\\`ROLE\\\_ADMIN\\\`

\\- [x] \\\`/api/auth/me\\\`

\\- [x] Admin statistics API

\\- [x] Authentication/authorization error handling

\\- [x] Swagger/OpenAPI

**\*\*### Account Service\*\***

\\- [x] Customer profile

\\- [x] Bank account creation

\\- [x] Savings accounts

\\- [x] Current accounts

\\- [x] Account status

\\- [x] Balance ownership

\\- [x] Account ownership enforcement

\\- [x] Beneficiary management

\\- [x] Internal transfer endpoint

\\- [x] Internal service-secret protection

\\- [x] Admin account statistics API

\\- [x] JWT Resource Server validation

\\- [x] Swagger/OpenAPI

\\- [x] Service-layer automated tests

**\*\*### Transaction Service\*\***

\\- [x] Transfer initiation

\\- [x] Transaction references

\\- [x] PENDING / COMPLETED / FAILED lifecycle

\\- [x] Account Service integration

\\- [x] Internal service authentication

\\- [x] Idempotency

\\- [x] Concurrent-request protection

\\- [x] Transaction history

\\- [x] Audit logging

\\- [x] Business vs infrastructure error handling

\\- [x] Admin transaction statistics API

\\- [x] JWT Resource Server validation

\\- [x] Swagger/OpenAPI

\\- [x] Automated service tests

**\*\*### API Gateway\*\***

\\- [x] Single entry point for frontend

\\- [x] Auth routing

\\- [x] Account/customer/beneficiary routing

\\- [x] Transaction routing

\\- [x] Admin routing

\\- [x] CORS configuration

\\- [x] Browser preflight handling

**\*\*---\*\***

**\*\*# 4. Core Architecture Principles\*\***

\\- [x] Each service owns its own data

\\- [x] No direct cross-service database access

\\- [x] Services communicate through APIs

\\- [x] Frontend communicates through API Gateway

\\- [x] DTOs are used instead of exposing entities directly

\\- [x] Business logic resides in services

\\- [x] API validation is enforced at boundaries

\\- [x] Ownership checks are enforced server-side

\\- [x] Service-to-service endpoints use an internal secret

\\- [x] Financial transfers are idempotent

\\- [x] Balance updates are concurrency-safe

\\- [x] Audit events are persisted

**\*\*---\*\***

**\*\*# PHASE 1 — AUTHENTICATION & SECURITY ✅\*\***

**\*\*## 1.1 Authentication\*\***

\\- [x] Registration

\\- [x] Login

\\- [x] BCrypt password hashing

\\- [x] JWT access token

\\- [x] Refresh token

\\- [x] Logout

\\- [x] Refresh-token revocation

\\- [x] JWT validation

\\- [x] Role-based authorization

\\- [x] Protected endpoints

\\- [x] \\\`/api/auth/me\\\`

\\- [x] Authentication error handling

\\- [x] Authorization error handling

**\*\*### Implemented APIs\*\***

\\\`\\\`\\\`text

POST /api/auth/register

POST /api/auth/login

POST /api/auth/refresh

POST /api/auth/logout

GET  /api/auth/me

\\\`\\\`\\\`

**\*\*## 1.2 Roles\*\***

\\\`\\\`\\\`text

ROLE\\\_USER

ROLE\\\_ADMIN

\\\`\\\`\\\`

**\*\*## 1.3 Security Validation\*\***

\\- [x] Request without token → 401

\\- [x] Invalid JWT → 401

\\- [x] Valid JWT → protected resource access

\\- [x] User-only access

\\- [x] Admin-only access

\\- [x] Fresh JWT required after role changes

**\*\*## 1.4 Swagger\*\***

\\- [x] Swagger UI

\\- [x] OpenAPI specification

\\- [x] Bearer JWT Authorize button

\\- [x] Swagger endpoints permitted in security configuration

**\*\*### Phase 1 Exit Criteria\*\***

\\- [x] User can register

\\- [x] User can log in

\\- [x] JWT is issued and validated

\\- [x] Refresh flow works

\\- [x] Logout/revocation works

\\- [x] USER/ADMIN access rules work

\\- [x] Auth endpoints documented

**\*\*---\*\***

**\*\*# PHASE 2 — ACCOUNT MANAGEMENT ✅\*\***

**\*\*## 2.1 Customer Profile\*\***

\\- [x] Create customer profile

\\- [x] View current customer

\\- [x] Ownership enforced through authenticated user ID

APIs:

\\\`\\\`\\\`text

POST /api/customers

GET  /api/customers/me

\\\`\\\`\\\`

**\*\*## 2.2 Account Management\*\***

\\- [x] Create account

\\- [x] View accounts

\\- [x] View account details

\\- [x] View balance

\\- [x] View status

\\- [x] Savings account

\\- [x] Current account

\\- [x] Ownership enforcement

\\- [x] Prevent unauthorized access

\\- [x] Prevent invalid account use in transfers

**\*\*## 2.3 Beneficiary Management\*\***

\\- [x] Add beneficiary

\\- [x] View beneficiaries

\\- [x] View beneficiary

\\- [x] Delete beneficiary

\\- [x] Validate beneficiary account

\\- [x] Prevent duplicate beneficiary

\\- [x] Prevent adding own account

\\- [x] Prevent inactive beneficiary account

\\- [x] Expose actual beneficiary account ID to frontend

APIs:

\\\`\\\`\\\`text

GET    /api/beneficiaries

POST   /api/beneficiaries

GET    /api/beneficiaries/{id}

DELETE /api/beneficiaries/{id}

\\\`\\\`\\\`

**\*\*## 2.4 Account Security\*\***

\\- [x] JWT Resource Server

\\- [x] User-to-account ownership enforcement

\\- [x] Internal Account Service endpoint

\\- [x] Internal service-secret protection

**\*\*## 2.5 Account Service Testing\*\***

\\- [x] Customer service tests

\\- [x] Account service tests

\\- [x] Beneficiary service tests

\\- [x] Spring context test

\\- [x] Maven test/build validation

**\*\*## 2.6 Account UI\*\***

\\- [x] Accounts page

\\- [x] Customer profile creation UI

\\- [x] Savings creation UI

\\- [x] Current account creation UI

\\- [x] Account summary

\\- [x] Account balance display

\\- [x] Account status display

\\- [x] Loading/error/empty states

**\*\*### Phase 2 Exit Criteria\*\***

\\- [x] Customer profile works

\\- [x] Customers can create/view accounts

\\- [x] Account ownership works

\\- [x] Beneficiaries work

\\- [x] Account APIs work through Gateway

\\- [x] Account Swagger available

\\- [x] Account automated service tests pass

**\*\*---\*\***

**\*\*# PHASE 3 — TRANSACTION ENGINE ✅\*\***

\\> **\*\*\\\*\\\*Most important engineering phase.\\\*\\\*\*\***

**\*\*## 3.1 Money Transfer\*\***

\\- [x] Account-to-account transfer

\\- [x] Source account validation

\\- [x] Destination account validation

\\- [x] Account status validation

\\- [x] Sender != receiver validation

\\- [x] Amount > 0 validation

\\- [x] Two-decimal amount validation

\\- [x] Sufficient-balance validation

\\- [x] Transaction reference generation

\\- [x] PENDING state

\\- [x] COMPLETED state

\\- [x] FAILED state

\\- [x] Completion timestamp

\\- [x] Balance debit/credit

API:

\\\`\\\`\\\`text

POST /api/transactions/transfers

\\\`\\\`\\\`

**\*\*## 3.2 Idempotency\*\***

\\- [x] \\\`Idempotency-Key\\\` header

\\- [x] Unique database constraint

\\- [x] Fast-path existing transaction lookup

\\- [x] Concurrent duplicate protection

\\- [x] Existing result returned for repeated requests

Expected:

\\\`\\\`\\\`text

Request 1 -> transfer executed

Request 2 -> existing transaction returned

\\\`\\\`\\\`

**\*\*## 3.3 Concurrency & Consistency\*\***

\\- [x] Database transaction boundaries

\\- [x] Pessimistic locking for balance updates

\\- [x] Concurrent transfer protection

\\- [x] Balance consistency

\\- [x] Failure handling

\\- [x] Idempotency race handling

**\*\*## 3.4 Transaction History\*\***

\\- [x] Account history

\\- [x] Ownership enforcement

\\- [x] Sender sees transactions

\\- [x] Receiver sees transactions

\\- [x] Newest-first sorting

API:

\\\`\\\`\\\`text

GET /api/transactions/account/{accountId}

\\\`\\\`\\\`

Not currently implemented:

\\- [ ] Pagination

\\- [ ] Search

\\- [ ] Advanced date filtering

\\- [ ] Advanced type/status filtering

**\*\*## 3.5 Audit Logging\*\***

\\- [x] \\\`TRANSFER\\\_INITIATED\\\`

\\- [x] \\\`TRANSFER\\\_COMPLETED\\\`

\\- [x] \\\`TRANSFER\\\_FAILED\\\`

\\- [x] User ID recorded

\\- [x] Transaction reference recorded

\\- [x] Status recorded

\\- [x] Message recorded

\\- [x] Timestamp recorded

\\- [x] Audit data persisted in \\\`audit\\\_logs\\\`

**\*\*## 3.6 Error Handling\*\***

\\- [x] Global exception handling

\\- [x] Validation errors

\\- [x] Authentication errors

\\- [x] Authorization errors

\\- [x] Business rule violations

\\- [x] Resource-not-found errors

\\- [x] Account Service business failures

\\- [x] Account Service unavailable → 503

\\- [x] Consistent error responses

**\*\*## 3.7 Transaction Service Testing\*\***

\\- [x] Successful transfer

\\- [x] Business failure

\\- [x] Account Service unavailable

\\- [x] Idempotency

\\- [x] Missing idempotency key

\\- [x] Same-account validation

\\- [x] Zero amount

\\- [x] Invalid decimal precision

\\- [x] Spring context test

\\- [x] Maven package/test validation

**\*\*## 3.8 Gateway Integration\*\***

\\- [x] Transaction routes through Gateway

\\- [x] Auth/Account/Transaction services accessible via \\\`:8080\\\`

\\- [x] Frontend uses Gateway only

\\- [x] CORS configuration

\\- [x] OPTIONS preflight support

**\*\*### Phase 3 Exit Criteria\*\***

\\- [x] Successful transfer works

\\- [x] Invalid transfers rejected

\\- [x] Insufficient funds rejected

\\- [x] Duplicate transfer requests prevented

\\- [x] Concurrent transfer behavior handled

\\- [x] Transaction history works

\\- [x] Audit logs generated

\\- [x] Errors are structured

\\- [x] Gateway integration works

\\- [x] Swagger documents Transaction Service

**\*\*---\*\***

**\*\*# PHASE 4 — FRONTEND INTEGRATION, DASHBOARD, ADMIN & TESTING ✅\*\***

**\*\*## 4.1 Frontend Foundation\*\***

\\- [x] React + TypeScript + Vite

\\- [x] MUI

\\- [x] React Router

\\- [x] Axios

\\- [x] Environment configuration

\\- [x] MUI theme

\\- [x] Application folder structure

\\- [x] API client

\\- [x] Protected route structure

**\*\*## 4.2 Frontend Authentication\*\***

\\- [x] Register UI

\\- [x] Login UI

\\- [x] Logout UI

\\- [x] AuthContext

\\- [x] JWT persistence

\\- [x] \\\`/api/auth/me\\\` integration

\\- [x] Access token storage

\\- [x] Refresh token storage

\\- [x] 401 → refresh → retry Axios interceptor

\\- [x] Failed refresh → login redirect

\\- [x] ProtectedRoute

\\- [x] AdminRoute

\\- [x] ROLE\\\_USER UI behavior

\\- [x] ROLE\\\_ADMIN UI behavior

**\*\*## 4.3 Customer Dashboard & Accounts\*\***

\\- [x] Responsive MUI application layout

\\- [x] Sidebar navigation

\\- [x] Mobile navigation drawer

\\- [x] Active navigation state

\\- [x] Customer profile creation

\\- [x] Account creation

\\- [x] Savings/current account selection

\\- [x] Account summary

\\- [x] Balance display

\\- [x] Account status display

\\- [x] Loading states

\\- [x] Error states

\\- [x] Empty states

**\*\*## 4.4 Beneficiary UI\*\***

\\- [x] Beneficiary list

\\- [x] Add beneficiary

\\- [x] Duplicate handling

\\- [x] Own-account rejection

\\- [x] Invalid-account handling

\\- [x] Delete confirmation dialog

\\- [x] Delete operation

\\- [x] User ownership isolation

\\- [x] API error messages

**\*\*## 4.5 Transfer UI\*\***

\\- [x] Source account selector

\\- [x] Beneficiary selector

\\- [x] Amount input

\\- [x] Description input

\\- [x] Client-side amount validation

\\- [x] Real beneficiary account ID integration

\\- [x] Unique idempotency key per logical transfer attempt

\\- [x] Transfer result UI

\\- [x] Success state

\\- [x] Failure state

\\- [x] Updated account balances after transfer

**\*\*## 4.6 Transaction History UI\*\***

\\- [x] Account selector

\\- [x] Transaction list

\\- [x] Newest-first display

\\- [x] Sender transaction visibility

\\- [x] Receiver transaction visibility

\\- [x] Status chips

\\- [x] Empty history state

\\- [x] Error/loading states

**\*\*## 4.7 Dashboard Analytics\*\***

\\- [x] Total balance

\\- [x] Total transactions

\\- [x] Completed transaction count

\\- [x] Failed transaction count

\\- [x] Transaction volume

\\- [x] Recent transactions

\\- [x] Credits/debits analytics

\\- [x] Recharts integration

\\- [x] Empty chart state

\\- [x] Real backend data

**\*\*## 4.8 Admin Dashboard\*\***

\\- [x] Admin-only route

\\- [x] Admin user statistics

\\- [x] Customer statistics

\\- [x] Account statistics

\\- [x] Active account count

\\- [x] Total active balance

\\- [x] Transaction statistics

\\- [x] Completed transaction count

\\- [x] Failed transaction count

\\- [x] Transaction volume

\\- [x] Backend \\\`ROLE\\\_ADMIN\\\` enforcement

Admin APIs:

\\\`\\\`\\\`text

GET /api/admin/user-stats

GET /api/admin/account-stats

GET /api/admin/transaction-stats

\\\`\\\`\\\`

**\*\*## 4.9 Frontend Polish\*\***

\\- [x] Responsive layout

\\- [x] Active navigation

\\- [x] MUI confirmation dialog

\\- [x] Consistent loading states

\\- [x] Consistent error states

\\- [x] Informative empty states

\\- [x] Centralized API error message helper

\\- [x] Transfer form reset after success

\\- [x] Admin navigation visible only to admins

\\- [x] Favicon reference cleanup

**\*\*## 4.10 Frontend Automated Tests\*\***

Testing stack:

\\\`\\\`\\\`text

Vitest

React Testing Library

@testing-library/user-event

\\\`\\\`\\\`

Critical tests implemented:

\\- [x] AuthContext tests

\\- [x] ProtectedRoute tests

\\- [x] AdminRoute tests

\\- [x] Login tests

\\- [x] Transfer tests

\\- [x] Dashboard tests

\\- [x] Transfer idempotency-key behavior

\\- [x] Transfer beneficiary-account-ID mapping

\\- [x] Role-based route behavior

\\- [x] Dashboard rendering/analytics states

**\*\*## 4.11 Frontend Build\*\***

\\- [x] \\\`npm run build\\\`

\\- [x] \\\`npm run test\\\:run\\\`

**\*\*### Phase 4 Exit Criteria\*\***

\\- [x] Customer dashboard works

\\- [x] Accounts work

\\- [x] Beneficiaries work

\\- [x] Transfers work end-to-end

\\- [x] Transaction history works

\\- [x] Charts use backend data

\\- [x] Admin dashboard works

\\- [x] RBAC works in practice

\\- [x] Critical frontend tests pass

\\- [x] Production frontend build passes

\\- [x] Frontend integrated through API Gateway

**\*\*---\*\***

**\*\*# PHASE 5 — DOCKER, DEPLOYMENT & FINAL DOCUMENTATION ✅\*\***

\> V1 packaging, deployment, and deployment validation are complete. The deployed Render/Vercel environment is retained as a V1 reference/demo environment, but V2 development and validation will primarily use localhost because the Render free services/database may expire or become unavailable soon.

**\*\*## 5.1 Docker\*\***

\- [x] Dockerfile for Auth Service

\- [x] Dockerfile for Account Service

\- [x] Dockerfile for Transaction Service

\- [x] Dockerfile for API Gateway

\- [x] Dockerfile for frontend

\- [x] PostgreSQL container(s)

\- [x] Docker Compose

\- [x] Environment variable configuration

\- [x] Inter-service networking

\- [x] Startup/dependency configuration

\- [x] Verify full stack with \`docker compose up\`

\- [x] Host-built JAR + runtime-image deployment workaround used where required

**\*\*## 5.2 V1 Deployment\*\***

\- [x] Deploy frontend on Vercel

\- [x] Deploy backend services on Render

\- [x] Deploy PostgreSQL databases on Render

\- [x] Configure production environment variables

\- [x] Configure production secrets

\- [x] Configure production CORS

\- [x] Test production authentication

\- [x] Test production transfer flow

\- [x] Test production database connectivity

\- [x] Verify frontend → Gateway → backend service flow

\- [x] Verify browser SPA routing on Vercel

**\*\*### V1 Deployment Environment\*\***

\`\`\`text

Frontend

Vercel

API Gateway

Render

Auth Service

Render

Account Service

Render

Transaction Service

Render

PostgreSQL

Render

\`\`\`

\> Deployment caveat: Render free web services can sleep after inactivity, and free database availability is temporary. Do not treat the Render environment as the primary V2 development dependency.

**\*\*## 5.3 README\*\***

**\*\*### V1 README baseline\*\***

\- [x] Project overview

\- [x] Features

\- [x] Architecture

\- [x] Service responsibilities

\- [x] Technology stack

\- [x] Database layout

\- [x] Authentication flow

\- [x] Transaction flow

\- [x] Idempotency explanation

\- [x] Concurrency strategy

\- [x] Audit logging

\- [x] API overview

\- [x] Local setup

\- [x] Environment variables

\- [x] Test instructions

\- [x] Swagger/OpenAPI links

\- [x] Screenshots

\- [x] Architecture diagram

\- [x] Future enhancements

**\*\*### V2 documentation rule\*\***

\- [ ] Update \`README.md\` after V2 completion

\- [ ] Add V2 features and changed APIs

\- [ ] Add final V2 workflow diagrams

\- [ ] Add V2 database/schema changes

\- [ ] Add V2 local validation instructions

\- [ ] Update screenshots after V2 UI changes

**\*\*## 5.4 API Documentation\*\***

\- [x] Auth Swagger/OpenAPI

\- [x] Account Swagger/OpenAPI

\- [x] Transaction Swagger/OpenAPI

\- [x] README API links/examples for V1

\- [x] Gateway URLs documented

\- [x] Representative request/response examples documented

\- [x] Error response conventions documented

\- [ ] Update Swagger/OpenAPI for V2 APIs

**\*\*## 5.5 Git / Repository Cleanup\*\***

\- [x] Feature branches used

\- [x] Meaningful commits

\- [x] Pull requests used

\- [x] Frontend branch pushed and PR raised to \`develop\`

\- [x] \`.env\` contains only public local frontend URL/configuration

\- [ ] Final \`.gitignore\` review

\- [ ] Secret scan / repository review

\- [ ] Remove obsolete files

\- [ ] Remove unused dependencies

\- [ ] Remove dead code

\- [ ] Confirm clean \`git status\`

\- [ ] Confirm \`develop\` is the V2 integration branch

\- [ ] Confirm \`main\` remains deployable before/after V2 merge

**\*\*## 5.6 Screenshots / Portfolio Assets\*\***

\- [x] Login

\- [x] Dashboard

\- [x] Accounts

\- [x] Beneficiaries

\- [x] Transfer result

\- [x] Transaction history

\- [x] Analytics

\- [x] Admin dashboard

\- [x] Swagger

\- [x] Architecture diagram

\- [ ] Capture updated V2 screenshots after V2 UI work

**\*\*---\*\***

**\*\*# PHASE 6 — V2 CORE WORKFLOW REFINEMENT ⏳\*\***

\> **\*\*V2 goal:\*\*** turn the current technically strong MVP into a more realistic, end-to-end banking workflow that can be demonstrated and validated without manually editing PostgreSQL data.

\>

\> **\*\*Primary validation rule for V2:\*\*** every important change must be cross-checked through **\*\*Database + UI + Postman\*\***. Localhost is the primary environment for this validation.

**\*\*## 6.0 V2 Baseline — Localhost Development Environment\*\***

**\*\*### Objective\*\***

Move the active development/testing workflow away from the Render free environment and ensure the website points to the local API Gateway.

**\*\*### Git branch\*\***

\`chore/v2-local-dev-baseline\`

**\*\*### Backend / Infrastructure\*\***

\- [x] Start PostgreSQL through Docker Compose

\- [x] Start Auth Service on \`:8081\`

\- [x] Start Account Service on \`:8082\`

\- [x] Start Transaction Service on \`:8083\`

\- [x] Start API Gateway on \`:8080\`

\- [x] Verify all services use local PostgreSQL databases

\- [x] Verify service-to-service URLs are localhost-based

\- [x] Verify internal service secret configuration is local

\- [x] Verify local CORS includes \`http\://localhost:5173\`

\- [x] Keep Render URLs out of normal V2 local application flow

**\*\*### Frontend\*\***

\- [x] Set local frontend API base URL to \`http\://localhost:8080\`

\- [x] Verify Vite environment configuration uses localhost in development

\- [x] Verify Axios calls go only to API Gateway

\- [x] Verify login/register work from localhost

\- [x] Verify browser refresh/direct route navigation works locally

\- [x] Verify no frontend API call bypasses the Gateway

**\*\*### Database baseline\*\***

\- [x] Confirm \`auth_db\`, \`account_db\`, and \`transaction_db\` are local

\- [x] Confirm schema/table creation succeeds from a clean startup

\- [x] Confirm V2 testing does not require manual production/Render SQL edits

\- [x] Preserve Render only as a separate V1 reference/demo environment while it remains available

**\*\*### Validation\*\***

\- [x] UI smoke test

\- [x] Postman auth smoke test

\- [x] Postman account smoke test

\- [x] Postman transaction smoke test

\- [x] DB connectivity verification

\- [x] Capture any configuration differences before starting feature work

**\*\*### Exit criteria\*\***

\- [x] Full local stack starts successfully

\- [x] Frontend is using \`localhost:8080\` Gateway

\- [x] Login works through localhost

\- [x] Existing V1 transfer flow still works locally

\- [x] No feature work begins until local baseline is stable

**\*\*---\*\***

**\*\*## 6.1 Account Funding / Opening Balance ✅\*\***

**\*\*### Objective\*\***

Remove the current V1 dependency on manually editing account balances in PostgreSQL. A user should be able to create/fund an account through supported application/API flows.

**\*\*### Git branch\*\***

\`feature/v2-account-funding\`

**\*\*### Current V1 functionality\*\***

\- [x] Create Savings/Current account

\- [x] New account starts with a balance of \`0\`

\- [x] Transfer engine requires sufficient balance

\- [x] Balance can currently be manipulated manually in DB for demo testing

**\*\*### V2 functionality\*\***

\- [x] Define a controlled initial funding/deposit workflow

\- [x] Decide and document funding ownership between Account Service and Transaction Service

\- [x] Add explicit funding/deposit transaction type if transaction records represent all balance movements

\- [x] Validate positive amount

\- [x] Validate amount precision

\- [x] Validate target account ownership where applicable

\- [x] Validate account status before funding

\- [x] Update balance atomically

\- [x] Persist funding/deposit transaction record

\- [x] Persist relevant audit events

\- [x] Return clear API response

\- [x] Prevent invalid/duplicate funding requests where applicable

\- [x] Expose funding action in the frontend

\- [x] Display updated balance immediately after funding

**\*\*### Suggested API surface\*\***

\`\`\`text

POST /api/transactions/deposits

GET  /api/transactions/account/{accountId}

\`\`\`

\> Final endpoint naming and service ownership must be decided during implementation after reviewing the existing transaction/account boundaries.

**\*\*### Database validation\*\***

\- [x] Funding creates expected transaction row

\- [x] Funding changes only the intended account balance

\- [x] Funding amount matches transaction amount

\- [x] Transaction status is correct

\- [x] Audit record is created

\- [x] No duplicate balance credit from repeated request

**\*\*### UI validation\*\***

\- [x] Funding/deposit entry point visible from account workflow

\- [x] Amount validation

\- [x] Loading state

\- [x] Success confirmation

\- [x] Failure message

\- [x] Updated balance shown without manual refresh where appropriate

**\*\*### Postman validation\*\***

\- [x] Valid funding succeeds

\- [x] Zero/negative amount rejected

\- [x] Excess decimal precision rejected

\- [x] Invalid account rejected

\- [x] Inactive account rejected

\- [x] Duplicate/retry behavior verified

**\*\*### Exit criteria\*\***

\- [x] Demo user can create and fund an account without SQL edits

\- [x] Funding is persisted correctly

\- [x] Balance and transaction history remain consistent

**\*\*---\*\***

**\*\*## 6.2 Reachable / Realistic Failed Transaction Flow\*\***

**\*\*### Objective\*\***

Make the \`FAILED\` transaction lifecycle demonstrable through a legitimate application/API scenario instead of relying mainly on validation that prevents transaction creation before a \`FAILED\` record exists.

**\*\*### Git branch\*\***

\`feature/v2-transaction-failure\`

**\*\*### Current V1 functionality\*\***

\- [x] Transaction entity supports \`PENDING\`, \`COMPLETED\`, \`FAILED\`

\- [x] Failed business/infrastructure paths are covered by backend tests/API behavior

\- [x] Audit events include \`TRANSFER_FAILED\`

\- [x] Frontend shows transfer failure state

\- [x] Frontend does not naturally create a persisted \`FAILED\` transaction for every failed validation scenario

**\*\*### V2 functionality\*\***

\- [x] Define legitimate failure cases that occur after transaction initiation

\- [x] Ensure transaction is persisted as \`PENDING\` before the failure decision where appropriate

\- [x] Transition to \`FAILED\` with failure reason

\- [x] Guarantee no incorrect balance mutation

\- [x] Preserve idempotency behavior for failed requests

\- [x] Persist \`TRANSFER_FAILED\` audit event

\- [x] Surface failure reference/message to frontend

\- [x] Show failed transaction in history when appropriate

**\*\*### Failure scenarios to cover\*\***

\- [x] Controlled downstream Account Service business failure after initiation

\- [x] Controlled service/infrastructure failure after transaction record creation

\- [x] Invalid/inactive account condition at the transaction-processing stage

\- [x] No partial debit/credit on failure

\> Do not introduce artificial random failures just to demo the status. The failure must be deterministic, controlled, and explainable.

**\*\*### Database validation\*\***

\- [x] \`FAILED\` row exists when the intended scenario occurs

\- [x] Status transition is correct

\- [x] Failure message/reason is persisted

\- [x] No incorrect source debit

\- [x] No incorrect destination credit

\- [x] Audit row is persisted

**\*\*### UI validation\*\***

\- [x] Failure state is understandable

\- [x] Transaction reference/error reason displayed where appropriate

\- [x] Failed transaction appears in history if designed to do so

\- [x] Account balances remain correct

**\*\*### Postman validation\*\***

\- [x] Trigger each supported failure scenario

\- [x] Verify HTTP status/error body

\- [x] Query transaction history

\- [x] Verify audit record

\- [x] Retry with same \`Idempotency-Key\` and verify consistent result

**\*\*### Exit criteria\*\***

\- [x] A genuine failed transaction can be demonstrated end-to-end

\- [x] No balance corruption occurs

\- [x] Failure is auditable and explainable

**\*\*---\*\***

**\*\*## 6.3 Customer Onboarding ✅ / Guided First-Time Flow\*\***

**\*\*### Objective\*\***

Handle the currently possible state where authentication succeeds but the user does not yet have a customer profile/account, and guide the user instead of showing an empty or confusing dashboard.

**\*\*### Git branch\*\***

\`feature/v2-onboarding\`

**\*\*### V2 functionality\*\***

\- [x] Detect missing customer profile

\- [x] Detect authenticated customer with no bank account

\- [x] Provide guided onboarding state

\- [x] Create customer profile from onboarding

\- [x] Continue directly into account creation

\- [x] Explain next required step clearly

\- [x] Prevent unnecessary duplicate profile creation

\- [x] Handle partial onboarding safely

\- [x] Redirect back to dashboard after onboarding completion

**\*\*### UI states\*\***

\- [x] New user after registration/login

\- [x] Profile required

\- [x] Profile complete, account required

\- [x] Account created, funding required

\- [x] Fully onboarded customer

**\*\*### Database validation\*\***

\- [x] Profile created against authenticated user ID

\- [x] Account created against correct customer

\- [x] No duplicate customer record

\- [x] Ownership remains enforced

**\*\*### Postman validation\*\***

\- [x] New user → no profile

\- [x] Create profile

\- [x] Create account

\- [x] Verify subsequent authenticated requests use the same customer/account ownership

**\*\*### Exit criteria\*\***

\- [x] Fresh user can go from registration/login to usable funded account without database editing

**\*\*---\*\***

**\*\*## 6.4 Account Lifecycle ✅ — ACTIVE / FROZEN / CLOSED\*\***

**\*\*### Objective\*\***

Make account status meaningful beyond simple validation by adding a controlled lifecycle that can demonstrate blocked financial operations.

**\*\*### Git branch\*\***

\`feature/v2-account-lifecycle\`

**\*\*### Current V1 functionality\*\***

\- [x] Account status exists

\- [x] Transfer validates usable account status

\- [x] Beneficiary validation checks inactive destination accounts

**\*\*### V2 functionality\*\***

\- [x] Define supported statuses and lifecycle transitions

\- [x] Define who can perform each lifecycle action

\- [x] Add freeze/unfreeze behavior for appropriate roles

\- [x] Add close-account behavior with business constraints

\- [x] Prevent transfers from frozen/closed source account

\- [x] Prevent transfers to closed/ineligible destination account

\- [x] Prevent funding of ineligible account

\- [x] Prevent beneficiary addition against ineligible accounts

\- [x] Persist status changes with timestamps/reason where appropriate

\- [x] Surface status clearly in UI

**\*\*### Suggested status model\*\***

\`\`\`text

ACTIVE

FROZEN

CLOSED

\`\`\`

\> Do not add statuses unless their business meaning and allowed transitions are defined first.

**\*\*### Database validation\*\***

\- [x] Status transition persisted correctly

\- [x] Invalid transitions rejected

\- [x] Existing balance preserved when frozen

\- [x] Closed account cannot be used for prohibited operations

**\*\*### UI validation\*\***

\- [x] Status chip/indicator

\- [x] Disabled actions for prohibited operations

\- [x] Clear user-facing explanation

**\*\*### Postman validation\*\***

\- [x] Freeze account

\- [x] Attempt transfer

\- [x] Attempt funding if applicable

\- [x] Unfreeze account

\- [x] Verify valid operation resumes

\- [x] Close account where business conditions allow

**\*\*### Exit criteria\*\***

\- [x] Account lifecycle affects real business operations consistently across services

**\*\*---\*\***

**\*\*## 6.5 Transaction Details, Filtering & Pagination\*\***

**\*\*### Objective\*\***

Improve transaction history from a basic list into a more realistic banking transaction view.

**\*\*### Git branch\*\***

\`feature/v2-transaction-details\`

**\*\*### Current V1 functionality\*\***

\- [x] Account transaction history

\- [x] Newest-first sorting

\- [x] Sender/receiver visibility enforcement

\- [x] Pagination

\- [x] Search

\- [x] Advanced date filtering

\- [x] Advanced type/status filtering

**\*\*### V2 functionality\*\***

\- [x] Transaction detail endpoint or detail response expansion

\- [x] Pagination

\- [x] Date-range filtering

\- [x] Status filtering

\- [x] Transaction type filtering

\- [x] Credit/debit direction filtering

\- [x] Optional reference search

\- [x] Stable sorting

\- [x] UI pagination controls

\- [x] UI filter controls

\- [x] Detail/receipt view

**\*\*### Suggested API shape\*\***

\`\`\`text

GET /api/transactions/account/{accountId}

    ?page=0

    &size=20

    &status=COMPLETED

    &type=TRANSFER

    &fromDate=...

    &toDate=...

    &search=...

\`\`\`

**\*\*### Database validation\*\***

\- [x] Pagination returns correct records/counts

\- [x] Filters do not leak another user's data

\- [x] Sorting is deterministic

\- [x] Detail response matches stored transaction

**\*\*### UI validation\*\***

\- [x] Filter form

\- [x] Empty filtered state

\- [x] Loading state

\- [x] Pagination state

\- [x] Transaction detail/receipt

**\*\*### Postman validation\*\***

\- [x] Default page

\- [x] Different page/size

\- [x] Status filter

\- [x] Date filter

\- [x] Search/reference filter

\- [x] Invalid filter values

**\*\*### Exit criteria\*\***

\- [x] Transaction history is usable for a realistic demo dataset

\- [x] Ownership/security remains enforced for every query

**\*\*---\*\***

**\*\*## 6.6 Reliability & Cold-Start UX\*\***

**\*\*### Objective\*\***

Make the local and deployed frontend behave predictably when a backend service is unavailable or slow to start.

**\*\*### Git branch\*\***

\`feature/v2-reliability\`

**\*\*### V2 functionality\*\***

\- [x] Distinguish timeout/network errors from business errors

\- [x] Provide clear service-unavailable messages

\- [x] Handle gateway/downstream \`502/503/504\` consistently

\- [x] Add safe retry behavior only for read operations where appropriate

\- [x] Do not blindly retry money-transfer \`POST\` operations

\- [x] Preserve idempotency-key behavior for safe transfer retries initiated intentionally by the user

\- [x] Add local startup/readiness documentation

\- [x] Add sensible loading states for cold-start scenarios

\- [x] Prevent duplicate form submissions while request is in flight

**\*\*### UI validation\*\***

\- [x] Backend unavailable state

\- [x] Backend starting/slow state

\- [x] Read retry action

\- [x] Transfer button disabled during submission

\- [x] Clear error message without leaking internal stack traces

**\*\*### Postman validation\*\***

\- [x] Stop a downstream service and observe expected error

\- [x] Restart service and verify recovery

\- [x] Verify transfer request is not duplicated by automatic retries

**\*\*### Exit criteria\*\***

\- [x] Users receive understandable failure/recovery behavior

\- [x] Financial write operations are not blindly retried

**\*\*---\*\***

**\*\*## 6.7 Banking UI / Product Polish ✅\*\***

**\*\*### Objective\*\***

Improve the banking experience after the core workflows are functionally complete.

**\*\*### Git branch\*\***

\`feature/v2-ui-polish\`

**\*\*### Planned functionality\*\***

\- [x] Account card improvements

\- [x] Funding/deposit UI

\- [x] Guided onboarding UI

\- [x] Transaction detail/receipt UI

\- [x] Filter/pagination UI

\- [x] Account lifecycle indicators

\- [x] Better success/error feedback

\- [x] Consistent currency/amount formatting

\- [x] Confirmation dialogs for critical actions

\- [x] Responsive/mobile refinements

\- [x] Accessibility review of important forms and actions

\- [x] Empty-state copy refinement

\- [x] Error-state copy refinement

**\*\*### Exit criteria\*\***

\- [x] V2 workflows are understandable without developer knowledge

\- [x] Important actions have clear feedback and confirmation

**\*\*---\*\***

**\*\*## 6.8 V2 Regression & Automated Testing\*\***

**\*\*### Objective\*\***

Protect the existing V1 behavior while adding V2 workflows.

**\*\*### Git branch\*\***

\`feature/v2-testing\`

**\*\*### Backend tests\*\***

\- [x] Account funding success

\- [x] Account funding validation failures

\- [x] Funding idempotency/retry behavior where applicable

\- [x] Failed transaction lifecycle

\- [x] No-balance-corruption failure cases

\- [x] Onboarding/profile ownership

\- [x] Account lifecycle transitions

\- [x] Frozen/closed operation restrictions

\- [x] Transaction pagination/filtering

\- [x] Reliability/error mapping

\- [x] Regression of successful transfer

\- [x] Regression of concurrent transfer handling

\- [x] Regression of idempotency

**\*\*### Frontend tests\*\***

\- [x] Funding form

\- [x] Onboarding state

\- [x] Failure-state rendering

\- [x] Account lifecycle UI

\- [x] Transaction filters

\- [x] Pagination

\- [x] Transaction detail

\- [x] Service-unavailable states

\- [x] Regression of transfer form

\- [x] Regression of auth refresh

\- [x] Regression of protected/admin routes

**\*\*### Integration / API validation\*\***

\- [x] Postman collection updated for V2

\- [x] Local Gateway routes tested

\- [x] Database state cross-checked after each major scenario

\- [x] Full V2 happy-path smoke test

\- [x] Full V2 failure-path smoke test

**\*\*### Build validation\*\***

\- [x] Maven test/package for all backend services

\- [x] Frontend \`npm run test\:run\`

\- [x] Frontend \`npm run build\`

\- [x] Docker Compose full-stack validation

**\*\*---\*\***

**\*\*## V2 Recommended Development Order\*\***

\`\`\`text

6.0 Localhost Development Baseline ✅

        ↓

6.1 Account Funding / Opening Balance ✅

        ↓

6.2 Reachable Failed Transaction Flow ✅

        ↓

6.3 Customer Onboarding ✅

        ↓

6.4 Account Lifecycle ✅

        ↓

6.5 Transaction Details / Filtering / Pagination ✅

        ↓

6.6 Reliability / Cold-Start UX ✅

        ↓

6.7 Banking UI / Product Polish ✅

        ↓

6.8 V2 Regression Testing ✅

        ↓

README.md final V2 update — release documentation

\`\`\`

**\*\*## V2 Git Strategy\*\***

\`\`\`text

main

  ↓

develop

  ↓

chore/v2-local-dev-baseline

  ↓

feature/v2-account-funding

  ↓

feature/v2-transaction-failure

  ↓

feature/v2-onboarding

  ↓

feature/v2-account-lifecycle

  ↓

feature/v2-transaction-details

  ↓

feature/v2-reliability

  ↓

feature/v2-ui-polish

  ↓

feature/v2-testing

  ↓

develop

  ↓

main

\`\`\`

**\*\*### Git rules\*\***

\- [x] Start each feature branch from the latest \`develop\`

\- [x] Keep commits focused by requirement

\- [x] Run tests before opening PR

\- [x] Verify Database + UI + Postman before merging each major feature

\- [x] Merge feature branches into \`develop\`

\- [x] Do not merge unfinished V2 work directly into \`main\`

\- [x] Keep \`main\` deployable

\- [x] Update \`README.md\` only after V2 is complete, as requested

**\*\*---\*\***

**\*\*# V2 Definition of Done\*\***

**\*\*## Environment\*\***

\- [x] Frontend development environment points to localhost Gateway

\- [x] Full local Docker Compose stack works

\- [x] V2 does not depend on Render availability

**\*\*## Customer workflow\*\***

\- [x] Register

\- [x] Login

\- [x] Guided profile creation

\- [x] Create account

\- [x] Fund account without SQL

\- [x] Add another user's account as beneficiary

\- [x] Complete transfer

\- [x] View updated balances

\- [x] View transaction history/details

**\*\*## Failure workflow\*\***

\- [x] Trigger a legitimate post-initiation failure

\- [x] Persist \`FAILED\` transaction

\- [x] No incorrect balance mutation

\- [x] Audit failure

\- [x] Show understandable failure feedback

**\*\*## Account lifecycle\*\***

\- [x] Freeze/unfreeze behavior works

\- [x] Closed account rules work where applicable

\- [x] Prohibited financial operations are blocked consistently

**\*\*## Reliability\*\***

\- [x] Slow/unavailable services handled clearly

\- [x] No blind automatic retries for money-transfer writes

\- [x] Duplicate submission prevented in UI

**\*\*## Quality\*\***

\- [x] Backend tests pass

\- [x] Frontend tests pass

\- [x] Postman collection passes

\- [x] Database state matches expected business outcome

\- [x] Docker Compose stack passes smoke test

\- [x] No manual SQL required for normal demo workflow

\- [ ] README updated after V2 completion

**\*\*---\*\***

**\*\*# V2 RELEASE STATE — IMPLEMENTATION COMPLETE ✅\*\***

V2 core workflow refinement and V2 regression/testing work are complete. The V2 feature branches have been merged into `develop`, and the Vercel `develop` deployment is running successfully.

**\*\*Current remaining release/documentation tasks**\*\*\*\*

\- [ ] Update `README.md` with the final V2 architecture, workflows, validation steps, and screenshots where applicable

\- [ ] Run the final release validation from `develop`

\- [ ] Raise/merge the final `develop` → `main` release PR

\- [ ] Verify the production deployment after the `main` merge

**\*\*---\*\***

**\*\*# 7. Optional Advanced Features\*\***

\\> Add these only after the core application and Phase 5 packaging are complete.

**\*\*## Notifications\*\***

\\- [ ] Email after successful transfer

\\- [ ] Email after failed transfer

\\- [ ] Notification history

\\- [ ] Notification service

**\*\*## Redis\*\***

Potential uses:

\\- [ ] Caching

\\- [ ] Rate limiting

\\- [ ] Temporary data

\\- [ ] Frequently accessed read data

**\*\*## Event-Driven Architecture\*\***

Potential flow:

\\\`\\\`\\\`text

Transaction Service

        |

        v

  Transaction Event

      /       \\\\

     v         v

Notification   Analytics

Service        /Reporting

\\\`\\\`\\\`

Possible technology:

\\- [ ] Apache Kafka

**\*\*## Rate Limiting\*\***

\\- [ ] Login attempt limits

\\- [ ] Transfer request limits

\\- [ ] Sensitive endpoint limits

**\*\*## Account Statements\*\***

\\- [ ] Monthly statement

\\- [ ] CSV export

\\- [ ] PDF export

**\*\*## Scheduled Transfers\*\***

\\- [ ] Schedule transfer

\\- [ ] View scheduled transfers

\\- [ ] Cancel scheduled transfer

\\- [ ] Execute scheduled transfer

**\*\*---\*\***

**\*\*# 8. Features Deliberately Excluded from MVP\*\***

Do not add these merely to increase the technology list:

\\- [ ] Kubernetes

\\- [ ] Complex cloud infrastructure

\\- [ ] Large-scale observability stack

\\- [ ] Multiple messaging systems

\\- [ ] Multiple caching systems

\\- [ ] External payment gateway

\\- [ ] Fraud-detection ML model

\\- [ ] Complex banking integrations

\\- [ ] Excessive microservices

\\> The goal is depth, not technology count.

**\*\*---\*\***

**\*\*# 9. Current API Surface\*\***

**\*\*## Auth Service\*\***

\\\`\\\`\\\`text

POST /api/auth/register

POST /api/auth/login

POST /api/auth/refresh

POST /api/auth/logout

GET  /api/auth/me

GET  /api/admin/user-stats

\\\`\\\`\\\`

**\*\*## Account Service\*\***

\\\`\\\`\\\`text

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

\\\`\\\`\\\`

Internal service endpoints remain service-to-service and are not exposed through the Gateway.

**\*\*## Transaction Service\*\***

\\\`\\\`\\\`text

POST /api/transactions/transfers

GET  /api/transactions/account/{accountId}

GET  /api/admin/transaction-stats

\\\`\\\`\\\`

**\*\*---\*\***

**\*\*# 10. Important End-to-End Scenarios\*\***

**\*\*## Scenario 1 — Registration & Login\*\***

\\\`\\\`\\\`text

Register

   ↓

Login

   ↓

JWT + Refresh Token

   ↓

/api/auth/me

   ↓

Protected dashboard

\\\`\\\`\\\`

\\- [x] Verified

**\*\*## Scenario 2 — Account Creation\*\***

\\\`\\\`\\\`text

Login

   ↓

Create customer profile

   ↓

Create account

   ↓

Account + balance displayed

\\\`\\\`\\\`

\\- [x] Verified

**\*\*## Scenario 3 — Successful Transfer\*\***

\\\`\\\`\\\`text

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

\\\`\\\`\\\`

\\- [x] Verified through UI

**\*\*## Scenario 4 — Failed Transfer\*\***

\\\`\\\`\\\`text

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

\\\`\\\`\\\`

\\- [x] Verified

**\*\*## Scenario 5 — Duplicate Request\*\***

\\\`\\\`\\\`text

Request + Idempotency-Key

          ↓

     Transfer executed

          ↓

       Retry same key

          ↓

Existing transaction returned

          ↓

No duplicate debit

\\\`\\\`\\\`

\\- [x] Verified

**\*\*## Scenario 6 — Role-Based Admin Monitoring\*\***

\\\`\\\`\\\`text

ROLE\\\_ADMIN

    ↓

Admin Dashboard

    ↓

Users / Customers / Accounts / Transactions

\\\`\\\`\\\`

\\- [x] Verified through UI

\\- [x] Backend admin APIs protected by role

**\*\*## Scenario 7 — Frontend Token Refresh\*\***

\\\`\\\`\\\`text

Expired access token

        ↓

401

        ↓

Refresh token

        ↓

New access token

        ↓

Retry original request

\\\`\\\`\\\`

\\- [x] Verified

**\*\*---\*\***

**\*\*# 11. Definition of Done\*\***

**\*\*## Core Application\*\***

\\- [x] Authentication works

\\- [x] JWT protection works

\\- [x] USER/ADMIN roles work

\\- [x] Customer profile works

\\- [x] Accounts work

\\- [x] Beneficiaries work

\\- [x] Transfers work

\\- [x] Balance validation works

\\- [x] Duplicate transfers are prevented

\\- [x] Concurrent transfer behavior is handled

\\- [x] Transaction lifecycle is stored

\\- [x] Transaction history works

\\- [x] Audit logs work

\\- [x] Admin monitoring works

\\- [x] Charts display real backend data

\\- [x] Backend tests pass

\\- [x] Frontend critical-path tests pass

\\- [x] Swagger is available

\\- [x] API Gateway integration works

**\*\*## V2 / Release Definition-of-Done Items\*\***

\- [x] V2 workflow refinement completed

\- [x] V2 regression/testing completed

\- [x] V2 changes merged into `develop`

\- [x] `develop` deployment validated on Vercel

\- [ ] README fully updated for final V2 state

\- [ ] Final architecture/workflow documentation reviewed

\- [ ] Final screenshots refreshed where V2 UI changed

\- [ ] Final `develop` → `main` release validation completed

\- [ ] Production deployment verified after `main` merge

**\*\*---\*\***

**\*\*# 12. Core Interview Topics to Master\*\***

**\*\*## Authentication & Security\*\***

\\- [x] JWT authentication

\\- [x] Access token vs refresh token

\\- [x] Password hashing

\\- [x] Spring Security filters/resource server

\\- [x] Authentication vs authorization

\\- [x] RBAC

\\- [x] Protected endpoints

\\- [x] Frontend token refresh flow

\\- [x] Service-to-service secret protection

**\*\*## Transaction Processing\*\***

\\- [x] Database transactions

\\- [x] ACID principles

\\- [x] Balance validation

\\- [x] Rollback/error behavior

\\- [x] Transaction statuses

\\- [x] Failed transactions

\\- [x] Idempotency

\\- [x] Duplicate requests

\\- [x] Audit events

**\*\*## Concurrency\*\***

\\- [x] Race conditions

\\- [x] Concurrent transfers

\\- [x] Pessimistic locking

\\- [x] Optimistic/version-aware update strategy

\\- [x] Maintaining correct balances

**\*\*## Microservices\*\***

\\- [x] Why services were separated

\\- [x] Service responsibilities

\\- [x] Service-to-service REST calls

\\- [x] API Gateway

\\- [x] Database-per-service ownership

\\- [x] Security boundaries

\\- [x] Microservice trade-offs

**\*\*## REST APIs\*\***

\\- [x] HTTP methods

\\- [x] HTTP status codes

\\- [x] DTOs

\\- [x] Validation

\\- [x] Error response design

\\- [x] Swagger/OpenAPI

**\*\*## Testing\*\***

\\- [x] Unit testing

\\- [x] Mockito

\\- [x] Spring Boot tests

\\- [x] API testing with Postman

\\- [x] Frontend component testing

\\- [x] Route testing

\\- [x] Form testing

\\- [x] Critical-path testing

\\- [ ] Testcontainers

\\- [ ] Full end-to-end automated browser testing

**\*\*---\*\***

**\*\*# 13. Resume-Focused Outcome\*\***

The completed core project demonstrates:

\\- Secure JWT-based authentication

\\- Refresh-token based session continuation

\\- Role-based access control

\\- Spring Boot microservices

\\- API Gateway architecture

\\- PostgreSQL database ownership per service

\\- Customer and account management

\\- Beneficiary management

\\- Financial transfer processing

\\- Idempotent API design

\\- Concurrency-safe balance updates

\\- Transaction history

\\- Audit logging

\\- React + TypeScript + MUI

\\- Recharts analytics

\\- Admin monitoring

\\- Backend automated testing

\\- Frontend critical-path testing

\\- Swagger/OpenAPI documentation

**\*\*## Strong Resume-Level Project Description\*\***

\\> **\*\*\\\*\\\*Digital Banking Platform\\\*\\\*\*\*** — Built a secure full-stack banking platform using Java, Spring Boot, React, TypeScript, JWT, PostgreSQL, and microservices, supporting customer/account management, beneficiaries, money transfers, transaction history, audit logging, role-based admin monitoring, and financial analytics. Implemented idempotent transfer processing, concurrency-safe balance updates, service-to-service authentication, automated backend/frontend testing, API Gateway routing, and Swagger/OpenAPI documentation.

**\*\*---\*\***

**\*\*# 14. Final Priority Order**

**\*\*## Priority 1 — Existing V1 Engineering Foundations ✅**\*\*\*\*

1\. Authentication & Spring Security ✅

2\. Account management ✅

3\. Transfer business logic ✅

4\. Balance validation ✅

5\. Transaction lifecycle ✅

6\. Idempotency ✅

7\. Concurrency/consistency ✅

8\. REST API quality ✅

9\. Testing ✅

**\*\*## Priority 2 — Existing V1 Product ✅**\*\*\*\*

10\. React dashboard ✅

11\. Admin dashboard ✅

12\. Transaction history ✅

13\. Audit logging ✅

14\. Recharts analytics ✅

15\. API Gateway ✅

16\. Swagger/OpenAPI ✅

17\. Docker / Docker Compose ✅

18\. V1 deployment ✅

**\*\*## Priority 3 — V2 Core Workflow ✅**\*\*\*\*

19\. Localhost development baseline ✅

20\. Account funding / opening balance ✅

21\. Reachable failed transaction flow ✅

22\. Customer onboarding ✅

23\. Account lifecycle ✅

24\. Transaction details/filtering/pagination ✅

25\. Reliability / cold-start UX ✅

26\. Banking UI polish ✅

27\. V2 regression testing ✅

**\*\*## Priority 4 — Final Release & Documentation**\*\*\*\*

28\. Final README V2 update ⏳

29\. Final develop release validation ⏳

30\. Merge validated `develop` into `main` ⏳

31\. Production deployment verification ⏳

**\*\*## Priority 5 — Optional Advanced Features**\*\*\*\*

32\. Redis

33\. Notifications

34\. Kafka / event-driven architecture

35\. Rate limiting

36\. Statements

37\. Scheduled transfers

38\. GitHub Actions

**\*\*---\*\***

**\*\*# 15. Golden Rule for This Project\*\***

\\> **\*\*\\\*\\\*Do not add technology unless it solves a real requirement.\\\*\\\*\*\***

The strongest version of this project is not the one with the most tools.

It is the one where you can confidently explain:

\\> **\*\*\\\*\\\*“Here is the problem, here is why I designed the system this way, here is how the transaction is processed, here is how I keep the data consistent, here is how I secure it, here is how I test it, and here is how the services interact.”\\\*\\\*\*\***
