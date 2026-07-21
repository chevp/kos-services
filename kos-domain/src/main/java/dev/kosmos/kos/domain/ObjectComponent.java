package dev.kosmos.kos.domain;

import java.util.Map;

/**
 * One entry of a StudioObject's component bag — Vorbild: iris' Component
 * variant (frostgfx dto::Component). {@code type} identifies the component
 * kind (e.g. "metadata", "permissions", "workflow-state"); {@code data} holds
 * its free-form attributes. Serialized as part of StudioObject's JSONB column,
 * not a separate table.
 */
public record ObjectComponent(
        String type,
        Map<String, Object> data
) {}
