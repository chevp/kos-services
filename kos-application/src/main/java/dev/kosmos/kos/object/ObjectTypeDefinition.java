package dev.kosmos.kos.object;

import java.util.List;

/**
 * Catalog entry for a Business-Object type (Phase 3 of the migration plan).
 * {@code requiredAttributes} are keys that must be present in
 * StudioObject.attributes for an object of this type — a minimal schema,
 * not full JSON-Schema validation.
 */
public record ObjectTypeDefinition(
        String key,
        String label,
        List<String> requiredAttributes
) {}
