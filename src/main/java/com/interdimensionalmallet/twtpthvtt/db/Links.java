package com.interdimensionalmallet.twtpthvtt.db;

import com.interdimensionalmallet.twtpthvtt.model.Link;
import com.interdimensionalmallet.twtpthvtt.model.LinkId;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface Links extends ReactiveCrudRepository<Link, LinkId> {
}
