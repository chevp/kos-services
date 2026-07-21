package dev.kosmos.kos.web;

import java.util.Map;

public record AgentTaskRequest(
        String agentId,
        String goalId,
        String description,
        Map<String, Object> input
) {}
