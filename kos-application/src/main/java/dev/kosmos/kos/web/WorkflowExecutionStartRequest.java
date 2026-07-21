package dev.kosmos.kos.web;

import java.util.Map;

public record WorkflowExecutionStartRequest(
        String graphId,
        Map<String, Object> context
) {}
