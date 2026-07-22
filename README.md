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

## Running locally

```
docker compose up -d   # Postgres, RabbitMQ, Grafana LGTM
./gradlew bootRun
```
