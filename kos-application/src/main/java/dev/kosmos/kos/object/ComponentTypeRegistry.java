package dev.kosmos.kos.object;

import dev.kosmos.kos.domain.ObjectComponent;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Catalog of known Component types attachable to a StudioObject (Phase 4,
 * docs/ideas/studio-platform-migrationsplan.md). Vorbild: iris' Component
 * variant (frostgfx dto::Component) — the shape (a bag of typed components
 * per object) is the same idea, but these component types are business,
 * not game, concepts.
 */
@Component
public class ComponentTypeRegistry {

    private final Map<String, ComponentTypeDefinition> types = List.of(
            new ComponentTypeDefinition("metadata", "Metadata", List.of()),
            new ComponentTypeDefinition("permissions", "Permissions", List.of("roles")),
            new ComponentTypeDefinition("version", "Version", List.of("version")),
            new ComponentTypeDefinition("tags", "Tags", List.of("values")),
            new ComponentTypeDefinition("state", "State", List.of("value")),
            new ComponentTypeDefinition("workflow", "Workflow", List.of("workflowId")),
            new ComponentTypeDefinition("memory", "Memory", List.of()),
            new ComponentTypeDefinition("knowledge", "Knowledge", List.of()),
            new ComponentTypeDefinition("connector", "Connector", List.of("connectorType"))
    ).stream().collect(java.util.stream.Collectors.toMap(ComponentTypeDefinition::key, t -> t));

    public List<ComponentTypeDefinition> listAll() {
        return types.values().stream()
                .sorted((a, b) -> a.key().compareTo(b.key()))
                .toList();
    }

    public ComponentTypeDefinition require(String key) {
        var def = types.get(key);
        if (def == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown component type: " + key);
        }
        return def;
    }

    public void validate(ObjectComponent component) {
        var def = require(component.type());
        var data = component.data() != null ? component.data() : Map.of();
        for (var required : def.requiredDataKeys()) {
            if (!data.containsKey(required)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Component type '" + component.type() + "' requires data key '" + required + "'");
            }
        }
    }
}
