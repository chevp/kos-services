package dev.kosmos.kos.web;

import dev.kosmos.kos.domain.Project;
import dev.kosmos.kos.domain.ProjectSummary;
import dev.kosmos.kos.project.ProjectHealthClient;
import dev.kosmos.kos.project.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService service;
    private final ProjectHealthClient healthClient;

    public ProjectController(ProjectService service, ProjectHealthClient healthClient) {
        this.service = service;
        this.healthClient = healthClient;
    }

    @GetMapping
    public List<ProjectSummary> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project create(@RequestBody ProjectRequest req) {
        return service.create(req.name(), req.namespace(), req.serviceEndpoint(),
                req.bundleRef(), req.settings());
    }

    @GetMapping("/{id}")
    public Project get(@PathVariable String id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Project update(@PathVariable String id, @RequestBody ProjectRequest req) {
        return service.update(id, req.name(), req.namespace(), req.serviceEndpoint(),
                req.bundleRef(), req.settings());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PostMapping("/{id}/activate")
    public Project activate(@PathVariable String id) {
        return service.activate(id);
    }

    @PostMapping("/{id}/archive")
    public Project archive(@PathVariable String id) {
        return service.archive(id);
    }

    @GetMapping("/{id}/health")
    public Map<String, Object> health(@PathVariable String id) {
        var project = service.get(id);
        return healthClient.probe(project.getServiceEndpoint());
    }
}
