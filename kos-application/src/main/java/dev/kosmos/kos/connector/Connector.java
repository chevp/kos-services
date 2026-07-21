package dev.kosmos.kos.connector;

import dev.kosmos.kos.domain.ConnectorConfig;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Uniform interface every external system integration implements (Phase 7,
 * docs/ideas/studio-platform-migrationsplan.md). iris is one implementation
 * among Git/Slack/SAP/Jira/Teams/REST/Filesystem/Database/Email — it is
 * addressed over RPC/WebSocket like any other external process, never
 * linked against runtime/iris C++ code (see plan's leading principle).
 */
public interface Connector {

    Map<String, Object> read(ConnectorConfig config, Map<String, Object> query);

    void write(ConnectorConfig config, Map<String, Object> payload);

    Map<String, Object> execute(ConnectorConfig config, String action, Map<String, Object> params);

    void subscribe(ConnectorConfig config, Consumer<Map<String, Object>> handler);
}
