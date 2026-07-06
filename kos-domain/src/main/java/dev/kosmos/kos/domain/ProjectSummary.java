package dev.kosmos.kos.domain;

import java.time.OffsetDateTime;

public record ProjectSummary(
        String id,
        String name,
        String namespace,
        ProjectStatus status,
        OffsetDateTime updatedAt
) {}
