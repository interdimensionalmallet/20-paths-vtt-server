package com.interdimensionalmallet.twtpthvtt.db;

import com.interdimensionalmallet.twtpthvtt.model.Resource;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface Resources extends ReactiveCrudRepository<Resource, Long> {

    @Query("SELECT NEXT VALUE FOR RESOURCE_ID_SEQ")
    Mono<Long> nextId();

}
