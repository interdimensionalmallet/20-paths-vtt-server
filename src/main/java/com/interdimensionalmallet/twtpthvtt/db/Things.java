package com.interdimensionalmallet.twtpthvtt.db;

import com.interdimensionalmallet.twtpthvtt.model.Thing;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface Things extends ReactiveCrudRepository<Thing, Long> {

    @Query("SELECT NEXT VALUE FOR THING_ID_SEQ")
    Mono<Long> nextId();
}