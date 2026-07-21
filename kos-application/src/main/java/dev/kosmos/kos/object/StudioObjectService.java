package dev.kosmos.kos.object;

import dev.kosmos.kos.domain.ObjectComponent;
import dev.kosmos.kos.domain.StudioObject;
import dev.kosmos.kos.domain.StudioObjectRepository;
import dev.kosmos.kos.domain.StudioObjectSummary;
import dev.kosmos.kos.event.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StudioObjectService {

    private final StudioObjectRepository repo;
    private final EventService events;
    private final ObjectTypeRegistry types;
    private final ComponentTypeRegistry componentTypes;

    public StudioObjectService(StudioObjectRepository repo, EventService events,
                                ObjectTypeRegistry types, ComponentTypeRegistry componentTypes) {
        this.repo = repo;
        this.events = events;
        this.types = types;
        this.componentTypes = componentTypes;
    }

    public StudioObject create(String type, String namespace, String name,
                                Map<String, Object> attributes, List<ObjectComponent> components) {
        types.validate(type, attributes);
        validateComponents(components);
        var o = new StudioObject();
        o.setType(type);
        o.setNamespace(namespace);
        o.setName(name);
        o.setAttributes(attributes);
        o.setComponents(components);
        o = repo.save(o);
        events.publish("object.created", o.getId(), Map.of("type", type, "name", name));
        return o;
    }

    @Transactional(readOnly = true)
    public StudioObject get(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<StudioObjectSummary> list(String type) {
        var all = type != null ? repo.findAllByTypeOrderByUpdatedAtDesc(type) : repo.findAllByOrderByUpdatedAtDesc();
        return all.stream().map(StudioObject::toSummary).toList();
    }

    public StudioObject update(String id, String name, Map<String, Object> attributes,
                                List<ObjectComponent> components) {
        var o = get(id);
        if (name != null) o.setName(name);
        if (attributes != null) {
            types.validate(o.getType(), attributes);
            o.setAttributes(attributes);
        }
        if (components != null) {
            validateComponents(components);
            o.setComponents(components);
        }
        o = repo.save(o);
        events.publish("object.updated", o.getId(), Map.of("name", o.getName()));
        return o;
    }

    public void delete(String id) {
        var o = get(id);
        repo.deleteById(id);
        events.publish("object.deleted", o.getId(), Map.of("type", o.getType()));
    }

    private void validateComponents(List<ObjectComponent> components) {
        if (components == null) return;
        components.forEach(componentTypes::validate);
    }
}
