package dev.kosmos.kos.web;

import java.util.Map;

public record AgentMemoryRequest(
        Map<String, Object> value
) {}
