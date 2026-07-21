package dev.kosmos.kos.web;

import dev.kosmos.kos.object.ComponentTypeDefinition;
import dev.kosmos.kos.object.ComponentTypeRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/component-types")
public class ComponentTypeController {

    private final ComponentTypeRegistry registry;

    public ComponentTypeController(ComponentTypeRegistry registry) {
        this.registry = registry;
    }

    @GetMapping
    public List<ComponentTypeDefinition> list() {
        return registry.listAll();
    }
}
