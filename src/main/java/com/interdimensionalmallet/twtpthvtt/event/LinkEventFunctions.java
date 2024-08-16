package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.Link;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LinkEventFunctions implements EventHandlerFunctionSupplier<Link> {

    private final Repos repos;

    public LinkEventFunctions(Repos repos) {
        this.repos = repos;
    }

    public Mono<Link> addLink(Event event) {
        Link newLink = new Link(event.thingId(), event.targetThingId());
        Link other = newLink.reverse();
        return Mono.when(repos.entityTemplate().insert(newLink), repos.entityTemplate().insert(other))
                .thenReturn(newLink);
    }

    private Mono<Void> deleteByKey(Mono<Link> existingLink) {
        return existingLink.flatMap(link -> repos.links().deleteByKey(link.sourceThingId(), link.targetThingId())).then();
    }

    public Mono<Link> removeLink(Event event) {
        Mono<Link> existingLink = repos.links().findByKey(event.thingId(), event.targetThingId()).cache();
        Mono<Link> reverseLink = existingLink.map(Link::reverse);

        return Mono.when(deleteByKey(existingLink), deleteByKey(reverseLink))
                .then(existingLink);
    }

    public EventHandlerFunction<Link> getHandlerFunction(Event.EventType eventType, Event.EventDirection eventDirection) {
        return switch(eventType){
            case CREATE -> switch(eventDirection){
                case FORWARD -> this::addLink;
                case REVERSE -> this::removeLink;
            };
            case REMOVE -> switch(eventDirection){
                case FORWARD -> this::removeLink;
                case REVERSE -> this::addLink;
            };
        };
    }

}
