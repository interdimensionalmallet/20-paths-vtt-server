package com.interdimensionalmallet.twtpthvtt.db;

import com.interdimensionalmallet.twtpthvtt.model.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface Queries extends ReactiveCrudRepository<Query, Long> {

    @org.springframework.data.r2dbc.repository.Query("SELECT NEXT VALUE FOR QUERY_ID_SEQ")
    Mono<Long> nextId();

    Flux<Query> findByDeleted(Boolean deleted);

}
