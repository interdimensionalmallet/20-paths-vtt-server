package com.interdimensionalmallet.twtpthvtt.db;

import com.interdimensionalmallet.twtpthvtt.model.QueryOption;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface QueryOptions extends ReactiveCrudRepository<QueryOption, Long> {

    @Query("SELECT NEXT VALUE FOR QUERY_OPTION_ID_SEQ")
    Mono<Long> nextId();


    Flux<QueryOption> findByDeleted(Boolean deleted);
}
