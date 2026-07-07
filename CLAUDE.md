# kos-services

Game-agnostic project registry — stores and serves Project metadata for koshub.
Every project points to a game-specific runtime (e.g. nuna-services) via `serviceEndpoint`.

## Stack

- Java 21 + Spring Boot 3.3
- PostgreSQL 16 (via JPA + Flyway), port **5435** locally
- Port: **8085** (avoids conflict with nuna-services:8080 and kaga:8090)

## Local dev

```bash
# 1. Start PostgreSQL only (from this directory)
docker compose up kos-db -d

# 2. Run the service (MUST be run from kos-application submodule)
cd kos-application && mvn spring-boot:run

# 3. Or: full stack in Docker
docker compose up
```

## API

Base URL: `http://localhost:8085`  
Auth: HTTP Basic — user `kosmos`, password from `KOS_ADMIN_PASSWORD` (default: `changeme`)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/projects` | list all (summary) |
| POST | `/api/projects` | create project |
| GET | `/api/projects/{id}` | get full project |
| PUT | `/api/projects/{id}` | update project |
| DELETE | `/api/projects/{id}` | delete project |
| POST | `/api/projects/{id}/activate` | status → ACTIVE |
| POST | `/api/projects/{id}/archive` | status → ARCHIVED |
| GET | `/api/projects/{id}/health` | proxy → serviceEndpoint/api/status |
| GET | `/api/status` | service health (public, no auth) |

## DB migrations

Flyway migrations: `kos-application/src/main/resources/db/migration/`  
Naming: `V<N>__<slug>.sql`

## Module structure

```
kos-domain/      — JPA entities (Project), repository, domain types
kos-application/ — Spring Boot main, REST controllers, services, config
```

`mvn spring-boot:run` must be run from `kos-application/`, not the root.
