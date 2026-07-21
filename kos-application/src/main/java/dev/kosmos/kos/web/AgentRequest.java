package dev.kosmos.kos.web;

import java.util.List;

public record AgentRequest(
        String name,
        String namespace,
        List<String> tools
) {}
