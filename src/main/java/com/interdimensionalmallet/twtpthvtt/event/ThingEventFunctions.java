package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.Thing;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.EnumMap;
import java.util.Map;

@Component
public class ThingEventFunctions implements EventHandlerFunctionSupplier<Thing> {

    private final Repos repos;
    private final Topics topics;

    public ThingEventFunctions(Repos repos, Topics topics) {
        this.repos = repos;
        this.topics = topics;
    }

    public Mono<Thing> forwardCreateHandle(Event event) {
        Thing newThing = new Thing(event.thingId(), event.thingName(), false);
        return Mono.just(newThing).transform(Topics.create(topics.thingTopic()));
    }

    public Mono<Thing> reverseCreateHandle(Event event) {
        return repos.things().findById(event.thingId())
                .transform(Topics.delete(topics.thingTopic()));
    }

    public Mono<Thing> forwardRemoveHandle(Event event) {
        return repos.things().findById(event.thingId())
                .map(thing -> thing.withDeleted(true))
                .transform(Topics.update(topics.thingTopic()));
    }

    public Mono<Thing> reverseRemoveHandle(Event event) {
        return repos.things().findById(event.thingId())
                .map(thing -> thing.withDeleted(false))
                .transform(Topics.update(topics.thingTopic()));
    }

    @Override
    public EventHandlerFunction<Thing> getHandlerFunction(Event.EventType eventType, Event.EventDirection eventDirection) {
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
