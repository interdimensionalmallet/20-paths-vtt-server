package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.Link;
import com.interdimensionalmallet.twtpthvtt.model.LinkId;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LinkEventFunctions implements EventHandlerFunctionSupplier<Link> {

    private final Repos repos;

    public LinkEventFunctions(Repos repos) {
        this.repos = repos;
    }

    public Mono<Link> forwardCreateHandle(Event event) {
        Link newLink = new Link(new LinkId(event.thingId(), event.targetThingId()), false);
        Link other = newLink.reverse();
        return Mono.when(repos.entityTemplate().insert(newLink), repos.entityTemplate().insert(other))
                .thenReturn(newLink);
    }

    public Mono<Link> reverseCreateHandle(Event event) {
        return repos.links().findById(new LinkId(event.thingId(), event.targetThingId()))
                .flatMap(link -> Mono.when(repos.entityTemplate().delete(link), repos.entityTemplate().delete(link.reverse()))
                        .thenReturn(link));
    }

    public Mono<Link> forwardRemoveHandle(Event event) {
        return repos.links().findById(new LinkId(event.thingId(), event.targetThingId()))
                .flatMap(link ->
                        Mono.when(
                                repos.entityTemplate().update(link.withDeleted(true)),
                                repos.entityTemplate().update(link.reverse().withDeleted(true))
                        ).thenReturn(link)
                );
    }

    public Mono<Link> reverseRemoveHandle(Event event) {
        return repos.links().findById(new LinkId(event.thingId(), event.targetThingId()))
                .flatMap(link ->
                        Mono.when(
                                repos.entityTemplate().update(link.withDeleted(false)),
                                repos.entityTemplate().update(link.reverse().withDeleted(false))
                        ).thenReturn(link)
                );
    }

    public EventHandlerFunction<Link> getHandlerFunction(Event.EventType eventType, Event.EventDirection eventDirection) {
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
