package dev.kosmos.kos.agent;

import dev.kosmos.kos.domain.Goal;
import dev.kosmos.kos.domain.GoalRepository;
import dev.kosmos.kos.domain.GoalStatus;
import dev.kosmos.kos.event.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GoalService {

    private final GoalRepository repo;
    private final AgentService agents;
    private final EventService events;

    public GoalService(GoalRepository repo, AgentService agents, EventService events) {
        this.repo = repo;
        this.agents = agents;
        this.events = events;
    }

    public Goal create(String agentId, String description) {
        agents.get(agentId);
        var g = new Goal();
        g.setAgentId(agentId);
        g.setDescription(description);
        g = repo.save(g);
        events.publish("goal.created", agentId, Map.of("goalId", g.getId(), "description", description));
        return g;
    }

    @Transactional(readOnly = true)
    public Goal get(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Goal> listByAgent(String agentId) {
        return repo.findAllByAgentIdOrderByCreatedAtDesc(agentId);
    }

    public Goal setStatus(String id, GoalStatus status) {
        var g = get(id);
        g.setStatus(status);
        g = repo.save(g);
        events.publish("goal.status-changed", g.getAgentId(), Map.of("goalId", g.getId(), "status", status.name()));
        return g;
    }
}
