package dev.kosmos.kos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, String> {

    List<WorkflowExecution> findAllByGraphIdOrderByStartedAtDesc(String graphId);
}
