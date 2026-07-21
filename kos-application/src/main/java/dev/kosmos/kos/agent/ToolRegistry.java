package dev.kosmos.kos.agent;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Catalog of Tools an Agent may be granted (Phase 6,
 * docs/ideas/studio-platform-migrationsplan.md). Fixed in-memory list
 * scoped to what kos-services itself already exposes (StudioObjects,
 * Events, WorkflowGraphs) — external Connectors (SAP, Slack, ...) are
 * Phase 7 and will extend this catalog once built, not replace it.
 */
@Component
public class ToolRegistry {

    private final Map<String, ToolDefinition> tools = List.of(
            new ToolDefinition("read-object", "Read Object", "Read a StudioObject by id"),
            new ToolDefinition("write-object", "Write Object", "Create or update a StudioObject"),
            new ToolDefinition("publish-event", "Publish Event", "Publish a StudioEvent"),
            new ToolDefinition("run-workflow", "Run Workflow", "Start a WorkflowExecution for a WorkflowGraph")
    ).stream().collect(java.util.stream.Collectors.toMap(ToolDefinition::key, t -> t));

    public List<ToolDefinition> listAll() {
        return tools.values().stream()
                .sorted((a, b) -> a.key().compareTo(b.key()))
                .toList();
    }

    public void validate(List<String> toolKeys) {
        if (toolKeys == null) return;
        for (var key : toolKeys) {
            if (!tools.containsKey(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown tool: " + key);
            }
        }
    }
}
