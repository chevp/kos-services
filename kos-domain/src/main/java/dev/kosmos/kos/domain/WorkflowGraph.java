package dev.kosmos.kos.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Workflow Graph — Node/Edge infrastructure (Phase 5,
 * docs/ideas/studio-platform-migrationsplan.md). Conceptually analogous to
 * iris' Scene/SceneNode tree (a hierarchy of typed nodes), but independently
 * implemented for business workflows, not scene rendering.
 */
@Entity
@Table(name = "workflow_graph")
public class WorkflowGraph {

    @Id
    private String id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 64)
    private String namespace;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private List<WorkflowNode> nodes = List.of();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private List<WorkflowEdge> edges = List.of();

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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }

    public List<WorkflowNode> getNodes() { return nodes; }
    public void setNodes(List<WorkflowNode> nodes) { this.nodes = nodes != null ? nodes : List.of(); }

    public List<WorkflowEdge> getEdges() { return edges; }
    public void setEdges(List<WorkflowEdge> edges) { this.edges = edges != null ? edges : List.of(); }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
