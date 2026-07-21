package dev.kosmos.kos.web;

import dev.kosmos.kos.agent.ToolDefinition;
import dev.kosmos.kos.agent.ToolRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
public class ToolController {

    private final ToolRegistry registry;

    public ToolController(ToolRegistry registry) {
        this.registry = registry;
    }

    @GetMapping
    public List<ToolDefinition> list() {
        return registry.listAll();
    }
}
