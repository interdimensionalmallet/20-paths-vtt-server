package com.interdimensionalmallet.twtpthvtt.db;

import com.interdimensionalmallet.twtpthvtt.model.Event;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public interface Events extends R2dbcRepository<Event, Long> {

    @Query("SELECT NEXT VALUE FOR EVENT_ID_SEQ")
    Mono<Long> nextId();

}
