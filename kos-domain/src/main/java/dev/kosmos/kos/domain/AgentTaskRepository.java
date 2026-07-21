package dev.kosmos.kos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgentTaskRepository extends JpaRepository<AgentTask, String> {

    List<AgentTask> findAllByAgentIdOrderByCreatedAtDesc(String agentId);
}
