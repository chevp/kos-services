package dev.kosmos.kos.web;

import java.util.Map;

public record ConnectorExecuteRequest(
        String action,
        Map<String, Object> params
) {}
