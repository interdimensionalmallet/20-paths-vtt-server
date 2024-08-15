package com.interdimensionalmallet.twtpthvtt.controllers;

import com.interdimensionalmallet.twtpthvtt.db.Links;
import com.interdimensionalmallet.twtpthvtt.db.Things;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.Link;
import com.interdimensionalmallet.twtpthvtt.model.Thing;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class DebugController {

    private final DatabaseClient databaseClient;
    private final Things things;
    private final Links links;

    public DebugController(DatabaseClient databaseClient, Things things, Links links) {
        this.databaseClient = databaseClient;
        this.things = things;
        this.links = links;
    }

    @GetMapping("/thingNames")
    public Flux<Thing> getEvents() {
        return databaseClient.sql("SELECT * FROM THING")
                .map(row -> new Thing(row.get("ID", Long.class), row.get("NAME", String.class)))
                .all();
    }

    @PostMapping("/createThing")
    public Mono<Long> createThing(@RequestBody Thing thing) {
        return things.nextId().map(thing::withId).flatMap(things::save).map(Thing::id);
    }


    @PostMapping("/createLink")
    public Flux<Link> createLink(@RequestBody Link link) {
        return Flux.just(link, link.reverse())
                .flatMap(newLink -> links.nextId().map(newLink::withId))
                .flatMap(links::save);
    }



}
