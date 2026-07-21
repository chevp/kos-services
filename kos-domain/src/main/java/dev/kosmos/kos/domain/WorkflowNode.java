package dev.kosmos.kos.domain;

import java.util.Map;

/**
 * One node of a WorkflowGraph. {@code type} is a free string ("start",
 * "task", "decision", "end", ...); {@code config} holds node-specific
 * settings (e.g. which agent/connector a "task" node invokes).
 */
public record WorkflowNode(
        String id,
        String type,
        String name,
        Map<String, Object> config
) {}
