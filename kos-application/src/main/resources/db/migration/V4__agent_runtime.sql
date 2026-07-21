-- Agent Runtime: Agent/Goal/Task/Memory (Phase 6, docs/ideas/studio-platform-migrationsplan.md)
CREATE TABLE agent (
    id         VARCHAR(36)  PRIMARY KEY,
    name       VARCHAR(128) NOT NULL,
    namespace  VARCHAR(64)  NOT NULL,
    status     VARCHAR(16)  NOT NULL DEFAULT 'IDLE',
    tools      JSONB        NOT NULL DEFAULT '[]',
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_agent_namespace ON agent(namespace);

CREATE TABLE goal (
    id          VARCHAR(36)  PRIMARY KEY,
    agent_id    VARCHAR(36)  NOT NULL,
    description VARCHAR(512) NOT NULL,
    status      VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_goal_agent_id ON goal(agent_id);

CREATE TABLE agent_task (
    id          VARCHAR(36)  PRIMARY KEY,
    agent_id    VARCHAR(36)  NOT NULL,
    goal_id     VARCHAR(36),
    description VARCHAR(512) NOT NULL,
    status      VARCHAR(16)  NOT NULL DEFAULT 'PENDING',
    input       JSONB        NOT NULL DEFAULT '{}',
    output      JSONB        NOT NULL DEFAULT '{}',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_agent_task_agent_id ON agent_task(agent_id);
CREATE INDEX idx_agent_task_status ON agent_task(status);

CREATE TABLE agent_memory_entry (
    id          VARCHAR(36)  PRIMARY KEY,
    agent_id    VARCHAR(36)  NOT NULL,
    memory_key  VARCHAR(128) NOT NULL,
    value_json  JSONB        NOT NULL DEFAULT '{}',
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_agent_memory_agent_key ON agent_memory_entry(agent_id, memory_key);
