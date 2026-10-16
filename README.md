# Purrfect Match

Cat shelter REST API: cat profiles and the adoption workflow.
Java 17 · Spring Boot 4.1 · Spring Data JPA · Flyway · H2 (dev) / PostgreSQL (prod).

## Getting started

```bash
./mvnw spring-boot:run          # dev profile: in-memory H2 + 3 demo cats
./mvnw verify                   # compile + run all tests
```

- API: http://localhost:8080/api/v1/cats
- Health: http://localhost:8080/actuator/health
- H2 console (dev): http://localhost:8080/h2-console — JDBC URL `jdbc:h2:mem:purrfect`, user `sa`

In production:

```bash
SPRING_PROFILES_ACTIVE=prod DB_URL=jdbc:postgresql://host:5432/shelter \
DB_USERNAME=... DB_PASSWORD=... java -jar target/purrfect-match-0.0.1-SNAPSHOT.jar
```

## Endpoints

| Method | Path | Purpose |
|---|---|---|
| GET  | `/api/v1/cats?status=AVAILABLE&page=0&size=20` | List cats (filtering and pagination) |
| GET  | `/api/v1/cats/{id}` | Get a cat |
| POST | `/api/v1/cats` | Register a cat's arrival |
| PUT  | `/api/v1/cats/{id}` | Update a profile (unless adopted) |
| POST | `/api/v1/cats/{id}/adoption-requests` | Submit an adoption request |
| GET  | `/api/v1/cats/{id}/adoption-requests` | Requests for a cat |
| GET  | `/api/v1/adoption-requests?status=SUBMITTED` | Requests by status |
| POST | `/api/v1/adoption-requests/{id}/approve` | Approve → cat becomes `RESERVED` |
| POST | `/api/v1/adoption-requests/{id}/reject` | Reject (releases the cat if reserved) |
| POST | `/api/v1/adoption-requests/{id}/complete` | Complete → cat becomes `ADOPTED`, other requests rejected |

## Business rules

- Cat: `AVAILABLE → RESERVED → ADOPTED`. Request: `SUBMITTED → APPROVED → COMPLETED`, or `REJECTED`.
- Only one approved request per cat at a time; no requests at all on an adopted cat.
- No two open requests for the same cat from the same email.
- Optimistic locking (`@Version`) on the cat guards against concurrent approvals.

Errors follow RFC 9457 (`application/problem+json`): 400 for validation (with per-field `errors`),
404 for missing resources, 409 for business rule violations or concurrent modifications.

## Project layout

```
src/main/java/io/github/selenap/purrfectmatch/
├── cat/           entity, repository, service, controller, DTOs (records)
├── adoption/      adoption requests and their state transitions
└── common/error/  business exceptions + global ProblemDetail handler
src/main/resources/
├── application.yml / -dev.yml / -prod.yml
└── db/migration/  Flyway migrations (schema), db/dev-data/ (demo data)
```

Code is organised by feature (package per domain) rather than by technical layer. Business rules
live in the entities, so they can be tested without Spring (`AdoptionRequestTest`).
