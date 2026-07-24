# LedgerX Backend

LedgerX is an open-source financial management backend for micro and small
businesses (MSEs). It centralizes cash accounts, income/expense tracking,
accounts receivable/payable, and cash-flow reporting behind a REST API.

## Tech stack

- Java 25, Spring Boot 4
- Spring Data JPA/JDBC, PostgreSQL
- Spring Security (OAuth2 client, LDAP, JDBC sessions)
- Spring AMQP (RabbitMQ)
- OpenTelemetry

## Architecture

The codebase follows Domain-Driven Design, organized as one package per
bounded context. Each context is sliced into the classic DDD layers:

```
domain          business rules: entities, value objects, repository
                interfaces (ports), domain services, domain events
application     use cases orchestrating the domain, DTOs, mappers
infrastructure  adapters: JPA entities/repositories, messaging, config
interfaces      inbound adapters: REST controllers and request/response DTOs
```

Only `domain` is free of framework dependencies. `application` depends on
`domain` ports, never on `infrastructure` or `interfaces`. `infrastructure`
implements the ports declared in `domain`.

### Bounded contexts

| Context    | Package      | Responsibility                                                                                                                                            |
| ---------- | ------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Shared     | `shared`     | Cross-cutting kernel: value objects (`Money`, `DocumentNumber`, `EmailAddress`), base exceptions, domain events, base JPA entities, global error handling |
| Identity   | `identity`   | Users, roles and authentication                                                                                                                           |
| Company    | `company`    | Company/tenant registration and profile                                                                                                                   |
| Accounting | `accounting` | Financial accounts, categories, income/expense/transfer transactions, budgets, recurring transactions |
| Billing    | `billing`    | Customers/suppliers, invoices and installments (accounts receivable/payable)                                                                              |
| Reporting  | `reporting`  | Read-side queries such as the cash-flow summary                                                                                                           |
| Notification | `notification` | In-app notification feed populated from domain events published over RabbitMQ                                                                        |

```
src/main/java/br/com/nischor/ledgerxbackend/
├── shared/
├── identity/
├── company/
├── accounting/
├── billing/
├── reporting/
└── notification/
```

Each business context (`identity`, `company`, `accounting`, `billing`)
follows this internal layout:

```
<context>/
├── domain/
│   ├── model/          aggregates and entities
│   ├── valueobject/     value objects specific to the context
│   ├── repository/      repository interfaces (ports)
│   ├── service/         domain services (multi-aggregate rules)
│   ├── event/           domain events
│   └── exception/       domain-specific exceptions
├── application/
│   ├── usecase/         one class per use case
│   ├── dto/              application-layer DTOs
│   └── mapper/           domain <-> DTO mappers
├── infrastructure/
│   └── persistence/
│       ├── entity/       JPA entities
│       ├── repository/   Spring Data repositories + adapter implementing the domain port
│       └── mapper/       domain <-> JPA entity mappers
└── interfaces/
    └── rest/
        ├── controller/   REST controllers
        └── dto/          request/response payloads
```

`reporting` is read-only (CQRS-style) and therefore only has `application`
and `interfaces` layers, querying the `accounting` context's repositories
directly.

## Business rules

The API enforces 118 documented business rules (validation, uniqueness,
state transitions, invariants) across every context. See
[BUSINESS_RULES.md](BUSINESS_RULES.md) for the full catalog, each rule
cross-referenced from the controller that enforces it.

## API endpoints

All endpoints are versioned under `/api/v1`. See
[BUSINESS_RULES.md](BUSINESS_RULES.md) or Swagger UI for the validation and
business rules each one enforces.

### Identity — `/api/v1/users`

| Method | Path | Description |
| ------ | ---- | ------------ |
| POST | `/api/v1/users` | Register a new user |
| PATCH | `/api/v1/users/{userId}/roles` | Grant a role to a user |
| PATCH | `/api/v1/users/{userId}/deactivate` | Deactivate a user |

### Company — `/api/v1/companies`

| Method | Path | Description |
| ------ | ---- | ------------ |
| POST | `/api/v1/companies` | Register a new company |
| PATCH | `/api/v1/companies/{companyId}/deactivate` | Deactivate a company |

### Accounting — financial accounts, categories, transactions, transfers, budgets, recurring transactions

| Method | Path | Description |
| ------ | ---- | ------------ |
| POST | `/api/v1/companies/{companyId}/financial-accounts` | Create a financial account |
| GET | `/api/v1/companies/{companyId}/financial-accounts` | List financial accounts of a company |
| GET | `/api/v1/companies/{companyId}/financial-accounts/{accountId}` | Get a financial account by id |
| PATCH | `/api/v1/companies/{companyId}/financial-accounts/{accountId}/deactivate` | Deactivate a financial account |
| POST | `/api/v1/companies/{companyId}/categories` | Create an income/expense category |
| GET | `/api/v1/companies/{companyId}/categories` | List categories of a company |
| POST | `/api/v1/transactions` | Record an income or expense transaction |
| POST | `/api/v1/transfers` | Transfer funds between two financial accounts |
| POST | `/api/v1/companies/{companyId}/budgets` | Create a monthly budget for an expense category |
| GET | `/api/v1/companies/{companyId}/budgets` | List budgets of a company |
| GET | `/api/v1/companies/{companyId}/budgets/{budgetId}/status` | Get spent/remaining amount for a budget |
| PATCH | `/api/v1/companies/{companyId}/budgets/{budgetId}/deactivate` | Deactivate a budget |
| POST | `/api/v1/companies/{companyId}/recurring-transactions` | Create a recurring transaction rule |
| GET | `/api/v1/companies/{companyId}/recurring-transactions` | List recurring transaction rules of a company |
| POST | `/api/v1/companies/{companyId}/recurring-transactions/generate-due` | Materialize every rule that is currently due into real transactions |
| PATCH | `/api/v1/companies/{companyId}/recurring-transactions/{ruleId}/deactivate` | Deactivate a recurring transaction rule |

### Billing — parties, invoices

| Method | Path | Description |
| ------ | ---- | ------------ |
| POST | `/api/v1/companies/{companyId}/parties` | Create a customer/supplier party |
| GET | `/api/v1/companies/{companyId}/parties` | List parties of a company |
| POST | `/api/v1/invoices` | Issue an invoice with installments |
| GET | `/api/v1/invoices/{invoiceId}` | Get an invoice by id |
| PATCH | `/api/v1/invoices/{invoiceId}/payments` | Register a payment against an installment |
| PATCH | `/api/v1/invoices/{invoiceId}/cancel` | Cancel an invoice |

### Reporting — `/api/v1/companies/{id}/reports`

| Method | Path | Description |
| ------ | ---- | ------------ |
| GET | `/api/v1/companies/{companyId}/reports/cash-flow` | Cash-flow summary (income, expense, net result) for a date range |

### Notifications — `/api/v1/notifications`

| Method | Path | Description |
| ------ | ---- | ------------ |
| GET | `/api/v1/notifications` | List notifications, most recent first (`?unreadOnly=true` to filter) |
| PATCH | `/api/v1/notifications/{notificationId}/read` | Mark a notification as read |

## API documentation (OpenAPI / Swagger)

Every endpoint is documented with springdoc-openapi:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Raw OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Both paths are explicitly permitted by `SecurityConfig` so they can be
browsed without authentication.

## Authentication

Two independent authentication mechanisms are available:

- **Password login (`POST /api/v1/auth/login`)** — exchanges an email/password
  for an **Ed25519-signed (EdDSA)** JWT access token (`AuthController`,
  `LoginUseCase`, `JwtService` in `shared/infrastructure/security`). Send it
  back as `Authorization: Bearer <token>`; `JwtAuthenticationFilter` verifies
  the signature and populates roles as granted authorities on every request.
  The signing key pair is configured via `ledgerx.security.jwt.private-key`
  /`public-key` (Base64 DER); if left unset, a fresh key pair is generated at
  startup, which only works for a single, long-lived instance.
- **OAuth2 Authorization Code + PKCE (`/oauth2/authorize`, `/oauth2/token`,
  `/oauth2/jwks`, ...)** — a first-party Spring Authorization Server
  (`AuthorizationServerConfig`) for public clients (SPA/mobile) that cannot
  keep a client secret. PKCE is mandatory
  (`ClientSettings.requireProofKey(true)`); the registered client uses
  `client_authentication_method=none`. Configure the client id, redirect
  URIs and scopes under `ledgerx.security.oauth2.*`. These tokens are signed
  with a separate, ephemeral RSA key (regenerated every startup) and are
  unrelated to the Ed25519 JWTs above.

## Authorization profiles

Every authenticated user carries one or more `Role`s (`identity` domain model), each mapped to a
fixed set of `Permission`s by `RolePermissions`. Both authentication mechanisms above populate the
same `ROLE_*`/`PERMISSION_*` Spring Security authorities from this single mapping, and business
endpoints enforce them via `@PreAuthorize` (see BR-119..BR-126 in
[BUSINESS_RULES.md](BUSINESS_RULES.md) for the full endpoint-by-endpoint breakdown):

| Role | Permissions | Summary |
|------|-------------|---------|
| `DEVELOPER` | READ, CREATE, UPDATE, DELETE, APPROVE, DEBUG | Full access + debug mode |
| `ADMINISTRATOR` | READ, CREATE, UPDATE, DELETE, APPROVE | Full access |
| `MANAGER` | READ, CREATE, UPDATE, APPROVE | Add / change / approve changes |
| `COLLABORATOR` | READ, CREATE, UPDATE | Add / change |

Debug mode (`DEVELOPER` only) adds two things not available to any other role:

- `GET /api/v1/debug/info` — runtime/build diagnostics (`DebugController`).
- `X-Debug-Request-Id` / `X-Debug-Duration-Ms` response headers on every request
  (`DebugModeFilter`), to trace individual requests without an external APM tool.

A caller authenticated but lacking the required role/permission gets `403 Forbidden` with a
structured `ApiError` body (`GlobalExceptionHandler`), not a stack trace.

## TLS

The embedded server serves **HTTPS only**, restricted to **TLS 1.3 with a
TLS 1.2 fallback** (`server.ssl.enabled-protocols`). On every startup,
`TlsEnvironmentPostProcessor` generates a fresh self-signed RSA certificate
(`SelfSignedCertificateGenerator`, built on BouncyCastle) into a temporary
PKCS#12 keystore and wires it into `server.ssl.*` before the embedded Tomcat
factory reads those properties — no certificate needs to be provisioned
up front for local development.

Since the certificate is regenerated (and its keystore password rotated) on
every restart, it is **not suitable for production** or for any client that
needs to trust the certificate across restarts: set `server.ssl.key-store`
(and related `server.ssl.*` properties) explicitly to use a real certificate,
or set `ledgerx.security.tls.enabled=false` to disable TLS entirely (e.g.
behind a TLS-terminating reverse proxy/load balancer).

## Sample data seeding

On first startup against an empty database, `DatabaseSeeder`
(`shared/infrastructure/seed`) populates ~5000 realistic pt-BR records
(companies, users, financial accounts, categories, parties, invoices,
transactions) using [Datafaker](https://www.datafaker.net/) with the
`pt_BR` locale. CPF/CNPJ values are generated with valid check digits.

The whole seed runs in a single transaction, so a failure midway leaves the
database untouched instead of a partially-seeded state. Disable it with:

```yaml
ledgerx:
  seed:
    enabled: false
```

## Security standards

- **Passwords** are hashed with **Argon2id** (`PasswordEncoderConfig`,
  `shared/infrastructure/security`), which is provided as the `PasswordEncoder`
  bean used by `identity`. If Argon2id cannot be used at runtime, the encoder
  automatically falls back to **PBKDF2**. Encoded hashes are prefixed with
  `{argon2id}`/`{pbkdf2}` so both formats can always be verified.
- **Any other hash** (checksums, fingerprints, idempotency keys, etc.) must
  use **SHA3-512**, via `Sha3512Hasher` (`shared/infrastructure/security`).
  Do not use this for passwords — use the `PasswordEncoder` bean instead.

## Running locally

### Gradle

```
docker compose up -d postgres rabbitmq grafana-lgtm
./gradlew bootRun
```

`./gradlew build` and `./gradlew test` also need Postgres and RabbitMQ
running first: `LedgerxBackendApplicationTests.contextLoads()` boots the
full Spring context (real datasource, JDBC session-schema initializer,
AMQP topology), and there is no test profile with an embedded/in-memory
database. Start the dependencies with the command above before running
either task, or skip tests for a pure compile:

```
./gradlew build -x test
```

### Docker

The `Dockerfile` builds a self-contained runtime image (multi-stage,
`eclipse-temurin:25-jdk` → `eclipse-temurin:25-jre`, non-root user).

```
docker compose up -d --build
```

This starts the app together with Postgres, RabbitMQ and Grafana LGTM. The
app container reads its datasource/RabbitMQ connection from environment
variables (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`,
`RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USER`, `RABBITMQ_PASSWORD`), all
defaulted in `compose.yaml`. The API is exposed on `http://localhost:8080`.

`spring.jpa.hibernate.ddl-auto` is currently set to `update` as a stopgap
until a migration tool (Flyway/Liquibase) is introduced. `spring.session.jdbc.initialize-schema`
is set to `always` so the `SPRING_SESSION` table (required by
`spring-boot-starter-session-jdbc`) is created automatically.
