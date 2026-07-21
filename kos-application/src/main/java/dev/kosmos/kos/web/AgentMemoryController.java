package dev.kosmos.kos.web;

import dev.kosmos.kos.agent.AgentMemoryService;
import dev.kosmos.kos.domain.AgentMemoryEntry;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents/{agentId}/memory")
public class AgentMemoryController {

    private final AgentMemoryService service;

    public AgentMemoryController(AgentMemoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<AgentMemoryEntry> list(@PathVariable String agentId) {
        return service.list(agentId);
    }

    @GetMapping("/{key}")
    public AgentMemoryEntry get(@PathVariable String agentId, @PathVariable String key) {
        return service.get(agentId, key);
    }

    @PutMapping("/{key}")
    public AgentMemoryEntry put(@PathVariable String agentId, @PathVariable String key, @RequestBody AgentMemoryRequest req) {
        return service.put(agentId, key, req.value());
    }

    @DeleteMapping("/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String agentId, @PathVariable String key) {
        service.delete(agentId, key);
    }
}
