package dev.kosmos.kos.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * One key/value slot of an Agent's Memory (Phase 6) — the plan's State
 * Store building block, scoped per agent. Deliberately a flat key/value
 * store (no vector search, no memory types/tiers) — that is future work if
 * an agent needs it, not part of this skeleton.
 */
@Entity
@Table(
    name = "agent_memory_entry",
    uniqueConstraints = @UniqueConstraint(columnNames = {"agent_id", "memory_key"})
)
public class AgentMemoryEntry {

    @Id
    private String id;

    @Column(name = "agent_id", nullable = false, length = 36)
    private String agentId;

    @Column(name = "memory_key", nullable = false, length = 128)
    private String key;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "value_json", nullable = false)
    private Map<String, Object> value = Map.of();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID().toString();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public String getId() { return id; }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public Map<String, Object> getValue() { return value; }
    public void setValue(Map<String, Object> value) { this.value = value != null ? value : Map.of(); }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
