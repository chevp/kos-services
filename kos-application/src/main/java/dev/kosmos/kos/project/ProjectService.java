package dev.kosmos.kos.project;

import dev.kosmos.kos.domain.Project;
import dev.kosmos.kos.domain.ProjectRepository;
import dev.kosmos.kos.domain.ProjectStatus;
import dev.kosmos.kos.domain.ProjectSummary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    public Project create(String name, String namespace, String serviceEndpoint,
                          String bundleRef, Map<String, String> settings) {
        if (repo.existsByNamespaceAndName(namespace, name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Project '" + namespace + "/" + name + "' already exists");
        }
        var p = new Project();
        p.setName(name);
        p.setNamespace(namespace);
        p.setServiceEndpoint(serviceEndpoint);
        p.setBundleRef(bundleRef);
        p.setSettings(settings);
        return repo.save(p);
    }

    @Transactional(readOnly = true)
    public Project get(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ProjectSummary> list() {
        return repo.findAllByOrderByUpdatedAtDesc()
                .stream().map(Project::toSummary).toList();
    }

    public Project update(String id, String name, String namespace,
                          String serviceEndpoint, String bundleRef,
                          Map<String, String> settings) {
        var p = get(id);
        if (name != null) p.setName(name);
        if (namespace != null) p.setNamespace(namespace);
        if (serviceEndpoint != null) p.setServiceEndpoint(serviceEndpoint);
        if (bundleRef != null) p.setBundleRef(bundleRef);
        if (settings != null) p.setSettings(settings);
        return repo.save(p);
    }

    public Project activate(String id) {
        var p = get(id);
        if (p.getStatus() == ProjectStatus.ARCHIVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Archived projects cannot be activated");
        }
        p.setStatus(ProjectStatus.ACTIVE);
        return repo.save(p);
    }

    public Project archive(String id) {
        var p = get(id);
        p.setStatus(ProjectStatus.ARCHIVED);
        return repo.save(p);
    }

    public void delete(String id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repo.deleteById(id);
    }
}
