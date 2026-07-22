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
| Accounting | `accounting` | Financial accounts, categories, income/expense/transfer transactions                                                                                      |
| Billing    | `billing`    | Customers/suppliers, invoices and installments (accounts receivable/payable)                                                                              |
| Reporting  | `reporting`  | Read-side queries such as the cash-flow summary                                                                                                           |

```
src/main/java/br/com/nischor/ledgerxbackend/
├── shared/
├── identity/
├── company/
├── accounting/
├── billing/
└── reporting/
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
until a migration tool (Flyway/Liquibase) is introduced.
