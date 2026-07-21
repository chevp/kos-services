package dev.kosmos.kos.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * One execution run of a WorkflowGraph (Phase 5). Tracks state only —
 * {@code currentNodeId} and {@code context} — it does not itself interpret
 * node types or edge conditions; that is the Agent/Workflow Runtime's job
 * (Phase 6), which drives execution by calling advance()/complete()/fail().
 */
@Entity
@Table(name = "workflow_execution")
public class WorkflowExecution {

    @Id
    private String id;

    @Column(name = "graph_id", nullable = false, length = 36)
    private String graphId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private WorkflowExecutionStatus status;

    @Column(name = "current_node_id", length = 64)
    private String currentNodeId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, Object> context = Map.of();

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID().toString();
        if (status == null) status = WorkflowExecutionStatus.RUNNING;
        if (startedAt == null) startedAt = OffsetDateTime.now();
    }

    public String getId() { return id; }

    public String getGraphId() { return graphId; }
    public void setGraphId(String graphId) { this.graphId = graphId; }

    public WorkflowExecutionStatus getStatus() { return status; }
    public void setStatus(WorkflowExecutionStatus status) { this.status = status; }

    public String getCurrentNodeId() { return currentNodeId; }
    public void setCurrentNodeId(String currentNodeId) { this.currentNodeId = currentNodeId; }

    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context != null ? context : Map.of(); }

    public OffsetDateTime getStartedAt() { return startedAt; }

    public OffsetDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(OffsetDateTime finishedAt) { this.finishedAt = finishedAt; }
}
