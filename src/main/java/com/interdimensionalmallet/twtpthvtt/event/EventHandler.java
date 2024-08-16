package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.db.Events;
import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.function.Function;

@Component
public class EventHandler {

    private final Repos repos;

    public EventHandler(Repos repos) {
        this.repos = repos;
    }

    <T> Mono<T> inTransaction(Function<Mono<Void>, Mono<T>> transformer) {
        return Mono.from(repos.databaseClient().getConnectionFactory().create())
                .flatMap(connection -> Mono.from(connection.beginTransaction())
                        .transform(transformer)
                        .transform(result -> Mono.from(connection.commitTransaction()).then(result))
                        .onErrorResume(e -> Mono.from(connection.rollbackTransaction()).then(Mono.error(e))));
    }

    Mono<Event> pushEvent(Mono<Void> transaction, Event event) {
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

        Mono<Event> result = eventWithId
                .then(Mono.when(updateTailEvent, updateTailPointer, updateHeadPointer))
                .then(eventWithId);



        return transaction.then(result);
    }


    public Mono<Event> pushEvent(Event event) {
        return inTransaction(transaction -> pushEvent(transaction, event));
    }

    Mono<Event> popEvent(Mono<Void> transaction) {
        Events events = repos.events();
        R2dbcEntityTemplate entityTemplate = repos.entityTemplate();

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



        return transaction.then(Mono.when(updateHeadPointer, updateTailPointer, updateCurrentPointer)
                .then(headEvent));
    }

    public Mono<Event> popEventQueue() {
        return inTransaction(this::popEvent);
    }

}
