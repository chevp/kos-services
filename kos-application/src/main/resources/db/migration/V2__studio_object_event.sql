-- Studio Object/Component/Event model (Phase 2, docs/ideas/studio-platform-migrationsplan.md)
CREATE TABLE studio_object (
    id         VARCHAR(36)  PRIMARY KEY,
    type       VARCHAR(64)  NOT NULL,
    namespace  VARCHAR(64)  NOT NULL,
    name       VARCHAR(128) NOT NULL,
    attributes JSONB        NOT NULL DEFAULT '{}',
    components JSONB        NOT NULL DEFAULT '[]',
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_studio_object_type ON studio_object(type);
CREATE INDEX idx_studio_object_namespace ON studio_object(namespace);

CREATE TABLE studio_event (
    id          VARCHAR(36)  PRIMARY KEY,
    type        VARCHAR(64)  NOT NULL,
    object_id   VARCHAR(36),
    payload     JSONB        NOT NULL DEFAULT '{}',
    occurred_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_studio_event_object_id ON studio_event(object_id);
CREATE INDEX idx_studio_event_occurred_at ON studio_event(occurred_at);
