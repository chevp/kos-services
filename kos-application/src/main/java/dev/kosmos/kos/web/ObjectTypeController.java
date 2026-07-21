package dev.kosmos.kos.web;

import dev.kosmos.kos.object.ObjectTypeDefinition;
import dev.kosmos.kos.object.ObjectTypeRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/object-types")
public class ObjectTypeController {

    private final ObjectTypeRegistry registry;

    public ObjectTypeController(ObjectTypeRegistry registry) {
        this.registry = registry;
    }

    @GetMapping
    public List<ObjectTypeDefinition> list() {
        return registry.listAll();
    }
}
