package dev.kosmos.kos.web;

import java.util.Map;

public record ConnectorRequest(
        String type,
        String name,
        String namespace,
        Map<String, Object> config,
        Boolean enabled
) {}
