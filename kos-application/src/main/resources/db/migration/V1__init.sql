-- Projects: game-agnostic deployment units registered in kos-services
CREATE TABLE project (
    id               VARCHAR(36)   PRIMARY KEY,
    name             VARCHAR(64)   NOT NULL,
    namespace        VARCHAR(64)   NOT NULL,
    status           VARCHAR(16)   NOT NULL DEFAULT 'DRAFT',
    service_endpoint VARCHAR(512)  NOT NULL,
    bundle_ref       VARCHAR(512),
    settings         JSONB         NOT NULL DEFAULT '{}',
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_project_ns_name ON project(namespace, name);
CREATE INDEX idx_project_status ON project(status);
