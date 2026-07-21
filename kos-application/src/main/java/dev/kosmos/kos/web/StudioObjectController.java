package dev.kosmos.kos.web;

import dev.kosmos.kos.domain.StudioObject;
import dev.kosmos.kos.domain.StudioObjectSummary;
import dev.kosmos.kos.object.StudioObjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/objects")
public class StudioObjectController {

    private final StudioObjectService service;

    public StudioObjectController(StudioObjectService service) {
        this.service = service;
    }

    @GetMapping
    public List<StudioObjectSummary> list(@RequestParam(required = false) String type) {
        return service.list(type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudioObject create(@RequestBody StudioObjectRequest req) {
        return service.create(req.type(), req.namespace(), req.name(), req.attributes(), req.components());
    }

    @GetMapping("/{id}")
    public StudioObject get(@PathVariable String id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public StudioObject update(@PathVariable String id, @RequestBody StudioObjectRequest req) {
        return service.update(id, req.name(), req.attributes(), req.components());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
