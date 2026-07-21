package dev.kosmos.kos.domain;

import java.time.OffsetDateTime;

public record StudioObjectSummary(
        String id,
        String type,
        String namespace,
        String name,
        OffsetDateTime updatedAt
) {}
