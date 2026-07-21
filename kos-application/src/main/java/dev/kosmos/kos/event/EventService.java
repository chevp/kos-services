package dev.kosmos.kos.event;

import dev.kosmos.kos.domain.StudioEvent;
import dev.kosmos.kos.domain.StudioEventRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EventService {

    private final StudioEventRepository repo;
    private final ApplicationEventPublisher publisher;

    public EventService(StudioEventRepository repo, ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.publisher = publisher;
    }

    public StudioEvent publish(String type, String objectId, Map<String, Object> payload) {
        var event = new StudioEvent();
        event.setType(type);
        event.setObjectId(objectId);
        event.setPayload(payload);
        event = repo.save(event);
        publisher.publishEvent(new StudioEventPublished(event));
        return event;
    }

    @Transactional(readOnly = true)
    public List<StudioEvent> list(String objectId) {
        return objectId != null
                ? repo.findAllByObjectIdOrderByOccurredAtDesc(objectId)
                : repo.findAllByOrderByOccurredAtDesc();
    }
}
