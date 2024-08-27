package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.db.Events;
import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.WorldItem;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
public class EventHandler {

    private final Repos repos;
    private final EventHandlerFunctions eventHandlerFunctions;

    public EventHandler(Repos repos, EventHandlerFunctions eventHandlerFunctions) {
        this.repos = repos;
        this.eventHandlerFunctions = eventHandlerFunctions;
    }

    public Mono<Event> pushEvent(Event event) {
        Events events = repos.events();
        R2dbcEntityTemplate entityTemplate = repos.entityTemplate();

        Mono<Long> nextEventId = events.nextId().cache();

        Mono<Long> queueTail = events.getPointerId(Event.EventPointers.QUEUE_TAIL).cache();
        Mono<Long> current = events.getPointerId(Event.EventPointers.CURRENT).cache();

        Mono<Long> newPrevious = queueTail
                .filter(tail -> tail != -1L)
                .switchIfEmpty(current)
                .cache();


        Mono<Event> eventWithId = nextEventId
                .map(event::withId)
                .map(evt -> evt.withNextId(-1L))
                .zipWith(newPrevious, Event::withPreviousId)
                .flatMap(entityTemplate::insert)
                .cache();


        Mono<Void> updateTailEvent = queueTail
                .filter(tail -> tail != -1L)
                .flatMap(events::findById)
                .zipWith(nextEventId, Event::withNextId)
                .flatMap(entityTemplate::update)
                .then();


        Mono<Void> updateCurrentEvent = Mono.zip(queueTail, current)
                .filter(tuple -> tuple.getT1() == -1L && tuple.getT2() != -1)
                .map(Tuple2::getT2)
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
                .then(Mono.when(updateCurrentEvent, updateTailEvent, updateTailPointer, updateHeadPointer))
                .then(eventWithId);
    }

    public Mono<? extends WorldItem> popEventQueue() {
        Events events = repos.events();

        Mono<Long> queueHead = events.getPointerId(Event.EventPointers.QUEUE_HEAD).cache();
        Mono<Long> current = events.getPointerId(Event.EventPointers.CURRENT).cache();

        Mono<Event> headEvent = queueHead
                .filter(head -> head != -1L)
                .flatMap(events::findById)
                .map(evt -> evt.withPosition(Event.EventPosition.CURRENT))
                .flatMap(repos.entityTemplate()::update)
                .cache();

        Mono<Void> updateCurrentPosition = current
                .filter(id -> id != -1L)
                .flatMap(events::findById)
                .map(evt -> evt.withPosition(Event.EventPosition.COMPLETED))
                .flatMap(repos.entityTemplate()::update)
                .then();

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



        return Mono.when(updateHeadPointer, updateTailPointer, updateCurrentPointer, updateCurrentPosition)
                .then(headEvent)
                .flatMap(evt -> eventHandlerFunctions.getHandlerFunction(evt, Event.EventDirection.FORWARD).apply(evt));
    }

    public Mono<? extends WorldItem> reverseEvent() {
        Events events = repos.events();

        Mono<Long> current = events.getPointerId(Event.EventPointers.CURRENT).cache();
        Mono<Long> queueTail = events.getPointerId(Event.EventPointers.QUEUE_TAIL).cache();

        Mono<Event> currentEvent = current
                .filter(id -> id != -1L)
                .flatMap(events::findById)
                .cache();

        Mono<Void> updateCurrentPointer = currentEvent
                .map(Event::previousId)
                .flatMap(id -> events.setPointerId(Event.EventPointers.CURRENT, id));

        Mono<Void> updateHeadPointer = currentEvent
                .map(Event::id)
                .flatMap(id -> events.setPointerId(Event.EventPointers.QUEUE_HEAD, id));

        Mono<Void> updateTailPointer = queueTail
                .filter(id -> id == -1L)
                .flatMap(id -> current)
                .flatMap(id -> events.setPointerId(Event.EventPointers.QUEUE_TAIL, id));

        return Mono.when(updateCurrentPointer, updateHeadPointer, updateTailPointer)
                .then(currentEvent)
                .flatMap(evt -> eventHandlerFunctions.getHandlerFunction(evt, Event.EventDirection.REVERSE).apply(evt));
    }

}
