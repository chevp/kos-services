package dev.kosmos.kos.web;

import dev.kosmos.kos.domain.WorkflowEdge;
import dev.kosmos.kos.domain.WorkflowNode;

import java.util.List;

public record WorkflowGraphRequest(
        String name,
        String namespace,
        List<WorkflowNode> nodes,
        List<WorkflowEdge> edges
) {}
