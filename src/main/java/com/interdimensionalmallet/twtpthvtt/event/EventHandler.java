package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.db.Events;
import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
public class EventHandler {

    private final Repos repos;

    public EventHandler(Repos repos) {
        this.repos = repos;
    }

    public Mono<Event> pushEvent(Event event) {
        Events events = repos.events();
        R2dbcEntityTemplate entityTemplate = repos.entityTemplate();

        Mono<Long> nextEventId = events.nextId().cache();

        Mono<Long> queueTail = events.getPointerId(Event.EventPointers.QUEUE_TAIL).cache();


        Mono<Event> eventWithId = nextEventId
                .map(event::withId)
                .map(evt -> evt.withNextId(-1L))
                .zipWith(queueTail, Event::withPreviousId)
                .flatMap(entityTemplate::insert)
                .cache();


        Mono<Void> updateTailEvent = queueTail
                .filter(tail -> tail != -1L)
                .flatMap(events::findById)
                .zipWith(nextEventId, Event::withNextId)
                .flatMap(entityTemplate::update)
                .then();


        Mono<Void> updateTailPointer = nextEventId
                .flatMap(id -> events.setPointerId(Event.EventPointers.QUEUE_TAIL, id));

        Mono<Void> updateHeadPointer = queueTail
                .filter(tail -> tail == -1L)
                .zipWith(nextEventId)
                .map(Tuple2::getT2)
                .flatMap(id -> events.setPointerId(Event.EventPointers.QUEUE_HEAD, id));

        return eventWithId
                .then(Mono.when(updateTailEvent, updateTailPointer, updateHeadPointer))
                .then(eventWithId);
    }

    public Mono<Event> popEventQueue() {
        Events events = repos.events();

        Mono<Long> queueHead = events.getPointerId(Event.EventPointers.QUEUE_HEAD).cache();

        Mono<Event> headEvent = queueHead
                .filter(head -> head != -1L)
                .flatMap(events::findById)
                .cache();

        Mono<Void> updateHeadPointer = headEvent
                .map(Event::nextId)
                .flatMap(id -> events.setPointerId(Event.EventPointers.QUEUE_HEAD, id));

        Mono<Void> updateTailPointer = headEvent
                .map(Event::nextId)
                .filter(id -> id == -1L)
                .flatMap(id -> events.setPointerId(Event.EventPointers.QUEUE_TAIL, id));

        Mono<Void> updateCurrentPointer = headEvent
                .map(Event::id)
                .flatMap(id -> events.setPointerId(Event.EventPointers.CURRENT, id));



        return Mono.when(updateHeadPointer, updateTailPointer, updateCurrentPointer)
                .then(headEvent);
    }

}
