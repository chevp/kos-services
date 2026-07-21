package dev.kosmos.kos.object;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Catalog of known Business-Object types (Phase 3,
 * docs/ideas/studio-platform-migrationsplan.md). Fixed in-memory list for
 * now; a pluggable registry (types contributed by Packages) is Phase 9.
 */
@Component
public class ObjectTypeRegistry {

    private final Map<String, ObjectTypeDefinition> types = List.of(
            new ObjectTypeDefinition("pdf", "PDF", List.of("uri")),
            new ObjectTypeDefinition("excel", "Excel", List.of("uri")),
            new ObjectTypeDefinition("prompt", "Prompt", List.of("template")),
            new ObjectTypeDefinition("workflow", "Workflow", List.of()),
            new ObjectTypeDefinition("knowledge-base", "Knowledge Base", List.of()),
            new ObjectTypeDefinition("contract", "Contract", List.of("uri")),
            new ObjectTypeDefinition("customer", "Customer", List.of()),
            new ObjectTypeDefinition("invoice", "Invoice", List.of("amount", "currency")),
            new ObjectTypeDefinition("sql-query", "SQL Query", List.of("sql")),
            new ObjectTypeDefinition("rest-endpoint", "REST Endpoint", List.of("url", "method"))
    ).stream().collect(java.util.stream.Collectors.toMap(ObjectTypeDefinition::key, t -> t));

    public List<ObjectTypeDefinition> listAll() {
        return types.values().stream()
                .sorted((a, b) -> a.key().compareTo(b.key()))
                .toList();
    }

    public ObjectTypeDefinition require(String key) {
        var def = types.get(key);
        if (def == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown object type: " + key);
        }
        return def;
    }

    public void validate(String key, Map<String, Object> attributes) {
        var def = require(key);
        var attrs = attributes != null ? attributes : Map.of();
        for (var required : def.requiredAttributes()) {
            if (!attrs.containsKey(required)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Object type '" + key + "' requires attribute '" + required + "'");
            }
        }
    }
}
