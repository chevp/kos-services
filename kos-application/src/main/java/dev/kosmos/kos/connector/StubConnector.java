package dev.kosmos.kos.connector;

import dev.kosmos.kos.domain.ConnectorConfig;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Placeholder for Connector types named in the migration plan's Phase 7
 * that have no real implementation yet (git, slack, sap, jira, teams,
 * filesystem, database, email, iris). Fails loudly rather than silently
 * no-op'ing, so callers don't mistake "not wired up" for "did nothing".
 * The iris-Connector belongs here too: it must speak RPC/WebSocket to a
 * running irisdaemon (see plan's leading principle — no linking against
 * runtime/iris), which is real integration work not yet done.
 */
@Component
public class StubConnector implements Connector {

    @Override
    public Map<String, Object> read(ConnectorConfig config, Map<String, Object> query) {
        throw notImplemented(config);
    }

    @Override
    public void write(ConnectorConfig config, Map<String, Object> payload) {
        throw notImplemented(config);
    }

    @Override
    public Map<String, Object> execute(ConnectorConfig config, String action, Map<String, Object> params) {
        throw notImplemented(config);
    }

    @Override
    public void subscribe(ConnectorConfig config, Consumer<Map<String, Object>> handler) {
        throw notImplemented(config);
    }

    private ResponseStatusException notImplemented(ConnectorConfig config) {
        return new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,
                "Connector type '" + config.getType() + "' is not wired up yet (Phase 7 TODO)");
    }
}
