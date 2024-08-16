package com.interdimensionalmallet.twtpthvtt.controllers;

import com.interdimensionalmallet.twtpthvtt.db.Links;
import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.db.Things;
import com.interdimensionalmallet.twtpthvtt.event.EventHandler;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.Link;
import com.interdimensionalmallet.twtpthvtt.model.Resource;
import com.interdimensionalmallet.twtpthvtt.model.Thing;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class DebugController {

    private final Repos repos;
    private final EventHandler eventHandler;

    public DebugController(Repos repos, EventHandler eventHandler) {
        this.repos = repos;
        this.eventHandler = eventHandler;
    }

    @GetMapping("/debug/things")
    public Flux<Thing> getThings() {
        return repos.things().findAll();
    }

    @GetMapping("/debug/links")
    public Flux<Link> getLinks() {
        return repos.links().findAll();
    }

    @GetMapping("/debug/resources")
    public Flux<Resource> getResources() {
        return repos.resources().findAll();
    }

    @GetMapping("/debug/events")
    public Flux<Event> getEvents() {
        return repos.events().findAll();
    }

    @GetMapping("/debug/eventPointers")
    public Mono<Map<String, String>> getEventPointers() {
        return Mono.zip(
                repos.events().getPointerId(Event.EventPointers.QUEUE_HEAD),
                repos.events().getPointerId(Event.EventPointers.QUEUE_TAIL),
                repos.events().getPointerId(Event.EventPointers.CURRENT)
        ).map(tuple -> Map.of(
                "QUEUE_HEAD", tuple.getT1().toString(),
                "QUEUE_TAIL", tuple.getT2().toString(),
                "CURRENT", tuple.getT3().toString()
        ));
    }

    @PostMapping("/debug/events")
    public Mono<Event> createEvent(@RequestBody Event event) {
        return eventHandler.pushEvent(event);
    }

    @DeleteMapping("/debug/events")
    public Mono<Event> popEvent() {
        return eventHandler.popEventQueue();
    }

}
