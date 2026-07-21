package dev.kosmos.kos.web;

import java.util.Map;

public record AgentTaskOutputRequest(
        Map<String, Object> output
) {}
