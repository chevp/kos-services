package dev.kosmos.kos.web;

import dev.kosmos.kos.domain.WorkflowExecution;
import dev.kosmos.kos.workflow.WorkflowExecutionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow-executions")
public class WorkflowExecutionController {

    private final WorkflowExecutionService service;

    public WorkflowExecutionController(WorkflowExecutionService service) {
        this.service = service;
    }

    @GetMapping
    public List<WorkflowExecution> list(@RequestParam String graphId) {
        return service.listByGraph(graphId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkflowExecution start(@RequestBody WorkflowExecutionStartRequest req) {
        return service.start(req.graphId(), req.context());
    }

    @GetMapping("/{id}")
    public WorkflowExecution get(@PathVariable String id) {
        return service.get(id);
    }

    @PostMapping("/{id}/advance")
    public WorkflowExecution advance(@PathVariable String id, @RequestBody WorkflowExecutionAdvanceRequest req) {
        return service.advance(id, req.toNodeId());
    }

    @PostMapping("/{id}/complete")
    public WorkflowExecution complete(@PathVariable String id) {
        return service.complete(id);
    }

    @PostMapping("/{id}/fail")
    public WorkflowExecution fail(@PathVariable String id, @RequestBody(required = false) WorkflowExecutionFailRequest req) {
        return service.fail(id, req != null ? req.reason() : null);
    }
}
