package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.model.*;

import com.interdimensionalmallet.twtpthvtt.repo.Repos;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
public class IgniteEvents implements Events {

    private final Repos repos;
    private final Map<Event.EventType, Map<Event.EventStyle, BiConsumer<Event, Repos>>> eventHandlers;

    public IgniteEvents(Repos repos) {
        this.repos = repos;
        eventHandlers = new EnumMap<>(Event.EventType.class);
        Map<Event.EventStyle, BiConsumer<Event, Repos>> thingHandlers = new EnumMap<>(Event.EventStyle.class);
        thingHandlers.put(Event.EventStyle.CREATE, buildThingCreateHandler());
        thingHandlers.put(Event.EventStyle.DELETE, buildThingDeleteHandler());
        eventHandlers.put(Event.EventType.THING, thingHandlers);
        Map<Event.EventStyle, BiConsumer<Event, Repos>> resourceHandlers = new EnumMap<>(Event.EventStyle.class);
        resourceHandlers.put(Event.EventStyle.CREATE, buildResourceCreateHandler());
        resourceHandlers.put(Event.EventStyle.DELETE, buildResourceDeleteHandler());
        eventHandlers.put(Event.EventType.RESOURCE, resourceHandlers);
        Map<Event.EventStyle, BiConsumer<Event, Repos>> linkHandlers = new EnumMap<>(Event.EventStyle.class);
        linkHandlers.put(Event.EventStyle.CREATE, buildLinkCreateHandler());
        linkHandlers.put(Event.EventStyle.DELETE, buildLinkDeleteHandler());
        eventHandlers.put(Event.EventType.LINK, linkHandlers);
    }

    public static BiConsumer<Event, Repos> buildLinkCreateHandler() {
        return (event, repos) -> {
            Link leftLink = new Link(repos.linkIDSequence().getAndIncrement(), event.sourceThingId(), event.targetThingId());
            repos.links().put(leftLink.id(), leftLink);
            Link rightLink = new Link(repos.linkIDSequence().getAndIncrement(), event.targetThingId(), event.sourceThingId());
            repos.links().put(rightLink.id(), rightLink);
        };
    }

    public static BiConsumer<Event, Repos> buildLinkDeleteHandler() {
        return (event, repos) -> {
            repos.links().query(new SqlFieldsQuery("DELETE FROM Link WHERE (sourceThingId = ? AND targetThingId = ?) OR (sourceThingId = ? AND targetThingId = ?)")
                    .setArgs(event.sourceThingId(), event.targetThingId(), event.targetThingId(), event.sourceThingId()))
                    .getAll();
        };
    }

    public static BiConsumer<Event, Repos> buildResourceCreateHandler() {
        return (event, repos) -> {
            Long existingResourceId = repos.resources().query(new SqlFieldsQuery("SELECT id FROM Resource WHERE thingId = ? AND resourceName = ?")
                    .setArgs(event.thingId(), event.resourceName()))
                    .getAll().stream()
                    .findFirst()
                    .map(row -> (Long) row.get(0))
                    .orElse(null);
            if (existingResourceId == null) {
                Resource newResource = new Resource(repos.resourceIDSequence().getAndIncrement(), event.thingId(), event.resourceName(), event.resourceModifier());
                repos.resources().put(newResource.id(), newResource);
            } else {
                Resource existingResource = repos.resources().get(existingResourceId);
                Resource updatedResource = new Resource(existingResourceId, event.thingId(), event.resourceName(), existingResource.count() + event.resourceModifier());
                repos.resources().replace(existingResourceId, updatedResource);
            }
        };
    }

    public static BiConsumer<Event, Repos> buildResourceDeleteHandler() {
        return (event, repos) -> {
            Long existingResourceId = repos.resources().query(new SqlFieldsQuery("SELECT id FROM Resource WHERE thingId = ? AND resourceName = ?")
                    .setArgs(event.thingId(), event.resourceName()))
                    .getAll().stream()
                    .findFirst()
                    .map(row -> (Long) row.get(0))
                    .orElse(null);
            if (existingResourceId != null) {
                Resource existingResource = repos.resources().get(existingResourceId);
                int newCount = existingResource.count() - event.resourceModifier();
                if (newCount <= 0) {
                    repos.resources().remove(existingResourceId);
                } else {
                    Resource updatedResource = new Resource(existingResourceId, event.thingId(), event.resourceName(), newCount);
                    repos.resources().replace(existingResourceId, updatedResource);
                }
            }
        };
    }

    public static BiConsumer<Event, Repos> buildThingCreateHandler() {
        return (event, repos) -> {
            Thing newThing = new Thing(event.thingId(), event.thingName());
            repos.things().put(newThing.id(), newThing);
        };
    }

    public static BiConsumer<Event, Repos> buildThingDeleteHandler() {
        return (event, repos) -> {
            repos.things().remove(event.thingId());
        };
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

    private void processEvent(final Event event) {
        eventHandlers.get(event.eventType()).get(event.eventStyle()).accept(event, repos);
    }
}
