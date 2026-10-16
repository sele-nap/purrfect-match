# Purrfect Match

A REST API for a cat shelter: cat profiles and adoption requests.

**Stack:** Java 17 · Spring Boot 4 · JPA · Flyway · H2 / PostgreSQL

## Run

```bash
./mvnw spring-boot:run   # starts on :8080 with demo data
./mvnw verify            # runs the tests
```

## API

|                                                                     |                       |
| ------------------------------------------------------------------- | --------------------- |
| `GET/POST /api/v1/cats`                                             | List or register cats |
| `GET/PUT /api/v1/cats/{id}`                                         | Get or update a cat   |
| `POST /api/v1/cats/{id}/adoption-requests`                          | Apply to adopt        |
| `POST /api/v1/adoption-requests/{id}/approve \| reject \| complete` | Process a request     |

A cat goes `AVAILABLE → RESERVED → ADOPTED`, and can only be reserved by one request at a time.
