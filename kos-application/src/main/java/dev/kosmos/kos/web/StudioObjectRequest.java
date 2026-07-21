package dev.kosmos.kos.web;

import dev.kosmos.kos.domain.ObjectComponent;

import java.util.List;
import java.util.Map;

public record StudioObjectRequest(
        String type,
        String namespace,
        String name,
        Map<String, Object> attributes,
        List<ObjectComponent> components
) {}
