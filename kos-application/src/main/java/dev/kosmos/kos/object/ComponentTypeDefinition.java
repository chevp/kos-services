package dev.kosmos.kos.object;

import java.util.List;

/**
 * Catalog entry for a Component type (Phase 4 of the migration plan).
 * {@code requiredDataKeys} are keys that must be present in an
 * ObjectComponent's {@code data} for a component of this type.
 */
public record ComponentTypeDefinition(
        String key,
        String label,
        List<String> requiredDataKeys
) {}
