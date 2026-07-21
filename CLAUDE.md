# kos-services

Studio Server (Control Plane) for the generic Studio Platform — see
docs/ideas/studio-platform-migrationsplan.md. Backend for both `frost-hub`
(Studio client) and `platform/koshub` (infra hub); not tied to either.

Two model families live side by side:
- **Project** — the original game-agnostic project registry, unchanged.
  Every project points to a game-specific runtime (e.g. nuna-services) via
  `serviceEndpoint`.
- **StudioObject / ObjectComponent / StudioEvent** — the generic Business
  Object / Component / Event model (Phase 2 of the migration plan). Vorbild:
  iris' Entity+Component+Event (see plan's Phase 1 table), independently
  reimplemented — no dependency on runtime/iris. `type` is a free string for
  now, validated on create/update against `ObjectTypeRegistry` — a fixed
  in-memory catalog of known types (pdf, excel, prompt, workflow,
  knowledge-base, contract, customer, invoice, sql-query, rest-endpoint)
  with required-attribute checks (Phase 3). Components attached to a
  StudioObject are likewise validated against `ComponentTypeRegistry`
  (metadata, permissions, version, tags, state, workflow, memory,
  knowledge, connector — Phase 4). Both registries are pluggable only in
  spirit for now — a real plugin mechanism (types contributed by Packages)
  is Phase 9.
- **WorkflowGraph / WorkflowNode / WorkflowEdge / WorkflowExecution** —
  Node/Edge/Execution infrastructure for workflows (Phase 5). Vorbild:
  iris' Scene/SceneNode tree (shape only — hierarchy of typed nodes), fully
  independent implementation. `WorkflowExecution` only tracks state
  (`currentNodeId`, `context`, `status`) — it does not interpret node types
  or edge conditions itself; `advance()`/`complete()`/`fail()` are meant to
  be driven by the Agent/Workflow Runtime (Phase 6, below).
- **Agent / Goal / AgentTask / AgentMemoryEntry** — Agent Runtime core
  objects (Phase 6). `Agent.tools` is validated against `ToolRegistry` (a
  fixed catalog scoped to what kos-services itself exposes today —
  read/write-object, publish-event, run-workflow; external Connectors
  extend this in Phase 7, not replace it). `AgentTask` only tracks the
  PENDING→RUNNING→DONE/FAILED lifecycle (the plan's minimal "Executor") —
  the actual planning/reasoning that decides what a task does runs outside
  kos-services, in an external agent process; no Planner/Reasoner
  abstraction is implemented here. `AgentMemoryEntry` is a flat per-agent
  key/value store (the plan's State Store), no vector search or memory
  tiers. Column is `value_json`, not `value` — `value` is an H2/ANSI
  reserved word and broke the test context's schema generation. Scheduler and Message Queue (also named in the plan's Phase 6
  runtime building blocks) are **not implemented** — task creation/start
  is caller-driven, there is no background scheduler yet.
- **ConnectorConfig / Connector** — uniform `read()`/`write()`/`execute()`/
  `subscribe()` interface for external systems (Phase 7). Only `rest`
  (`RestConnector`, generic HTTP via Spring `RestClient`) is a real
  implementation; `git`, `slack`, `sap`, `jira`, `teams`, `filesystem`,
  `database`, `email`, and `iris` all resolve to `StubConnector`, which
  fails loudly (`501 Not Implemented`) rather than silently no-op'ing.
  The iris-Connector in particular still needs an actual RPC/WebSocket
  client to irisdaemon — not built yet. `GET /api/connector-types` shows
  which types are real vs. stubbed. Secrets: `ConnectorConfig.config` is
  free-form JSON with no secrets manager behind it yet — don't put raw
  credentials in there for anything beyond local dev.
  Events are persisted (`studio_event`) and additionally published
  in-process via Spring's `ApplicationEventPublisher`
  (`StudioEventPublished`) — Vorbild: iris' `IExtensionHost.publishEvent`/
  `subscribeToEvent`. WebSocket push to clients is not implemented yet.

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
| GET | `/api/objects?type=` | list StudioObjects (summary), optional type filter |
| POST | `/api/objects` | create StudioObject |
| GET | `/api/objects/{id}` | get full StudioObject (incl. components) |
| PUT | `/api/objects/{id}` | update StudioObject |
| DELETE | `/api/objects/{id}` | delete StudioObject |
| GET | `/api/events?objectId=` | list StudioEvents, optional objectId filter |
| GET | `/api/object-types` | list known Business-Object types (key, label, required attributes) |
| GET | `/api/component-types` | list known Component types (key, label, required data keys) |
| GET | `/api/workflow-graphs` | list WorkflowGraphs |
| POST | `/api/workflow-graphs` | create WorkflowGraph (nodes + edges) |
| GET | `/api/workflow-graphs/{id}` | get WorkflowGraph |
| PUT | `/api/workflow-graphs/{id}` | update WorkflowGraph |
| DELETE | `/api/workflow-graphs/{id}` | delete WorkflowGraph |
| GET | `/api/workflow-executions?graphId=` | list executions for a graph |
| POST | `/api/workflow-executions` | start execution (graphId + context) |
| GET | `/api/workflow-executions/{id}` | get execution |
| POST | `/api/workflow-executions/{id}/advance` | move currentNodeId along an existing edge |
| POST | `/api/workflow-executions/{id}/complete` | mark execution COMPLETED |
| POST | `/api/workflow-executions/{id}/fail` | mark execution FAILED |
| GET | `/api/agents` | list Agents |
| POST | `/api/agents` | create Agent (name, namespace, tools) |
| GET | `/api/agents/{id}` | get Agent |
| PUT | `/api/agents/{id}` | update Agent |
| POST | `/api/agents/{id}/status` | set Agent status (IDLE/RUNNING/PAUSED) |
| DELETE | `/api/agents/{id}` | delete Agent |
| GET | `/api/goals?agentId=` | list Goals for an Agent |
| POST | `/api/goals` | create Goal |
| GET | `/api/goals/{id}` | get Goal |
| POST | `/api/goals/{id}/status` | set Goal status (ACTIVE/ACHIEVED/ABANDONED) |
| GET | `/api/agent-tasks?agentId=` | list Tasks for an Agent |
| POST | `/api/agent-tasks` | create Task (PENDING) |
| GET | `/api/agent-tasks/{id}` | get Task |
| POST | `/api/agent-tasks/{id}/start` | PENDING → RUNNING |
| POST | `/api/agent-tasks/{id}/complete` | RUNNING → DONE (+ output) |
| POST | `/api/agent-tasks/{id}/fail` | RUNNING → FAILED (+ reason) |
| GET | `/api/agents/{agentId}/memory` | list an Agent's memory entries |
| GET/PUT/DELETE | `/api/agents/{agentId}/memory/{key}` | get/set/delete one memory entry |
| GET | `/api/tools` | list known Tools (key, label, description) |
| GET | `/api/connectors?type=` | list ConnectorConfigs |
| POST | `/api/connectors` | create ConnectorConfig |
| GET | `/api/connectors/{id}` | get ConnectorConfig |
| PUT | `/api/connectors/{id}` | update ConnectorConfig (name/config/enabled) |
| DELETE | `/api/connectors/{id}` | delete ConnectorConfig |
| POST | `/api/connectors/{id}/read` | Connector.read() passthrough |
| POST | `/api/connectors/{id}/write` | Connector.write() passthrough |
| POST | `/api/connectors/{id}/execute` | Connector.execute(action, params) passthrough |
| GET | `/api/connector-types` | list known Connector types + whether implemented |
| GET | `/api/status` | service health (public, no auth) |

## DB migrations

Flyway migrations: `kos-application/src/main/resources/db/migration/`  
Naming: `V<N>__<slug>.sql`

## Module structure

```
kos-domain/      — JPA entities (Project, StudioObject, StudioEvent, WorkflowGraph, WorkflowExecution, Agent, Goal, AgentTask, AgentMemoryEntry, ConnectorConfig), repositories, domain types
kos-application/ — Spring Boot main, REST controllers, services, config
```

`mvn spring-boot:run` must be run from `kos-application/`, not the root.
