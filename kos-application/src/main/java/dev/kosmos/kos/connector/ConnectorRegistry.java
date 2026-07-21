package dev.kosmos.kos.connector;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Maps a ConnectorConfig's {@code type} to its Connector implementation
 * (Phase 7). Only "rest" is real today; every other type in the plan's
 * list — git, slack, sap, jira, teams, filesystem, database, email, iris —
 * resolves to StubConnector until it gets its own implementation.
 */
@Component
public class ConnectorRegistry {

    private final Map<String, ConnectorTypeDefinition> definitions = List.of(
            new ConnectorTypeDefinition("rest", "REST", true),
            new ConnectorTypeDefinition("git", "Git", false),
            new ConnectorTypeDefinition("slack", "Slack", false),
            new ConnectorTypeDefinition("sap", "SAP", false),
            new ConnectorTypeDefinition("jira", "Jira", false),
            new ConnectorTypeDefinition("teams", "Teams", false),
            new ConnectorTypeDefinition("filesystem", "Filesystem", false),
            new ConnectorTypeDefinition("database", "Database", false),
            new ConnectorTypeDefinition("email", "Email", false),
            new ConnectorTypeDefinition("iris", "iris (RPC/WebSocket)", false)
    ).stream().collect(java.util.stream.Collectors.toMap(ConnectorTypeDefinition::key, t -> t));

    private final Connector restConnector;
    private final Connector stubConnector;

    public ConnectorRegistry(RestConnector restConnector, StubConnector stubConnector) {
        this.restConnector = restConnector;
        this.stubConnector = stubConnector;
    }

    public List<ConnectorTypeDefinition> listTypes() {
        return definitions.values().stream()
                .sorted((a, b) -> a.key().compareTo(b.key()))
                .toList();
    }

    public Connector resolve(String type) {
        var def = definitions.get(type);
        if (def == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown connector type: " + type);
        }
        return def.implemented() ? restConnector : stubConnector;
    }
}
