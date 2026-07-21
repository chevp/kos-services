package dev.kosmos.kos.agent;

import dev.kosmos.kos.domain.AgentMemoryEntry;
import dev.kosmos.kos.domain.AgentMemoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AgentMemoryService {

    private final AgentMemoryRepository repo;
    private final AgentService agents;

    public AgentMemoryService(AgentMemoryRepository repo, AgentService agents) {
        this.repo = repo;
        this.agents = agents;
    }

    public AgentMemoryEntry put(String agentId, String key, Map<String, Object> value) {
        agents.get(agentId);
        var entry = repo.findByAgentIdAndKey(agentId, key).orElseGet(() -> {
            var e = new AgentMemoryEntry();
            e.setAgentId(agentId);
            e.setKey(key);
            return e;
        });
        entry.setValue(value);
        return repo.save(entry);
    }

    @Transactional(readOnly = true)
    public AgentMemoryEntry get(String agentId, String key) {
        return repo.findByAgentIdAndKey(agentId, key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<AgentMemoryEntry> list(String agentId) {
        return repo.findAllByAgentIdOrderByKeyAsc(agentId);
    }

    public void delete(String agentId, String key) {
        repo.deleteByAgentIdAndKey(agentId, key);
    }
}
