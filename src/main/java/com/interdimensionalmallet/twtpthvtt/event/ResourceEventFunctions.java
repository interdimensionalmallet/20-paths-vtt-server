package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.Resource;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ResourceEventFunctions implements EventHandlerFunctionSupplier<Resource> {

    private final Repos repos;
    private final Topics topics;

    public ResourceEventFunctions(Repos repos, Topics topics) {
        this.repos = repos;
        this.topics = topics;
    }

    public Mono<Resource> forwardCreateHandle(Event event) {
        return repos.resources()
                .findById(event.resourceId())
                .map(rsc -> rsc.withModifier(event.resourceModifier()))
                .transform(Topics.update(topics.resourceTopic()))
                .switchIfEmpty(Mono.defer(() ->
                            Mono.just(new Resource(event.resourceId(), event.thingId(), event.resourceName(), event.resourceModifier(), false))
                                    .transform(Topics.create(topics.resourceTopic()))
                        )
                );
    }

    public Mono<Resource> reverseCreateHandle(Event event) {
        Mono<Resource> resource = repos.resources().findById(event.resourceId()).cache();
        return resource
                .map(rsc -> rsc.withModifier(-1 * event.resourceModifier()))
                .filter(rsc -> rsc.count() > 0)
                .transform(Topics.update(topics.resourceTopic()))
                .switchIfEmpty(Mono.defer(() ->
                            resource.transform(Topics.delete(topics.resourceTopic()))
                        )
                );
    }

    public Mono<Resource> forwardRemoveHandle(Event event) {
        return repos.resources().findById(event.resourceId())
                .map(resource -> resource.withDeleted(true))
                .transform(Topics.update(topics.resourceTopic()));
    }

    public Mono<Resource> reverseRemoveHandle(Event event) {
        return repos.resources().findById(event.resourceId())
                .map(resource -> resource.withDeleted(false))
                .transform(Topics.update(topics.resourceTopic()));
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
