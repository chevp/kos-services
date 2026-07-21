package dev.kosmos.kos.web;

import dev.kosmos.kos.domain.StudioEvent;
import dev.kosmos.kos.event.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class StudioEventController {

    private final EventService service;

    public StudioEventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<StudioEvent> list(@RequestParam(required = false) String objectId) {
        return service.list(objectId);
    }
}
