package dev.kosmos.kos.web;

import dev.kosmos.kos.agent.AgentTaskService;
import dev.kosmos.kos.domain.AgentTask;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent-tasks")
public class AgentTaskController {

    private final AgentTaskService service;

    public AgentTaskController(AgentTaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<AgentTask> list(@RequestParam String agentId) {
        return service.listByAgent(agentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgentTask create(@RequestBody AgentTaskRequest req) {
        return service.create(req.agentId(), req.goalId(), req.description(), req.input());
    }

    @GetMapping("/{id}")
    public AgentTask get(@PathVariable String id) {
        return service.get(id);
    }

    @PostMapping("/{id}/start")
    public AgentTask start(@PathVariable String id) {
        return service.start(id);
    }

    @PostMapping("/{id}/complete")
    public AgentTask complete(@PathVariable String id, @RequestBody AgentTaskOutputRequest req) {
        return service.complete(id, req.output());
    }

    @PostMapping("/{id}/fail")
    public AgentTask fail(@PathVariable String id, @RequestBody(required = false) AgentTaskFailRequest req) {
        return service.fail(id, req != null ? req.reason() : null);
    }
}
