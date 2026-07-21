package dev.kosmos.kos.connector;

public record ConnectorTypeDefinition(
        String key,
        String label,
        boolean implemented
) {}
