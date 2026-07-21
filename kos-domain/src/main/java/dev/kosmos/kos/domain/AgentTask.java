package dev.kosmos.kos.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * A unit of work for an Agent (Phase 6). {@code input}/{@code output} are
 * the Execution Context the plan calls for — free-form JSON handed to and
 * returned from whatever actually executes the task (an external agent
 * process). This entity only tracks the PENDING→RUNNING→DONE/FAILED
 * lifecycle (the "Executor" role, minimally): planning/reasoning that
 * decides what a task should do is out of scope here.
 */
@Entity
@Table(name = "agent_task")
public class AgentTask {

    @Id
    private String id;

    @Column(name = "agent_id", nullable = false, length = 36)
    private String agentId;

    @Column(name = "goal_id", length = 36)
    private String goalId;

    @Column(nullable = false, length = 512)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AgentTaskStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, Object> input = Map.of();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, Object> output = Map.of();

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID().toString();
        if (status == null) status = AgentTaskStatus.PENDING;
        createdAt = updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public String getId() { return id; }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public String getGoalId() { return goalId; }
    public void setGoalId(String goalId) { this.goalId = goalId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AgentTaskStatus getStatus() { return status; }
    public void setStatus(AgentTaskStatus status) { this.status = status; }

    public Map<String, Object> getInput() { return input; }
    public void setInput(Map<String, Object> input) { this.input = input != null ? input : Map.of(); }

    public Map<String, Object> getOutput() { return output; }
    public void setOutput(Map<String, Object> output) { this.output = output != null ? output : Map.of(); }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
