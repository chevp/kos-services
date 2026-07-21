package dev.kosmos.kos.event;

import dev.kosmos.kos.domain.StudioEvent;
import org.springframework.context.ApplicationEvent;

/**
 * In-process pub/sub signal — Vorbild: iris' IExtensionHost.subscribeToEvent.
 * Any bean can {@code @EventListener} on this to react to a StudioEvent
 * without polling the persisted log.
 */
public class StudioEventPublished extends ApplicationEvent {

    public StudioEventPublished(StudioEvent event) {
        super(event);
    }

    public StudioEvent getEvent() {
        return (StudioEvent) getSource();
    }
}
