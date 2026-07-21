package dev.kosmos.kos.connector;

import dev.kosmos.kos.domain.ConnectorConfig;
import dev.kosmos.kos.domain.ConnectorConfigRepository;
import dev.kosmos.kos.event.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ConnectorService {

    private final ConnectorConfigRepository repo;
    private final ConnectorRegistry registry;
    private final EventService events;

    public ConnectorService(ConnectorConfigRepository repo, ConnectorRegistry registry, EventService events) {
        this.repo = repo;
        this.registry = registry;
        this.events = events;
    }

    public ConnectorConfig create(String type, String name, String namespace, Map<String, Object> config) {
        registry.resolve(type);
        var c = new ConnectorConfig();
        c.setType(type);
        c.setName(name);
        c.setNamespace(namespace);
        c.setConfig(config);
        c = repo.save(c);
        events.publish("connector.created", c.getId(), Map.of("type", type, "name", name));
        return c;
    }

    @Transactional(readOnly = true)
    public ConnectorConfig get(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ConnectorConfig> list(String type) {
        return type != null ? repo.findAllByTypeOrderByUpdatedAtDesc(type) : repo.findAllByOrderByUpdatedAtDesc();
    }

    public ConnectorConfig update(String id, String name, Map<String, Object> config, Boolean enabled) {
        var c = get(id);
        if (name != null) c.setName(name);
        if (config != null) c.setConfig(config);
        if (enabled != null) c.setEnabled(enabled);
        c = repo.save(c);
        events.publish("connector.updated", c.getId(), Map.of("name", c.getName()));
        return c;
    }

    public void delete(String id) {
        var c = get(id);
        repo.deleteById(id);
        events.publish("connector.deleted", c.getId(), Map.of("type", c.getType()));
    }

    public Map<String, Object> read(String id, Map<String, Object> query) {
        var c = requireEnabled(id);
        return registry.resolve(c.getType()).read(c, query);
    }

    public void write(String id, Map<String, Object> payload) {
        var c = requireEnabled(id);
        registry.resolve(c.getType()).write(c, payload);
        events.publish("connector.write", c.getId(), Map.of("type", c.getType()));
    }

    public Map<String, Object> execute(String id, String action, Map<String, Object> params) {
        var c = requireEnabled(id);
        var result = registry.resolve(c.getType()).execute(c, action, params);
        events.publish("connector.execute", c.getId(), Map.of("type", c.getType(), "action", action));
        return result;
    }

    private ConnectorConfig requireEnabled(String id) {
        var c = get(id);
        if (!c.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Connector '" + id + "' is disabled");
        }
        return c;
    }
}
