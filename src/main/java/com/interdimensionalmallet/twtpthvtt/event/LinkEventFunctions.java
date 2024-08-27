package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.Link;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class LinkEventFunctions implements EventHandlerFunctionSupplier<Link> {

    private final Repos repos;
    private final Topics topics;

    public LinkEventFunctions(Repos repos, Topics topics) {
        this.repos = repos;
        this.topics = topics;
    }

    public Mono<Link> addLink(Event event) {
        Link newLink = new Link(event.thingId(), event.targetThingId());
        Link other = newLink.reverse();
        return Flux.just(newLink, other).transform(Topics.createMany(topics.linkTopic())).then(Mono.just(newLink));
    }

    public Mono<Link> removeLink(Event event) {
        Mono<Link> existingLink = repos.links().findByKey(event.thingId(), event.targetThingId()).cache();
        Mono<Link> reverseLink = existingLink.map(Link::reverse);
        return Flux.just(existingLink, reverseLink).flatMap(o -> o).transform(Topics.deleteMany(topics.linkTopic())).then(existingLink);
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
