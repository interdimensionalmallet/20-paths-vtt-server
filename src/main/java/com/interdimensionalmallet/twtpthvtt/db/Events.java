package com.interdimensionalmallet.twtpthvtt.db;

import com.interdimensionalmallet.twtpthvtt.model.Event;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public interface Events extends R2dbcRepository<Event, Long> {

    @Query("SELECT NEXT VALUE FOR EVENT_ID_SEQ")
    Mono<Long> nextId();

    @Query("SELECT EVENT_ID FROM EVENT_POINTERS WHERE POINTER_NAME = :pointerName")
    Mono<Long> getPointerId(Event.EventPointers pointerName);

    @Query("UPDATE EVENT_POINTERS SET EVENT_ID = :eventId WHERE POINTER_NAME = :pointerName")
    Mono<Void> setPointerId(Event.EventPointers pointerName, Long eventId);

    Flux<Event> findByDeleted(Boolean deleted);

}
