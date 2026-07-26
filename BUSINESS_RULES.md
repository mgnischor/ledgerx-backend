# 📜 LedgerX Business Rules Catalog

> **Single source of truth for every business rule enforced by the LedgerX Backend API.**
> Each rule has a stable identifier (`BR-XXX`) and is traceable from source code to documentation through Javadoc comments, OpenAPI annotations, and controller-level enforcement.

[![Rule IDs](https://img.shields.io/badge/Rule_IDs-BR--001%20→%20BR--127-blue)](#-rule-index)
[![Enforcement](https://img.shields.io/badge/Enforcement-DTO%20%7C%20Controller%20%7C%20Domain-green)](#-enforcement-layers)
[![Error Model](https://img.shields.io/badge/Error_Model-ApiError-orange)](#-error-model)
[![Traceability](https://img.shields.io/badge/Traceability-Javadoc%20%2B%20OpenAPI-purple)](#-traceability)

---

## 🧭 Purpose

This document catalogs every validation rule, uniqueness constraint, state transition, invariant, and authorization policy enforced by the LedgerX Backend API.

It is designed to keep product behavior, engineering implementation, and API documentation aligned over time.

Each rule:

- Has a stable ID, such as `BR-042`
- Is grouped by bounded context
- Identifies the enforcement layer
- Is referenced from the controller or component that enforces it

---

## 🧩 Rule Index

- [Enforcement Layers](#-enforcement-layers)
- [Error Model](#-error-model)
- [Traceability](#-traceability)
- [Identity](#-identity)
- [Company](#-company)
- [Accounting](#-accounting)
- [Billing](#-billing)
- [Reporting](#-reporting)
- [Budgets](#-budgets)
- [Recurring Transactions](#-recurring-transactions)
- [Notifications](#-notifications)
- [Authorization Profiles](#-authorization-profiles)
- [Cross-Cutting Foundations](#-cross-cutting-foundations)
- [Known Gaps](#-known-gaps)

---

## 🧱 Enforcement Layers

Rules are enforced at the most appropriate layer of the application.

| Layer | Meaning | Typical HTTP Status |
|---|---|---|
| **DTO** | Bean Validation constraint on a request record, validated with `@Valid` at the controller boundary before the method body runs | `400 Bad Request` |
| **Controller** | Explicit check in the controller method body, such as rejecting an unsupported operation on a specific endpoint | Usually `400`, `404`, or `422` |
| **Use Case / Domain** | Rule enforced by the application or domain layer, such as uniqueness checks requiring a repository lookup or aggregate invariants | Usually `404` or `422` |

---

## 🚨 Error Model

Rule violations are always returned as structured `ApiError` responses.

| Scenario | HTTP Status | Meaning |
|---|---:|---|
| DTO validation failure | `400 Bad Request` | Malformed or invalid request payload |
| Referenced entity does not exist | `404 Not Found` | Missing aggregate or resource |
| Business rule violation | `422 Unprocessable Entity` | Syntactically valid request, but semantically rejected |
| Missing or insufficient permission | `403 Forbidden` | Authenticated caller is not authorized |

The API never exposes stack traces to clients for business or authorization failures.

---

## 🔗 Traceability

Each `BR-XXX` identifier is referenced from the implementation through:

- Javadoc comments on controller methods
- OpenAPI `@Operation` and `@ApiResponse` annotations
- Domain and application-layer components when enforcement happens below the controller

This keeps the catalog and the codebase mutually traceable.

---

## 👤 Identity

**Base path:** `/api/v1/users`

| ID | Rule | Enforced By |
|---|---|---|
| `BR-001` | Full name is required | DTO — `CreateUserRequest` |
| `BR-002` | Full name must be between 2 and 150 characters | DTO — `CreateUserRequest` |
| `BR-003` | Email is required | DTO — `CreateUserRequest` |
| `BR-004` | Email must be a syntactically valid address, with a maximum of 254 characters | DTO — `CreateUserRequest`, `EmailAddress` |
| `BR-005` | Email must be unique among registered users | Use Case — `RegisterUserUseCase` |
| `BR-006` | Password is required | DTO — `CreateUserRequest` |
| `BR-007` | Password must be between 8 and 128 characters | DTO — `CreateUserRequest` |
| `BR-008` | Password must contain at least one uppercase letter | DTO — `@StrongPassword` |
| `BR-009` | Password must contain at least one lowercase letter | DTO — `@StrongPassword` |
| `BR-010` | Password must contain at least one digit | DTO — `@StrongPassword` |
| `BR-011` | Password must contain at least one special character | DTO — `@StrongPassword` |
| `BR-012` | Password must not equal the email address | DTO — `@FieldsNotEqual` |
| `BR-013` | Passwords are hashed with Argon2id, automatically falling back to PBKDF2 if Argon2id is unavailable at runtime | `PasswordEncoderConfig` |
| `BR-014` | Email is normalized to lowercase before persistence | Domain — `EmailAddress` |
| `BR-015` | Newly registered users are active by default | Domain — `User` |
| `BR-016` | Newly registered users start with no roles granted; roles must be explicitly assigned | Domain — `User` |
| `BR-017` | Registering a user publishes a `UserRegisteredEvent` | Use Case — `RegisterUserUseCase` |
| `BR-018` | Registering a duplicate email returns `422 Unprocessable Entity`, not a generic error | Use Case — `EmailAlreadyRegisteredException` |
| `BR-019` | Granting a role requires the target user to exist; otherwise returns `404 Not Found` | Use Case — `GrantRoleUseCase` |
| `BR-020` | Only roles defined by the `Role` enum may be granted: `DEVELOPER`, `ADMINISTRATOR`, `MANAGER`, `COLLABORATOR`; unknown values are rejected with `400 Bad Request` before the use case runs | Controller — Jackson enum deserialization |
| `BR-021` | Deactivating a user requires the user to exist; otherwise returns `404 Not Found` | Use Case — `DeactivateUserUseCase` |
| `BR-022` | Deactivating an already-inactive user is idempotent and does not return an error | Domain — `User.deactivate()` |

---

## 🏢 Company

**Base path:** `/api/v1/companies`

| ID | Rule | Enforced By |
|---|---|---|
| `BR-023` | Legal name is required and must have at most 150 characters | DTO — `CreateCompanyRequest` |
| `BR-024` | Trade name is required and must have at most 150 characters | DTO — `CreateCompanyRequest` |
| `BR-025` | CNPJ is required | DTO — `CreateCompanyRequest` |
| `BR-026` | CNPJ must contain exactly 14 digits after stripping formatting characters | Domain — `DocumentNumber.cnpj()` |
| `BR-027` | CNPJ must pass the official Brazilian check-digit algorithm | DTO — `@ValidCnpj` |
| `BR-028` | CNPJ must be unique among registered companies | Use Case — `RegisterCompanyUseCase` |
| `BR-029` | Company size is required and must be one of `MEI`, `MICRO`, or `SMALL` | DTO — `@NotNull` enum binding |
| `BR-030` | Street, number, and city are required | DTO — `CreateCompanyRequest` |
| `BR-031` | State is required and must be a valid Brazilian UF two-letter code | DTO — `@ValidBrazilianState` |
| `BR-032` | Zip code is required and must match the Brazilian CEP format `NNNNNNNN` or `NNNNN-NNN`, with the hyphen optional | DTO — `@ValidBrazilianZipCode` |
| `BR-033` | Country is required | DTO — `CreateCompanyRequest` |
| `BR-034` | Newly registered companies are active by default | Domain — `Company` |
| `BR-035` | Registering a duplicate CNPJ returns `422 Unprocessable Entity` | Use Case — `CompanyAlreadyRegisteredException` |
| `BR-036` | Deactivating a company requires the company to exist; otherwise returns `404 Not Found` | Use Case — `DeactivateCompanyUseCase` |
| `BR-037` | Deactivating an already-inactive company is idempotent | Domain — `Company.deactivate()` |

---

## 💰 Accounting

**Base paths:**

- `/api/v1/transactions`
- `/api/v1/transfers`
- `/api/v1/companies/{companyId}/financial-accounts`
- `/api/v1/companies/{companyId}/categories`

### Financial Accounts

| ID | Rule | Enforced By |
|---|---|---|
| `BR-038` | Financial account name is required and must have at most 100 characters | DTO — `CreateFinancialAccountRequest` |
| `BR-039` | Opening balance is required and cannot be negative | DTO — `@PositiveOrZero` |
| `BR-040` | Currency defaults to `BRL` for accounts created through the API | Controller — `FinancialAccountController` |
| `BR-041` | `companyId` is required to create or list financial accounts | Controller path variable |
| `BR-042` | Deactivating a financial account requires it to exist; otherwise returns `404 Not Found` | Use Case — `DeactivateFinancialAccountUseCase` |

### Categories

| ID | Rule | Enforced By |
|---|---|---|
| `BR-043` | Category name is required and must have at most 60 characters | DTO — `CreateCategoryRequest` |
| `BR-044` | Category type is required and must be `INCOME`, `EXPENSE`, or `TRANSFER` | DTO — `CreateCategoryRequest` |

### Transactions

| ID | Rule | Enforced By |
|---|---|---|
| `BR-045` | `financialAccountId` is required to record a transaction | DTO — `CreateTransactionRequest` |
| `BR-046` | `categoryId` is required to record a transaction | DTO — `CreateTransactionRequest` |
| `BR-047` | Transaction type is required | DTO — `CreateTransactionRequest` |
| `BR-048` | Transaction amount is required and must be strictly positive | DTO — `@Positive` |
| `BR-049` | Monetary amounts are scaled to the currency’s decimal precision, such as 2 decimal places for `BRL`, and never represented as floating point | Domain — `Money` |
| `BR-050` | `occurredOn` is required | DTO — `CreateTransactionRequest` |
| `BR-051` | `occurredOn` cannot be a future date | DTO — `@PastOrPresent` |
| `BR-052` | `occurredOn` cannot be older than 5 years | DTO — `@NotOlderThan(years = 5)` |
| `BR-053` | Description is limited to 255 characters | DTO — `@Size(max = 255)` |
| `BR-054` | The category type must match the transaction type; for example, an `EXPENSE` category cannot be used for an `INCOME` transaction | Use Case — `RecordTransactionUseCase` |
| `BR-055` | `TRANSFER` transactions are rejected on the single-account transaction endpoint; transfers must use `POST /api/v1/transfers` so both legs update atomically | Controller — `TransactionController` |
| `BR-056` | Recording a transaction against a non-existent financial account or category fails with `404 Not Found` | Use Case — `RecordTransactionUseCase` |
| `BR-057` | A financial account can never be debited below zero; insufficient balance is rejected | Domain — `FinancialAccount.debit()` |

### Transfers

| ID | Rule | Enforced By |
|---|---|---|
| `BR-058` | A transfer requires two distinct accounts; source and destination must differ | DTO — `@FieldsNotEqual` |
| `BR-059` | Transfer amount must be strictly positive | DTO — `@Positive` |
| `BR-060` | A transfer debits the source account and credits the destination account atomically within a single transaction | Use Case — `TransferFundsUseCase` |
| `BR-061` | A transfer fails if the source account has insufficient balance | Domain — `FinancialAccount.debit()` |

---

## 🧾 Billing

**Base paths:**

- `/api/v1/companies/{companyId}/parties`
- `/api/v1/invoices`

### Parties

| ID | Rule | Enforced By |
|---|---|---|
| `BR-062` | Party name is required and must have at most 150 characters | DTO — `CreatePartyRequest` |
| `BR-063` | Party document is required and must be a valid CPF or CNPJ matching the declared document type | DTO — `@ValidPartyDocument` |
| `BR-064` | Party email is required and must be syntactically valid | DTO — `CreatePartyRequest` |
| `BR-065` | Party type is required and must be `CUSTOMER` or `SUPPLIER` | DTO — `CreatePartyRequest` |
| `BR-066` | `companyId` is required to create or list parties | Controller path variable |

### Invoices

| ID | Rule | Enforced By |
|---|---|---|
| `BR-067` | `companyId`, `partyId`, and `direction` are required to issue an invoice | DTO — `CreateInvoiceRequest` |
| `BR-068` | The party referenced by an invoice must exist; otherwise returns `404 Not Found` | Use Case — `IssueInvoiceUseCase` |
| `BR-069` | Installment amounts list must not be empty | DTO — `@NotEmpty` |
| `BR-070` | An invoice is limited to 60 installments | DTO — `@Size(max = 60)` |
| `BR-071` | Each installment amount must be strictly positive | Use Case — `IssueInvoiceUseCase` |
| `BR-072` | `firstDueDate` is required and cannot be in the past | DTO — `@FutureOrPresent` |
| `BR-073` | Installments are due monthly, starting from `firstDueDate` | Use Case — `IssueInvoiceUseCase` |
| `BR-074` | Newly issued invoices start with status `OPEN` | Domain — `Invoice` |
| `BR-075` | Registering a payment requires the invoice to exist; otherwise returns `404 Not Found` | Use Case — `RegisterPaymentUseCase` |
| `BR-076` | Registering a payment requires the installment to belong to the invoice | Domain — `Invoice.registerPayment()` |
| `BR-077` | A payment cannot be registered against a canceled invoice | Domain — `Invoice.registerPayment()` |
| `BR-078` | `paidOn` is required and cannot be in the future | DTO — `@PastOrPresent` |
| `BR-079` | An invoice moves to `PARTIALLY_PAID` once at least one installment is paid, but not all | Domain — `Invoice.registerPayment()` |
| `BR-080` | An invoice moves to `PAID` only when every installment is paid | Domain — `Invoice.registerPayment()` |
| `BR-081` | Paying the final installment publishes an `InvoicePaidEvent` | Use Case — `RegisterPaymentUseCase` |
| `BR-082` | Canceling an invoice requires it to exist; otherwise returns `404 Not Found` | Use Case — `CancelInvoiceUseCase` |
| `BR-083` | A fully paid invoice cannot be canceled | Domain — `Invoice.cancel()` |
| `BR-084` | Canceling an already-canceled invoice is idempotent | Domain — `Invoice.cancel()` |

---

## 📊 Reporting

**Base path:** `/api/v1/companies/{companyId}/reports/cash-flow`

| ID | Rule | Enforced By |
|---|---|---|
| `BR-085` | `companyId` is required for the cash-flow report | Controller path variable |
| `BR-086` | `from` and `to` dates are required; missing or malformed values return `400 Bad Request` before the handler runs | Controller — Spring date binding |
| `BR-087` | `from` must not be after `to` | Controller — `ReportController` |
| `BR-088` | The reporting window cannot exceed 366 days | Controller — `ReportController` |
| `BR-089` | Only `INCOME` and `EXPENSE` transactions are included in totals; `TRANSFER` is excluded to avoid double counting | Use Case — `CashFlowReportService` |
| `BR-090` | `netResult` is computed as `totalIncome − totalExpense` | Use Case — `CashFlowReportService` |

---

## 🎯 Budgets

**Base path:** `/api/v1/companies/{companyId}/budgets`

| ID | Rule | Enforced By |
|---|---|---|
| `BR-101` | `period`, representing a calendar month, is required and cannot be in the past | DTO — `@FutureOrPresent` |
| `BR-102` | `limit` is required and must be strictly positive | DTO — `@Positive` |
| `BR-103` | The referenced category must exist; otherwise returns `404 Not Found` | Use Case — `CreateBudgetUseCase` |
| `BR-104` | Budgets can only be set for `EXPENSE` categories | Use Case — `CreateBudgetUseCase` |
| `BR-105` | Only one budget may exist per company, category, and period | Use Case — `CreateBudgetUseCase` |
| `BR-106` | Budget status, including `spent`, `remaining`, and `overBudget`, is computed from `EXPENSE` transactions recorded in the budget’s category during its period | Use Case — `GetBudgetStatusUseCase` |

---

## 🔁 Recurring Transactions

**Base path:** `/api/v1/companies/{companyId}/recurring-transactions`

| ID | Rule | Enforced By |
|---|---|---|
| `BR-107` | Amount is required and must be strictly positive | DTO — `@Positive` |
| `BR-108` | Description is limited to 255 characters | DTO — `@Size(max = 255)` |
| `BR-109` | Frequency is required and must be `WEEKLY`, `MONTHLY`, or `YEARLY` | DTO — `CreateRecurringTransactionRuleRequest` |
| `BR-110` | `firstOccurrence` is required and cannot be in the past | DTO — `@FutureOrPresent` |
| `BR-111` | The referenced category must exist and its type must match the rule’s own type | Use Case — `CreateRecurringTransactionRuleUseCase` |
| `BR-112` | `TRANSFER` is rejected; recurring transfers are not supported | Controller — `RecurringTransactionRuleController` |
| `BR-113` | `POST /api/v1/companies/{companyId}/recurring-transactions/generate-due` materializes every active rule whose `nextOccurrence` is on or before today into a real transaction, reusing `RecordTransactionUseCase` so the same balance rules apply, then advances the rule to its next occurrence | Use Case — `GenerateDueRecurringTransactionsUseCase` |
| `BR-114` | Deactivating a rule excludes it from future `generate-due` runs | Domain — `RecurringTransactionRule.deactivate()` |

---

## 🔔 Notifications

**Base path:** `/api/v1/notifications`

| ID | Rule | Enforced By |
|---|---|---|
| `BR-115` | A notification is created for every `UserRegisteredEvent`, `TransactionRecordedEvent`, and `InvoicePaidEvent` consumed from RabbitMQ | `UserRegisteredMessageListener`, `TransactionRecordedMessageListener`, `InvoicePaidMessageListener` |
| `BR-116` | Notifications are listed most recent first; `unreadOnly=true` filters out already-read notifications | Controller — `NotificationController` |
| `BR-117` | Marking a notification as read is idempotent | Domain — `Notification.markAsRead()` |
| `BR-118` | Marking a non-existent notification as read fails with `404 Not Found` | Use Case — `MarkNotificationAsReadUseCase` |

---

## 🛡️ Authorization Profiles

Authorization is defined by the `Role` enum in the `identity` domain model.

Each role carries a fixed, non-configurable set of `Permission` values through `RolePermissions`.

These permissions are embedded as `PERMISSION_*` authorities in the JWT issued by `POST /api/v1/auth/login` and are also re-derived from the authenticated user’s roles for the OAuth2/PKCE login flow. Both authentication paths enforce the same authorization rules.

### Role Matrix

| Role | Permissions | Summary |
|---|---|---|
| `DEVELOPER` | `READ`, `CREATE`, `UPDATE`, `DELETE`, `APPROVE`, `DEBUG` | Full access plus debug-mode tooling unavailable to any other role |
| `ADMINISTRATOR` | `READ`, `CREATE`, `UPDATE`, `DELETE`, `APPROVE` | Full access to business operations, without debug tooling |
| `MANAGER` | `READ`, `CREATE`, `UPDATE`, `APPROVE` | Can add, change, and approve changes, but cannot delete or deactivate |
| `COLLABORATOR` | `READ`, `CREATE`, `UPDATE` | Can add and change records, but cannot approve, delete, or deactivate |

### Authorization Rules

| ID | Rule | Enforced By |
|---|---|---|
| `BR-119` | `DEVELOPER` holds every permission: `READ`, `CREATE`, `UPDATE`, `DELETE`, `APPROVE`, and `DEBUG` | Domain — `RolePermissions` |
| `BR-120` | `ADMINISTRATOR` holds `READ`, `CREATE`, `UPDATE`, `DELETE`, and `APPROVE` | Domain — `RolePermissions` |
| `BR-121` | `MANAGER` holds `READ`, `CREATE`, `UPDATE`, and `APPROVE` | Domain — `RolePermissions` |
| `BR-122` | `COLLABORATOR` holds `READ`, `CREATE`, and `UPDATE` only | Domain — `RolePermissions` |
| `BR-123` | Every business-record endpoint requires the caller to hold the matching permission authority, returning `403 Forbidden` otherwise; `PATCH /api/v1/invoices/{invoiceId}/payments` requires `APPROVE`, and every deactivate or cancel endpoint requires `DELETE` | `@PreAuthorize` — business controllers |
| `BR-124` | Granting a role or deactivating a user account requires the `DEVELOPER` or `ADMINISTRATOR` role, returning `403 Forbidden` otherwise | `@PreAuthorize` — `GrantRoleUseCase`, `DeactivateUserUseCase` |
| `BR-125` | `GET /api/v1/debug/info`, as well as the `X-Debug-Request-Id` and `X-Debug-Duration-Ms` response headers, are only available to callers holding the `DEBUG` permission, which belongs exclusively to the `DEVELOPER` role | `@PreAuthorize` — `DebugController`; `DebugModeFilter` |
| `BR-126` | An authenticated caller lacking the required permission or role receives `403 Forbidden` with a structured `ApiError` body, not a stack trace or generic `500` response | `GlobalExceptionHandler` |
| `BR-127` | A `DEVELOPER` account is bootstrapped on every startup if its configured email is not already registered, ensuring there is always at least one user who can grant roles; this behavior is idempotent and can be disabled with `ledgerx.security.bootstrap-admin.enabled=false` | `AdminBootstrapRunner` |

---

## 🧬 Cross-Cutting Foundations

These rules apply across the entire system and define core modeling, persistence, and security invariants.

| ID | Rule | Enforced By |
|---|---|---|
| `BR-091` | All monetary amounts are represented as `BigDecimal` with an explicit `Currency`, never as floating point | Domain — `Money` |
| `BR-092` | Every business-rule violation returns `422 Unprocessable Entity` with a structured `ApiError` body | `GlobalExceptionHandler` |
| `BR-093` | Every entity-not-found error returns `404 Not Found` with a structured `ApiError` body | `GlobalExceptionHandler` |
| `BR-094` | Every request payload is validated with `@Valid` at the controller boundary before reaching application services | Controllers |
| `BR-095` | Password hashes never appear in API response DTOs; `UserDto` excludes `hashedPassword` | `UserMapper` |
| `BR-096` | Every persisted entity carries `createdAt` and `updatedAt` audit timestamps | `AuditableEntity` |
| `BR-097` | Every aggregate identifier is a UUID assigned client-side by the domain layer, never a sequential integer | Domain constructors |
| `BR-098` | Any non-password hash, such as checksums, fingerprints, or idempotency keys, must use SHA3-512 | `Sha3512Hasher` |
| `BR-099` | Passwords are never stored in plaintext; only Argon2id or PBKDF2 hashes are persisted | `PasswordEncoderConfig` |
| `BR-100` | Aggregate identifiers are immutable once assigned; JPA entities never rely on database-generated IDs, so re-saving an already-persisted aggregate updates its row instead of inserting a duplicate | `BaseEntity`, all `*JpaMapper` classes |

---

## ⚠️ Known Gaps

The following items are intentionally outside the current catalog because they are not yet fully implemented or are pending architectural completion.

| Area | Gap |
|---|---|
| LDAP | `spring-boot-starter-ldap` is on the classpath but remains unconfigured; no LDAP authentication provider is wired up |
| OAuth2 Resource Server | The Authorization Server issues its own RSA-signed access tokens for the PKCE flow, but the API resource-server chain currently validates only the Ed25519 JWTs issued by `POST /api/v1/auth/login`; OAuth2-issued tokens cannot yet be used as bearer credentials against `/api/v1/**` |
| Notification Scoping | The notification feed at `/api/v1/notifications` is global and is not scoped to a user or company because there is not yet a session/current-user concept to scope it by |
| Recurring Transaction Scheduling | `POST /api/v1/companies/{companyId}/recurring-transactions/generate-due` must be triggered manually or by an external scheduler; there is no in-process scheduled job that calls it automatically |

---

<p align="center">
  <sub>See <a href="README.md">README.md</a> for setup, authentication, and architecture documentation.</sub>
</p>
