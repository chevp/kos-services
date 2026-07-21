package dev.kosmos.kos.agent;

import dev.kosmos.kos.domain.Agent;
import dev.kosmos.kos.domain.AgentRepository;
import dev.kosmos.kos.domain.AgentStatus;
import dev.kosmos.kos.event.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AgentService {

    private final AgentRepository repo;
    private final ToolRegistry tools;
    private final EventService events;

    public AgentService(AgentRepository repo, ToolRegistry tools, EventService events) {
        this.repo = repo;
        this.tools = tools;
        this.events = events;
    }

    public Agent create(String name, String namespace, List<String> toolKeys) {
        tools.validate(toolKeys);
        var a = new Agent();
        a.setName(name);
        a.setNamespace(namespace);
        a.setTools(toolKeys);
        a = repo.save(a);
        events.publish("agent.created", a.getId(), Map.of("name", name));
        return a;
    }

    @Transactional(readOnly = true)
    public Agent get(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Agent> list() {
        return repo.findAllByOrderByUpdatedAtDesc();
    }

    public Agent update(String id, String name, List<String> toolKeys) {
        var a = get(id);
        if (name != null) a.setName(name);
        if (toolKeys != null) {
            tools.validate(toolKeys);
            a.setTools(toolKeys);
        }
        a = repo.save(a);
        events.publish("agent.updated", a.getId(), Map.of("name", a.getName()));
        return a;
    }

    public Agent setStatus(String id, AgentStatus status) {
        var a = get(id);
        a.setStatus(status);
        a = repo.save(a);
        events.publish("agent.status-changed", a.getId(), Map.of("status", status.name()));
        return a;
    }

    public void delete(String id) {
        var a = get(id);
        repo.deleteById(id);
        events.publish("agent.deleted", a.getId(), Map.of("name", a.getName()));
    }
}
