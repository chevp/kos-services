package dev.kosmos.kos.web;

import dev.kosmos.kos.domain.WorkflowGraph;
import dev.kosmos.kos.workflow.WorkflowGraphService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow-graphs")
public class WorkflowGraphController {

    private final WorkflowGraphService service;

    public WorkflowGraphController(WorkflowGraphService service) {
        this.service = service;
    }

    @GetMapping
    public List<WorkflowGraph> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkflowGraph create(@RequestBody WorkflowGraphRequest req) {
        return service.create(req.name(), req.namespace(), req.nodes(), req.edges());
    }

    @GetMapping("/{id}")
    public WorkflowGraph get(@PathVariable String id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public WorkflowGraph update(@PathVariable String id, @RequestBody WorkflowGraphRequest req) {
        return service.update(id, req.name(), req.nodes(), req.edges());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
