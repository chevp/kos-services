package dev.kosmos.kos.web;

import dev.kosmos.kos.agent.GoalService;
import dev.kosmos.kos.domain.Goal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService service;

    public GoalController(GoalService service) {
        this.service = service;
    }

    @GetMapping
    public List<Goal> list(@RequestParam String agentId) {
        return service.listByAgent(agentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Goal create(@RequestBody GoalRequest req) {
        return service.create(req.agentId(), req.description());
    }

    @GetMapping("/{id}")
    public Goal get(@PathVariable String id) {
        return service.get(id);
    }

    @PostMapping("/{id}/status")
    public Goal setStatus(@PathVariable String id, @RequestBody GoalStatusRequest req) {
        return service.setStatus(id, req.status());
    }
}
