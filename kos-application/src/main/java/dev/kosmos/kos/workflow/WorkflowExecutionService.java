package dev.kosmos.kos.workflow;

import dev.kosmos.kos.domain.WorkflowExecution;
import dev.kosmos.kos.domain.WorkflowExecutionRepository;
import dev.kosmos.kos.domain.WorkflowExecutionStatus;
import dev.kosmos.kos.event.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * State tracking for WorkflowGraph runs (Phase 5). Does not interpret node
 * types or edge conditions itself — advance()/complete()/fail() are called
 * by whatever drives execution (Agent/Workflow Runtime, Phase 6).
 */
@Service
@Transactional
public class WorkflowExecutionService {

    private final WorkflowExecutionRepository repo;
    private final WorkflowGraphService graphs;
    private final EventService events;

    public WorkflowExecutionService(WorkflowExecutionRepository repo, WorkflowGraphService graphs, EventService events) {
        this.repo = repo;
        this.graphs = graphs;
        this.events = events;
    }

    public WorkflowExecution start(String graphId, Map<String, Object> context) {
        var graph = graphs.get(graphId);
        if (graph.getNodes().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graph '" + graphId + "' has no nodes");
        }
        var startNode = graph.getNodes().stream()
                .filter(n -> "start".equals(n.type()))
                .findFirst()
                .orElse(graph.getNodes().get(0));

        var exec = new WorkflowExecution();
        exec.setGraphId(graphId);
        exec.setCurrentNodeId(startNode.id());
        exec.setContext(context);
        exec = repo.save(exec);
        events.publish("workflow-execution.started", exec.getId(),
                Map.of("graphId", graphId, "nodeId", startNode.id()));
        return exec;
    }

    @Transactional(readOnly = true)
    public WorkflowExecution get(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<WorkflowExecution> listByGraph(String graphId) {
        return repo.findAllByGraphIdOrderByStartedAtDesc(graphId);
    }

    public WorkflowExecution advance(String id, String toNodeId) {
        var exec = requireRunning(id);
        var graph = graphs.get(exec.getGraphId());
        var currentNodeId = exec.getCurrentNodeId();
        var validEdge = graph.getEdges().stream()
                .anyMatch(e -> e.fromNodeId().equals(currentNodeId) && e.toNodeId().equals(toNodeId));
        if (!validEdge) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No edge from '" + exec.getCurrentNodeId() + "' to '" + toNodeId + "' in graph '" + graph.getId() + "'");
        }
        exec.setCurrentNodeId(toNodeId);
        exec = repo.save(exec);
        events.publish("workflow-execution.advanced", exec.getId(), Map.of("nodeId", toNodeId));
        return exec;
    }

    public WorkflowExecution complete(String id) {
        var exec = requireRunning(id);
        exec.setStatus(WorkflowExecutionStatus.COMPLETED);
        exec.setFinishedAt(OffsetDateTime.now());
        exec = repo.save(exec);
        events.publish("workflow-execution.completed", exec.getId(), Map.of());
        return exec;
    }

    public WorkflowExecution fail(String id, String reason) {
        var exec = requireRunning(id);
        exec.setStatus(WorkflowExecutionStatus.FAILED);
        exec.setFinishedAt(OffsetDateTime.now());
        exec = repo.save(exec);
        events.publish("workflow-execution.failed", exec.getId(), Map.of("reason", reason != null ? reason : ""));
        return exec;
    }

    private WorkflowExecution requireRunning(String id) {
        var exec = get(id);
        if (exec.getStatus() != WorkflowExecutionStatus.RUNNING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Execution '" + id + "' is not running");
        }
        return exec;
    }
}
