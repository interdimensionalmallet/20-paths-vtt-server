package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ResourceEventFunctions implements EventHandlerFunctionSupplier<Resource> {

    private final Repos repos;

    public ResourceEventFunctions(Repos repos) {
        this.repos = repos;
    }

    public Mono<Resource> forwardCreateHandle(Event event) {
        Resource newResource = new Resource(event.resourceId(), event.thingId(), event.resourceName(), event.resourceModifier(), false);
        return repos.entityTemplate().insert(newResource);
    }

    public Mono<Resource> reverseCreateHandle(Event event) {
        return repos.resources().findById(event.resourceId())
                .flatMap(resource -> repos.entityTemplate().delete(resource)
                        .thenReturn(resource));
    }

    public Mono<Resource> forwardRemoveHandle(Event event) {
        return repos.resources().findById(event.resourceId())
                .flatMap(resource -> repos.entityTemplate().update(resource.withDeleted(true)));
    }

    public Mono<Resource> reverseRemoveHandle(Event event) {
        return repos.resources().findById(event.resourceId())
                .flatMap(resource -> repos.entityTemplate().update(resource.withDeleted(false)));
    }

    @Override
    public EventHandlerFunction<Resource> getHandlerFunction(Event.EventType eventType, Event.EventDirection eventDirection) {
        return switch(eventType){
            case CREATE -> switch(eventDirection){
                case FORWARD -> this::forwardCreateHandle;
                case REVERSE -> this::reverseCreateHandle;
            };
            case REMOVE -> switch(eventDirection){
                case FORWARD -> this::forwardRemoveHandle;
                case REVERSE -> this::reverseRemoveHandle;
            };
        };
    }
}
