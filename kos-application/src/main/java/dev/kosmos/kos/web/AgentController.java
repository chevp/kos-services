package dev.kosmos.kos.web;

import dev.kosmos.kos.agent.AgentService;
import dev.kosmos.kos.domain.Agent;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentService service;

    public AgentController(AgentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Agent> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agent create(@RequestBody AgentRequest req) {
        return service.create(req.name(), req.namespace(), req.tools());
    }

    @GetMapping("/{id}")
    public Agent get(@PathVariable String id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Agent update(@PathVariable String id, @RequestBody AgentRequest req) {
        return service.update(id, req.name(), req.tools());
    }

    @PostMapping("/{id}/status")
    public Agent setStatus(@PathVariable String id, @RequestBody AgentStatusRequest req) {
        return service.setStatus(id, req.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
