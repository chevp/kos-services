-- Connector Layer (Phase 7, docs/ideas/studio-platform-migrationsplan.md)
CREATE TABLE connector_config (
    id         VARCHAR(36)  PRIMARY KEY,
    type       VARCHAR(32)  NOT NULL,
    name       VARCHAR(128) NOT NULL,
    namespace  VARCHAR(64)  NOT NULL,
    config     JSONB        NOT NULL DEFAULT '{}',
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_connector_config_type ON connector_config(type);
CREATE INDEX idx_connector_config_namespace ON connector_config(namespace);
