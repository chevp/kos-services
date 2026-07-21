package dev.kosmos.kos.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Persisted domain event — Vorbild: iris' Event/IExtensionHost.publishEvent
 * (extension-system/IExtensionHost.hpp), eigenstaendig reimplementiert.
 * {@code objectId} ist optional, da nicht jedes Event an ein StudioObject
 * gebunden ist (z.B. System-/Connector-Events).
 */
@Entity
@Table(name = "studio_event")
public class StudioEvent {

    @Id
    private String id;

    @Column(nullable = false, length = 64)
    private String type;

    @Column(name = "object_id", length = 36)
    private String objectId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, Object> payload = Map.of();

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = OffsetDateTime.now();
    }

    public String getId() { return id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getObjectId() { return objectId; }
    public void setObjectId(String objectId) { this.objectId = objectId; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload != null ? payload : Map.of(); }

    public OffsetDateTime getOccurredAt() { return occurredAt; }
}
