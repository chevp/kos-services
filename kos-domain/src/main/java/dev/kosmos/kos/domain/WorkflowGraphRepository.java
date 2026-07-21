package dev.kosmos.kos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowGraphRepository extends JpaRepository<WorkflowGraph, String> {

    List<WorkflowGraph> findAllByOrderByUpdatedAtDesc();
}
