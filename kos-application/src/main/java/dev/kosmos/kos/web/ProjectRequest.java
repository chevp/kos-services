package dev.kosmos.kos.web;

import java.util.Map;

public record ProjectRequest(
        String name,
        String namespace,
        String serviceEndpoint,
        String bundleRef,
        Map<String, String> settings
) {}
