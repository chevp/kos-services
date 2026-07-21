package dev.kosmos.kos.workflow;

import dev.kosmos.kos.domain.WorkflowEdge;
import dev.kosmos.kos.domain.WorkflowGraph;
import dev.kosmos.kos.domain.WorkflowGraphRepository;
import dev.kosmos.kos.domain.WorkflowNode;
import dev.kosmos.kos.event.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkflowGraphService {

    private final WorkflowGraphRepository repo;
    private final EventService events;

    public WorkflowGraphService(WorkflowGraphRepository repo, EventService events) {
        this.repo = repo;
        this.events = events;
    }

    public WorkflowGraph create(String name, String namespace, List<WorkflowNode> nodes, List<WorkflowEdge> edges) {
        validateEdges(nodes, edges);
        var g = new WorkflowGraph();
        g.setName(name);
        g.setNamespace(namespace);
        g.setNodes(nodes);
        g.setEdges(edges);
        g = repo.save(g);
        events.publish("workflow-graph.created", g.getId(), Map.of("name", g.getName()));
        return g;
    }

    @Transactional(readOnly = true)
    public WorkflowGraph get(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<WorkflowGraph> list() {
        return repo.findAllByOrderByUpdatedAtDesc();
    }

    public WorkflowGraph update(String id, String name, List<WorkflowNode> nodes, List<WorkflowEdge> edges) {
        var g = get(id);
        if (name != null) g.setName(name);
        if (nodes != null || edges != null) {
            var newNodes = nodes != null ? nodes : g.getNodes();
            var newEdges = edges != null ? edges : g.getEdges();
            validateEdges(newNodes, newEdges);
            g.setNodes(newNodes);
            g.setEdges(newEdges);
        }
        g = repo.save(g);
        events.publish("workflow-graph.updated", g.getId(), Map.of("name", g.getName()));
        return g;
    }

    public void delete(String id) {
        var g = get(id);
        repo.deleteById(id);
        events.publish("workflow-graph.deleted", g.getId(), Map.of("name", g.getName()));
    }

    private void validateEdges(List<WorkflowNode> nodes, List<WorkflowEdge> edges) {
        Set<String> nodeIds = nodes.stream().map(WorkflowNode::id).collect(Collectors.toSet());
        for (var edge : edges) {
            if (!nodeIds.contains(edge.fromNodeId()) || !nodeIds.contains(edge.toNodeId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Edge '" + edge.id() + "' references a node id not present in the graph");
            }
        }
    }
}
