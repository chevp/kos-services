package dev.kosmos.kos.domain;

/**
 * One directed edge of a WorkflowGraph, connecting two WorkflowNode ids.
 * {@code condition} is an optional free-form expression string evaluated
 * by whatever drives execution (Agent Runtime, Phase 6) — the graph model
 * itself does not interpret it.
 */
public record WorkflowEdge(
        String id,
        String fromNodeId,
        String toNodeId,
        String condition
) {}
