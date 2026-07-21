package dev.kosmos.kos.agent;

import dev.kosmos.kos.domain.AgentTask;
import dev.kosmos.kos.domain.AgentTaskRepository;
import dev.kosmos.kos.domain.AgentTaskStatus;
import dev.kosmos.kos.event.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AgentTaskService {

    private final AgentTaskRepository repo;
    private final AgentService agents;
    private final EventService events;

    public AgentTaskService(AgentTaskRepository repo, AgentService agents, EventService events) {
        this.repo = repo;
        this.agents = agents;
        this.events = events;
    }

    public AgentTask create(String agentId, String goalId, String description, Map<String, Object> input) {
        agents.get(agentId);
        var t = new AgentTask();
        t.setAgentId(agentId);
        t.setGoalId(goalId);
        t.setDescription(description);
        t.setInput(input);
        t = repo.save(t);
        events.publish("agent-task.created", agentId, Map.of("taskId", t.getId(), "description", description));
        return t;
    }

    @Transactional(readOnly = true)
    public AgentTask get(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<AgentTask> listByAgent(String agentId) {
        return repo.findAllByAgentIdOrderByCreatedAtDesc(agentId);
    }

    public AgentTask start(String id) {
        var t = requireStatus(id, AgentTaskStatus.PENDING);
        t.setStatus(AgentTaskStatus.RUNNING);
        t = repo.save(t);
        events.publish("agent-task.started", t.getAgentId(), Map.of("taskId", t.getId()));
        return t;
    }

    public AgentTask complete(String id, Map<String, Object> output) {
        var t = requireStatus(id, AgentTaskStatus.RUNNING);
        t.setStatus(AgentTaskStatus.DONE);
        t.setOutput(output);
        t = repo.save(t);
        events.publish("agent-task.completed", t.getAgentId(), Map.of("taskId", t.getId()));
        return t;
    }

    public AgentTask fail(String id, String reason) {
        var t = requireStatus(id, AgentTaskStatus.RUNNING);
        t.setStatus(AgentTaskStatus.FAILED);
        t.setOutput(Map.of("error", reason != null ? reason : ""));
        t = repo.save(t);
        events.publish("agent-task.failed", t.getAgentId(), Map.of("taskId", t.getId(), "reason", reason != null ? reason : ""));
        return t;
    }

    private AgentTask requireStatus(String id, AgentTaskStatus expected) {
        var t = get(id);
        if (t.getStatus() != expected) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Task '" + id + "' is " + t.getStatus() + ", expected " + expected);
        }
        return t;
    }
}
