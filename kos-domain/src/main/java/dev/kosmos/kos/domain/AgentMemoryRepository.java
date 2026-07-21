package dev.kosmos.kos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentMemoryRepository extends JpaRepository<AgentMemoryEntry, String> {

    List<AgentMemoryEntry> findAllByAgentIdOrderByKeyAsc(String agentId);

    Optional<AgentMemoryEntry> findByAgentIdAndKey(String agentId, String key);

    void deleteByAgentIdAndKey(String agentId, String key);
}
