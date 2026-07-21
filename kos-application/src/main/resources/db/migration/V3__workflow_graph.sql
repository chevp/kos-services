-- Workflow Graph: Node/Edge/Execution (Phase 5, docs/ideas/studio-platform-migrationsplan.md)
CREATE TABLE workflow_graph (
    id         VARCHAR(36)  PRIMARY KEY,
    name       VARCHAR(128) NOT NULL,
    namespace  VARCHAR(64)  NOT NULL,
    nodes      JSONB        NOT NULL DEFAULT '[]',
    edges      JSONB        NOT NULL DEFAULT '[]',
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_workflow_graph_namespace ON workflow_graph(namespace);

CREATE TABLE workflow_execution (
    id              VARCHAR(36) PRIMARY KEY,
    graph_id        VARCHAR(36) NOT NULL,
    status          VARCHAR(16) NOT NULL DEFAULT 'RUNNING',
    current_node_id VARCHAR(64),
    context         JSONB       NOT NULL DEFAULT '{}',
    started_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    finished_at     TIMESTAMPTZ
);

CREATE INDEX idx_workflow_execution_graph_id ON workflow_execution(graph_id);
CREATE INDEX idx_workflow_execution_status ON workflow_execution(status);
