package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.Message;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

public class EventPersistenceHandler {

    private final Repos repos;

    public EventPersistenceHandler(Repos repos, Sinks.Many<Message<Event>> eventTopic) {
        this.repos = repos;
        eventTopic.asFlux().flatMap(this::persistEvent).subscribe();
    }

    private Mono<Event> persistEvent(Message<Event> event) {
        return switch (event.type()) {
            case CREATE -> repos.entityTemplate().insert(event.payload());
            case UPDATE -> repos.entityTemplate().update(event.payload());
            case DELETE -> repos.entityTemplate().delete(event.payload());
        };
    }

}
