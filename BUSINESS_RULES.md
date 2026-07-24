# Business Rules

This document catalogs every business rule enforced by the LedgerX Backend API, grouped by
bounded context. Each rule has a stable ID (`BR-XXX`) referenced from Javadoc comments on the
controller methods and OpenAPI `@Operation`/`@ApiResponse` annotations that enforce it, so the
code and this document stay traceable to each other.

**Enforcement layers**

- **DTO** — a Bean Validation constraint (built-in or custom, e.g. `@ValidCnpj`) on a request
  record, validated via `@Valid` at the controller boundary before the method body runs.
- **Controller** — an explicit check in the controller method body (e.g. rejecting `TRANSFER`
  on the single-account transaction endpoint).
- **Use case / Domain** — enforced by the application or domain layer the controller calls into
  (e.g. uniqueness checks that require a repository lookup, or invariants on the aggregate
  itself, such as `FinancialAccount.debit()` refusing to go negative).

A rule violation surfaces as `400 Bad Request` (DTO validation failure), `404 Not Found`
(referenced entity does not exist), or `422 Unprocessable Entity` (a business rule was
violated), all as a structured `ApiError` body (see BR-092).

## Identity (`/api/v1/users`)

| ID | Rule | Enforced by |
|----|------|-------------|
| BR-001 | Full name is required | DTO — `CreateUserRequest` |
| BR-002 | Full name must be between 2 and 150 characters | DTO — `CreateUserRequest` |
| BR-003 | Email is required | DTO — `CreateUserRequest` |
| BR-004 | Email must be a syntactically valid address, max 254 characters | DTO — `CreateUserRequest`, `EmailAddress` |
| BR-005 | Email must be unique among registered users | Use case — `RegisterUserUseCase` |
| BR-006 | Password is required | DTO — `CreateUserRequest` |
| BR-007 | Password must be between 8 and 128 characters | DTO — `CreateUserRequest` |
| BR-008 | Password must contain at least one uppercase letter | DTO — `@StrongPassword` |
| BR-009 | Password must contain at least one lowercase letter | DTO — `@StrongPassword` |
| BR-010 | Password must contain at least one digit | DTO — `@StrongPassword` |
| BR-011 | Password must contain at least one special character | DTO — `@StrongPassword` |
| BR-012 | Password must not equal the email address | DTO — `@FieldsNotEqual` |
| BR-013 | Passwords are hashed with Argon2id, falling back to PBKDF2 automatically if Argon2id is unavailable at runtime | `PasswordEncoderConfig` |
| BR-014 | Email is normalized to lowercase before persistence | Domain — `EmailAddress` |
| BR-015 | Newly registered users are active by default | Domain — `User` |
| BR-016 | Newly registered users start with no roles granted (explicit grant required) | Domain — `User` |
| BR-017 | Registering a user publishes a `UserRegisteredEvent` | Use case — `RegisterUserUseCase` |
| BR-018 | Registering a duplicate email returns 422, not a generic error | Use case — `EmailAlreadyRegisteredException` |
| BR-019 | Granting a role requires the target user to exist (404 otherwise) | Use case — `GrantRoleUseCase` |
| BR-020 | Only roles defined by the `Role` enum (`DEVELOPER`, `ADMINISTRATOR`, `MANAGER`, `COLLABORATOR`) may be granted; an unknown value is rejected as 400 before the use case runs | Controller — Jackson enum deserialization |
| BR-021 | Deactivating a user requires the user to exist (404 otherwise) | Use case — `DeactivateUserUseCase` |
| BR-022 | Deactivating an already-inactive user is idempotent (no error) | Domain — `User.deactivate()` |

## Company (`/api/v1/companies`)

| ID | Rule | Enforced by |
|----|------|-------------|
| BR-023 | Legal name is required, max 150 characters | DTO — `CreateCompanyRequest` |
| BR-024 | Trade name is required, max 150 characters | DTO — `CreateCompanyRequest` |
| BR-025 | CNPJ is required | DTO — `CreateCompanyRequest` |
| BR-026 | CNPJ must contain exactly 14 digits after stripping formatting characters | Domain — `DocumentNumber.cnpj()` |
| BR-027 | CNPJ must pass the official Brazilian check-digit algorithm | DTO — `@ValidCnpj` |
| BR-028 | CNPJ must be unique among registered companies | Use case — `RegisterCompanyUseCase` |
| BR-029 | Company size, if provided, must be one of `MEI`, `MICRO`, `SMALL` | DTO — enum binding |
| BR-030 | Street, number and city are required | DTO — `CreateCompanyRequest` |
| BR-031 | State is required and must be a valid Brazilian UF (2-letter code) | DTO — `@ValidBrazilianState` |
| BR-032 | Zip code is required and must match the Brazilian CEP format (`NNNNN-NNN`) | DTO — `@ValidBrazilianZipCode` |
| BR-033 | Country is required | DTO — `CreateCompanyRequest` |
| BR-034 | Newly registered companies are active by default | Domain — `Company` |
| BR-035 | Registering a duplicate CNPJ returns 422 | Use case — `CompanyAlreadyRegisteredException` |
| BR-036 | Deactivating a company requires the company to exist (404 otherwise) | Use case — `DeactivateCompanyUseCase` |
| BR-037 | Deactivating an already-inactive company is idempotent | Domain — `Company.deactivate()` |

## Accounting (`/api/v1/transactions`, `/api/v1/transfers`, `/api/v1/companies/{id}/financial-accounts`, `/api/v1/companies/{id}/categories`)

| ID | Rule | Enforced by |
|----|------|-------------|
| BR-038 | Financial account name is required, max 100 characters | DTO — `CreateFinancialAccountRequest` |
| BR-039 | Opening balance is required and cannot be negative | DTO — `@PositiveOrZero` |
| BR-040 | Currency defaults to BRL for accounts created through the API | Controller — `FinancialAccountController` |
| BR-041 | `companyId` is required to create or list financial accounts | Controller path variable |
| BR-042 | Deactivating a financial account requires it to exist (404 otherwise) | Use case — `DeactivateFinancialAccountUseCase` |
| BR-043 | Category name is required, max 60 characters | DTO — `CreateCategoryRequest` |
| BR-044 | Category type is required (`INCOME`, `EXPENSE` or `TRANSFER`) | DTO — `CreateCategoryRequest` |
| BR-045 | `financialAccountId` is required to record a transaction | DTO — `CreateTransactionRequest` |
| BR-046 | `categoryId` is required to record a transaction | DTO — `CreateTransactionRequest` |
| BR-047 | Transaction type is required | DTO — `CreateTransactionRequest` |
| BR-048 | Transaction amount is required and must be strictly positive | DTO — `@Positive` |
| BR-049 | Monetary amounts are scaled to the currency's decimal precision (2 for BRL), never floating point | Domain — `Money` |
| BR-050 | `occurredOn` is required | DTO — `CreateTransactionRequest` |
| BR-051 | `occurredOn` cannot be a future date | DTO — `@PastOrPresent` |
| BR-052 | `occurredOn` cannot be older than 5 years | DTO — `@NotOlderThan(years = 5)` |
| BR-053 | Description is capped at 255 characters | DTO — `@Size(max = 255)` |
| BR-054 | The transaction's category type must match the transaction's own type (e.g. an `EXPENSE` category cannot be used for an `INCOME` transaction) | Use case — `RecordTransactionUseCase` |
| BR-055 | `TRANSFER` type transactions are rejected on the single-account endpoint; transfers must go through `POST /api/v1/transfers` so both legs update atomically | Controller — `TransactionController` |
| BR-056 | Recording a transaction against a non-existent financial account or category fails with 404 | Use case — `RecordTransactionUseCase` |
| BR-057 | A financial account can never be debited below zero (insufficient balance) | Domain — `FinancialAccount.debit()` |
| BR-058 | A transfer requires two distinct accounts (source ≠ destination) | DTO — `@FieldsNotEqual` |
| BR-059 | Transfer amount must be strictly positive | DTO — `@Positive` |
| BR-060 | A transfer debits the source and credits the destination atomically (single transaction) | Use case — `TransferFundsUseCase` |
| BR-061 | A transfer fails if the source account has insufficient balance | Domain — `FinancialAccount.debit()` |

## Billing (`/api/v1/companies/{id}/parties`, `/api/v1/invoices`)

| ID | Rule | Enforced by |
|----|------|-------------|
| BR-062 | Party name is required, max 150 characters | DTO — `CreatePartyRequest` |
| BR-063 | Party document is required and must be a valid CPF/CNPJ matching the declared document type | DTO — `@ValidPartyDocument` |
| BR-064 | Party email is required and must be syntactically valid | DTO — `CreatePartyRequest` |
| BR-065 | Party type is required (`CUSTOMER` or `SUPPLIER`) | DTO — `CreatePartyRequest` |
| BR-066 | `companyId` is required to create or list parties | Controller path variable |
| BR-067 | `companyId`, `partyId` and `direction` are required to issue an invoice | DTO — `CreateInvoiceRequest` |
| BR-068 | The party referenced by an invoice must exist (404 otherwise) | Use case — `IssueInvoiceUseCase` |
| BR-069 | Installment amounts list must not be empty | DTO — `@NotEmpty` |
| BR-070 | An invoice is capped at 60 installments | DTO — `@Size(max = 60)` |
| BR-071 | Each installment amount must be strictly positive | Use case — `IssueInvoiceUseCase` |
| BR-072 | `firstDueDate` is required and cannot be in the past | DTO — `@FutureOrPresent` |
| BR-073 | Installments are due monthly, starting from `firstDueDate` | Use case — `IssueInvoiceUseCase` |
| BR-074 | Newly issued invoices start with status `OPEN` | Domain — `Invoice` |
| BR-075 | Registering a payment requires the invoice to exist (404 otherwise) | Use case — `RegisterPaymentUseCase` |
| BR-076 | Registering a payment requires the installment to belong to the invoice | Domain — `Invoice.registerPayment()` |
| BR-077 | A payment cannot be registered against a canceled invoice | Domain — `Invoice.registerPayment()` |
| BR-078 | `paidOn` is required and cannot be in the future | DTO — `@PastOrPresent` |
| BR-079 | An invoice moves to `PARTIALLY_PAID` once at least one, but not all, installments are paid | Domain — `Invoice.registerPayment()` |
| BR-080 | An invoice moves to `PAID` only once every installment is paid | Domain — `Invoice.registerPayment()` |
| BR-081 | Paying the final installment publishes an `InvoicePaidEvent` | Use case — `RegisterPaymentUseCase` |
| BR-082 | Canceling an invoice requires it to exist (404 otherwise) | Use case — `CancelInvoiceUseCase` |
| BR-083 | A fully paid invoice cannot be canceled | Domain — `Invoice.cancel()` |
| BR-084 | Canceling an already-canceled invoice is idempotent | Domain — `Invoice.cancel()` |

## Reporting (`/api/v1/companies/{id}/reports/cash-flow`)

| ID | Rule | Enforced by |
|----|------|-------------|
| BR-085 | `companyId` is required for the cash-flow report | Controller path variable |
| BR-086 | `from` and `to` dates are required; a missing or malformed value returns 400 before the handler runs | Controller — Spring date binding |
| BR-087 | `from` must not be after `to` | Controller — `ReportController` |
| BR-088 | The reporting window cannot exceed 366 days | Controller — `ReportController` |
| BR-089 | Only `INCOME` and `EXPENSE` transactions are included in the totals; `TRANSFER` is excluded to avoid double counting | Use case — `CashFlowReportService` |
| BR-090 | `netResult` is computed as `totalIncome − totalExpense` | Use case — `CashFlowReportService` |

## Budgets (`/api/v1/companies/{id}/budgets`)

| ID | Rule | Enforced by |
|----|------|-------------|
| BR-101 | `period` (a calendar month) is required and cannot be in the past | DTO — `@FutureOrPresent` |
| BR-102 | `limit` is required and must be strictly positive | DTO — `@Positive` |
| BR-103 | The referenced category must exist (404 otherwise) | Use case — `CreateBudgetUseCase` |
| BR-104 | Budgets can only be set for `EXPENSE` categories | Use case — `CreateBudgetUseCase` |
| BR-105 | Only one budget may exist per company, category and period | Use case — `CreateBudgetUseCase` |
| BR-106 | Budget status (`spent`, `remaining`, `overBudget`) is computed from `EXPENSE` transactions recorded in the budget's category during its period | Use case — `GetBudgetStatusUseCase` |

## Recurring Transactions (`/api/v1/companies/{id}/recurring-transactions`)

| ID | Rule | Enforced by |
|----|------|-------------|
| BR-107 | Amount is required and must be strictly positive | DTO — `@Positive` |
| BR-108 | Description is capped at 255 characters | DTO — `@Size(max = 255)` |
| BR-109 | Frequency is required (`WEEKLY`, `MONTHLY` or `YEARLY`) | DTO — `CreateRecurringTransactionRuleRequest` |
| BR-110 | `firstOccurrence` is required and cannot be in the past | DTO — `@FutureOrPresent` |
| BR-111 | The referenced category must exist and its type must match the rule's own type | Use case — `CreateRecurringTransactionRuleUseCase` |
| BR-112 | `TRANSFER` is rejected; recurring transfers are not supported | Controller — `RecurringTransactionRuleController` |
| BR-113 | `POST .../generate-due` materializes every active rule whose `nextOccurrence` is on or before today into a real transaction (reusing `RecordTransactionUseCase`, so the same balance rules apply), then advances it to its next occurrence | Use case — `GenerateDueRecurringTransactionsUseCase` |
| BR-114 | Deactivating a rule excludes it from future `generate-due` runs | Domain — `RecurringTransactionRule.deactivate()` |

## Notifications (`/api/v1/notifications`)

| ID | Rule | Enforced by |
|----|------|-------------|
| BR-115 | A notification is created for every `UserRegisteredEvent`, `TransactionRecordedEvent` and `InvoicePaidEvent` consumed from RabbitMQ | `UserRegisteredMessageListener`, `TransactionRecordedMessageListener`, `InvoicePaidMessageListener` |
| BR-116 | Notifications are listed most-recent first; `unreadOnly=true` filters out already-read ones | Controller — `NotificationController` |
| BR-117 | Marking a notification as read is idempotent | Domain — `Notification.markAsRead()` |
| BR-118 | Marking a non-existent notification as read fails with 404 | Use case — `MarkNotificationAsReadUseCase` |

## Authorization profiles

Four authentication/authorization profiles are defined by the `Role` enum (`identity` domain).
Each carries a fixed, non-configurable set of `Permission`s (`RolePermissions`), embedded as
`PERMISSION_*` authorities in the JWT issued by `POST /api/v1/auth/login` and re-derived from the
authenticated user's roles for the OAuth2/PKCE login form, so both authentication paths enforce
the same rules.

| ID | Rule | Enforced by |
|----|------|-------------|
| BR-119 | `DEVELOPER` holds every `Permission` (`READ`, `CREATE`, `UPDATE`, `DELETE`, `APPROVE`, `DEBUG`) — full access plus debug-mode tooling unavailable to any other role | Domain — `RolePermissions` |
| BR-120 | `ADMINISTRATOR` holds `READ`/`CREATE`/`UPDATE`/`DELETE`/`APPROVE` — full access to business operations, no debug tooling | Domain — `RolePermissions` |
| BR-121 | `MANAGER` holds `READ`/`CREATE`/`UPDATE`/`APPROVE` — can add and change records, and approve changes (e.g. registering invoice payments), but cannot delete/deactivate | Domain — `RolePermissions` |
| BR-122 | `COLLABORATOR` holds `READ`/`CREATE`/`UPDATE` only — can add and change records, but cannot approve or delete/deactivate them | Domain — `RolePermissions` |
| BR-123 | Every business-record endpoint (companies, financial accounts, categories, budgets, transactions, transfers, recurring rules, parties, invoices) requires the caller to hold the matching `Permission` authority, returning 403 otherwise; `PATCH /api/v1/invoices/{id}/payments` requires `APPROVE`, every `.../deactivate` and `.../cancel` endpoint requires `DELETE` | `@PreAuthorize` — business controllers |
| BR-124 | Granting a role or deactivating a user account requires the `DEVELOPER` or `ADMINISTRATOR` role, returning 403 otherwise | `@PreAuthorize` — `GrantRoleUseCase`, `DeactivateUserUseCase` |
| BR-125 | `GET /api/v1/debug/info`, and the `X-Debug-Request-Id`/`X-Debug-Duration-Ms` response headers added to every request, are only available to callers holding the `DEBUG` permission (`DEVELOPER` role) | `@PreAuthorize` — `DebugController`; `DebugModeFilter` |
| BR-126 | An authenticated caller lacking the required permission/role receives `403 Forbidden` with a structured `ApiError` body, not a stack trace or generic 500 | `GlobalExceptionHandler` |

## Cross-cutting

| ID | Rule | Enforced by |
|----|------|-------------|
| BR-091 | All monetary amounts are represented as `BigDecimal` with an explicit `Currency`, never floating point | Domain — `Money` |
| BR-092 | Every business-rule violation returns `422 Unprocessable Entity` with a structured `ApiError` body | `GlobalExceptionHandler` |
| BR-093 | Every "entity not found" error returns `404 Not Found` with a structured `ApiError` body | `GlobalExceptionHandler` |
| BR-094 | Every request payload is validated (`@Valid`) at the controller boundary before reaching application services | Controllers |
| BR-095 | Password hashes never appear in API response DTOs (`UserDto` excludes `hashedPassword`) | `UserMapper` |
| BR-096 | Every persisted entity carries `createdAt`/`updatedAt` audit timestamps | `AuditableEntity` |
| BR-097 | Every aggregate identifier is a UUID, assigned client-side by the domain layer, never a sequential integer | Domain constructors |
| BR-098 | Any non-password hash (checksums, fingerprints, idempotency keys) must use SHA3-512 | `Sha3512Hasher` |
| BR-099 | Passwords are never stored in plaintext, only as Argon2id/PBKDF2 hashes | `PasswordEncoderConfig` |
| BR-100 | Aggregate identifiers are immutable once assigned; JPA entities never rely on database-generated ids, so re-saving an already-persisted aggregate updates its row instead of inserting a duplicate | `BaseEntity`, all `*JpaMapper` classes |

## Known gaps (not covered by this catalog)

- `spring-boot-starter-ldap` is on the classpath but unconfigured — no LDAP authentication
  provider is wired up.
- The Authorization Server (`AuthorizationServerConfig`, `/oauth2/*`) issues its own RSA-signed
  access tokens for the PKCE flow, but the API's resource-server chain (`SecurityConfig`) only
  validates the Ed25519 JWTs issued by `POST /api/v1/auth/login`; OAuth2-issued tokens cannot yet
  be used as a bearer credential against `/api/v1/**`.
- The notification feed (`/api/v1/notifications`) is global, not scoped to a user or company —
  there is no session/current-user concept to scope it by yet, consistent with the authentication
  gap above.
- `POST /api/v1/companies/{id}/recurring-transactions/generate-due` must currently be triggered
  manually (or by an external scheduler hitting the endpoint); there is no in-process scheduled job
  calling it automatically.
