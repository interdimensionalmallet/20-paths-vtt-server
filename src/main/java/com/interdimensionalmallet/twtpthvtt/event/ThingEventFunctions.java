package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.Thing;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.EnumMap;
import java.util.Map;

@Component
public class ThingEventFunctions implements EventHandlerFunctionSupplier<Thing> {

    private final Repos repos;

    public ThingEventFunctions(Repos repos) {
        this.repos = repos;
    }

    public Mono<Thing> forwardCreateHandle(Event event) {
        Thing newThing = new Thing(event.thingId(), event.thingName(), false);
        return repos.entityTemplate().insert(newThing);
    }

    public Mono<Thing> reverseCreateHandle(Event event) {
        return repos.things().findById(event.thingId())
                .flatMap(thing -> repos.entityTemplate().delete(thing)
                        .thenReturn(thing));
    }

    public Mono<Thing> forwardRemoveHandle(Event event) {
        return repos.things().findById(event.thingId())
                .flatMap(thing -> repos.entityTemplate().update(thing.withDeleted(true)));
    }

    public Mono<Thing> reverseRemoveHandle(Event event) {
        return repos.things().findById(event.thingId())
                .flatMap(thing -> repos.entityTemplate().update(thing.withDeleted(false)));
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
