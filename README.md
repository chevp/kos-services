# kos-services

Kosmos backend infrastructure — Java 21 / Spring Boot 3 service layer.

## Run

```bash
mvn spring-boot:run
```

API available at `http://localhost:8085/api/status`.

## Stack

- Java 21
- Spring Boot 3.3 (Web, JPA, Security, Actuator)
- H2 in-memory DB (swap for Postgres in production)