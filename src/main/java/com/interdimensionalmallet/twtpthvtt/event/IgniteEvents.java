package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.model.*;

import com.interdimensionalmallet.twtpthvtt.repo.Repos;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.springframework.stereotype.Component;

@Component
public class IgniteEvents implements Events {

    private final Repos repos;

    public IgniteEvents(Repos repos) {
        this.repos = repos;
    }

    @Override
    public void enqueueEvent(Event event) {
        repos.futureEvents().put(event);
    }

    @Override
    public void advanceEvent() {
        if (!repos.futureEvents().isEmpty()) {
            Event event = repos.futureEvents().poll();
            Long newIndex = repos.events().query(new SqlFieldsQuery("SELECT MAX(index) FROM Event"))
                    .getAll().stream()
                    .findFirst()
                    .map(row -> row.get(0))
                    .map(index -> (Long) index)
                    .orElse(-1L) + 1;
            processEvent(event);
            repos.events().put(newIndex, event.withIndex(newIndex));
        }
    }

    private void processEvent(Event event) {
        switch (event.eventType()) {
            case LINK -> {
                EventLink link = (EventLink) event;
                switch (event.eventStyle()) {
                    case CREATE -> {
                        Link leftLink = new Link(repos.linkIDSequence().getAndIncrement(), link.sourceThingId(), link.targetThingId());
                        repos.links().put(leftLink.id(), leftLink);
                        Link rightLink = new Link(repos.linkIDSequence().getAndIncrement(), link.targetThingId(), link.sourceThingId());
                        repos.links().put(rightLink.id(), rightLink);
                    }
                    case DELETE -> {
                        repos.links().query(new SqlFieldsQuery("DELETE FROM Link WHERE (sourceThingId = ? AND targetThingId = ?) OR (sourceThingId = ? AND targetThingId = ?)")
                                .setArgs(link.sourceThingId(), link.targetThingId(), link.targetThingId(), link.sourceThingId()))
                                .getAll();
                    }
                }

            }
            case RESOURCE -> {
                EventResource resource = (EventResource) event;
                Long existingResourceId = repos.resources().query(new SqlFieldsQuery("SELECT id FROM Resource WHERE thingId = ? AND resourceName = ?")
                        .setArgs(resource.thingId(), resource.resourceName()))
                        .getAll().stream()
                        .findFirst()
                        .map(row -> (Long) row.get(0))
                        .orElse(null);
                switch (event.eventStyle()) {
                    case CREATE -> {
                        if (existingResourceId == null) {
                            Resource newResource = new Resource(repos.resourceIDSequence().getAndIncrement(), resource.thingId(), resource.resourceName(), resource.resourceModifier());
                            repos.resources().put(newResource.id(), newResource);
                        } else {
                            Resource existingResource = repos.resources().get(existingResourceId);
                            Resource updatedResource = new Resource(existingResourceId, resource.thingId(), resource.resourceName(), existingResource.count() + resource.resourceModifier());
                            repos.resources().replace(existingResourceId, existingResource);
                        }
                    }
                    case DELETE -> {
                        if (existingResourceId != null) {
                            Resource existingResource = repos.resources().get(existingResourceId);
                            int newCount = existingResource.count() - resource.resourceModifier();
                            if (newCount <= 0) {
                                repos.resources().remove(existingResourceId);
                            } else {
                                Resource updatedResource = new Resource(existingResourceId, resource.thingId(), resource.resourceName(), newCount);
                                repos.resources().replace(existingResourceId, existingResource);
                            }
                        }
                    }
                }
            }
            case THING -> {
                EventThing thing = (EventThing) event;
                switch (event.eventStyle()) {
                    case CREATE -> {
                        Thing newThing = new Thing(thing.thingId(), thing.thingName());
                        repos.things().put(newThing.id(), newThing);
                    }
                    case DELETE -> {
                        repos.things().remove(thing.thingId());
                    }
                }
            }
        }
    }
}
