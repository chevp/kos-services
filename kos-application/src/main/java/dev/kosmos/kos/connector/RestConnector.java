package dev.kosmos.kos.connector;

import dev.kosmos.kos.domain.ConnectorConfig;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Generic REST Connector — the one connector type with a real, working
 * implementation so far. Config requires {@code baseUrl}; requests append
 * an optional {@code path} param/key and send the remaining params/payload
 * as the JSON body. Also usable as a fallback for any external system that
 * exposes a plain REST API (SAP/Jira/Teams/Slack often do) until they get
 * a dedicated Connector implementation.
 */
@Component
public class RestConnector implements Connector {

    @Override
    public Map<String, Object> read(ConnectorConfig config, Map<String, Object> query) {
        return request(config, HttpMethod.GET, query);
    }

    @Override
    public void write(ConnectorConfig config, Map<String, Object> payload) {
        request(config, HttpMethod.POST, payload);
    }

    @Override
    public Map<String, Object> execute(ConnectorConfig config, String action, Map<String, Object> params) {
        var method = HttpMethod.valueOf(action.toUpperCase());
        return request(config, method, params);
    }

    @Override
    public void subscribe(ConnectorConfig config, Consumer<Map<String, Object>> handler) {
        throw new UnsupportedOperationException(
                "RestConnector does not support subscribe() — REST has no inherent push channel; wire up webhooks separately");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> request(ConnectorConfig config, HttpMethod method, Map<String, Object> params) {
        var baseUrl = (String) config.getConfig().get("baseUrl");
        if (baseUrl == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Connector '" + config.getId() + "' is missing required config key 'baseUrl'");
        }
        var body = params != null ? new HashMap<>(params) : new HashMap<String, Object>();
        var path = (String) body.remove("path");
        var uri = path != null ? baseUrl + path : baseUrl;

        var client = RestClient.create();
        var spec = client.method(method).uri(uri);
        if (method != HttpMethod.GET && method != HttpMethod.DELETE) {
            spec.body(body);
        }
        var response = spec.retrieve().body(Map.class);
        return response != null ? (Map<String, Object>) response : Map.of();
    }
}
