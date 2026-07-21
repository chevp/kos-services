package dev.kosmos.kos.web;

import dev.kosmos.kos.connector.ConnectorRegistry;
import dev.kosmos.kos.connector.ConnectorTypeDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/connector-types")
public class ConnectorTypeController {

    private final ConnectorRegistry registry;

    public ConnectorTypeController(ConnectorRegistry registry) {
        this.registry = registry;
    }

    @GetMapping
    public List<ConnectorTypeDefinition> list() {
        return registry.listTypes();
    }
}
