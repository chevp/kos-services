package dev.kosmos.kos.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Generic Business Object — Vorbild: iris' Entity+Component (FrostEntity.hpp),
 * eigenstaendig reimplementiert (siehe docs/ideas/studio-platform-migrationsplan.md Phase 1/2).
 * {@code type} ist ein freier String (z.B. "customer", "workflow", "invoice");
 * feste Typen mit eigenen Feldern folgen erst in Phase 3.
 */
@Entity
@Table(name = "studio_object")
public class StudioObject {

    @Id
    private String id;

    @Column(nullable = false, length = 64)
    private String type;

    @Column(nullable = false, length = 64)
    private String namespace;

    @Column(nullable = false, length = 128)
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, Object> attributes = Map.of();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private List<ObjectComponent> components = List.of();

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID().toString();
        createdAt = updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public String getId() { return id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes != null ? attributes : Map.of(); }

    public List<ObjectComponent> getComponents() { return components; }
    public void setComponents(List<ObjectComponent> components) { this.components = components != null ? components : List.of(); }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public StudioObjectSummary toSummary() {
        return new StudioObjectSummary(id, type, namespace, name, updatedAt);
    }
}
