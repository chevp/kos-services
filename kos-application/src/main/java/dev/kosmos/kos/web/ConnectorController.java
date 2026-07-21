package dev.kosmos.kos.web;

import dev.kosmos.kos.connector.ConnectorService;
import dev.kosmos.kos.domain.ConnectorConfig;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/connectors")
public class ConnectorController {

    private final ConnectorService service;

    public ConnectorController(ConnectorService service) {
        this.service = service;
    }

    @GetMapping
    public List<ConnectorConfig> list(@RequestParam(required = false) String type) {
        return service.list(type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConnectorConfig create(@RequestBody ConnectorRequest req) {
        return service.create(req.type(), req.name(), req.namespace(), req.config());
    }

    @GetMapping("/{id}")
    public ConnectorConfig get(@PathVariable String id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public ConnectorConfig update(@PathVariable String id, @RequestBody ConnectorRequest req) {
        return service.update(id, req.name(), req.config(), req.enabled());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PostMapping("/{id}/read")
    public Map<String, Object> read(@PathVariable String id, @RequestBody(required = false) Map<String, Object> query) {
        return service.read(id, query);
    }

    @PostMapping("/{id}/write")
    public void write(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        service.write(id, payload);
    }

    @PostMapping("/{id}/execute")
    public Map<String, Object> execute(@PathVariable String id, @RequestBody ConnectorExecuteRequest req) {
        return service.execute(id, req.action(), req.params());
    }
}
