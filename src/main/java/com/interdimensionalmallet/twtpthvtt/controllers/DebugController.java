package com.interdimensionalmallet.twtpthvtt.controllers;

import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.repo.Repos;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class DebugController {


    private final Repos repos;

    public DebugController(Repos repos) {
        this.repos = repos;
    }

    @GetMapping("/events")
    public Flux<Event> getEvents() {
        return Mono.just(repos.events()).flatMapMany(eventsRepo -> Flux.fromStream(eventsRepo.query(new SqlFieldsQuery("SELECT * from Event"))
                .getAll().stream()
                .map(Event::fromRow)));
    }



}
