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
        return repos.resources()
                .findById(event.resourceId())
                .map(rsc -> rsc.withModifier(event.resourceModifier()))
                .flatMap(repos.entityTemplate()::update)
                .switchIfEmpty(Mono.defer(() ->
                            Mono.just(new Resource(event.resourceId(), event.thingId(), event.resourceName(), event.resourceModifier(), false))
                                    .flatMap(repos.entityTemplate()::insert)
                        )
                );
    }

    public Mono<Resource> reverseCreateHandle(Event event) {
        Mono<Resource> resource = repos.resources().findById(event.resourceId()).cache();
        return resource
                .map(rsc -> rsc.withModifier(-1 * event.resourceModifier()))
                .filter(rsc -> rsc.count() > 0)
                .flatMap(repos.entityTemplate()::update)
                .switchIfEmpty(Mono.defer(() ->
                            resource.flatMap(repos.entityTemplate()::delete)
                        )
                );
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
