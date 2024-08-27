package com.interdimensionalmallet.twtpthvtt.rules;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.*;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuleEngine {

    private KieSession kieSession;
    private final ConcurrentHashMap<Long, FactHandle> eventHandles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<LinkId, FactHandle> linkHandles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, FactHandle> resourceHandles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, FactHandle> thingHandles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, FactHandle> queryHandles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, FactHandle> queryOptionHandles = new ConcurrentHashMap<>();



    public RuleEngine(KieSession kieSession, Repos repos, Topics topics) {
        this.kieSession = kieSession;
        repos.events().findAll().subscribe(this::insertEvent);
        repos.links().findAll().subscribe(this::insertLink);
        repos.resources().findAll().subscribe(this::insertResource);
        repos.things().findAll().subscribe(this::insertThing);
        repos.queries().findAll().subscribe(this::insertQuery);
        repos.queryOptions().findAll().subscribe(this::insertQueryOption);
        topics.eventTopic().asFlux().subscribe(this::handleEvent);
        topics.linkTopic().asFlux().subscribe(this::handleLink);
        topics.resourceTopic().asFlux().subscribe(this::handleResource);
        topics.thingTopic().asFlux().subscribe(this::handleThing);
        topics.queryTopic().asFlux().subscribe(this::handleQuery);
        topics.queryOptionTopic().asFlux().subscribe(this::handleQueryOption);
        kieSession.fireAllRules();
    }

    public void stepRules() {
        kieSession.fireAllRules();
    }

    private void handleEvent(Message<Event> eventMessage) {
        Event event = eventMessage.payload();
        switch (eventMessage.type()) {
            case CREATE -> insertEvent(event);
            case DELETE -> deleteEvent(event);
            case UPDATE -> updateEvent(event);
        }
    }

    private void insertEvent(Event event) {
        FactHandle handle = kieSession.insert(event);
        eventHandles.put(event.id(), handle);
    }

    private void updateEvent(Event event) {
        FactHandle handle = eventHandles.get(event.id());
        kieSession.update(handle, event, "eventPosition");
    }

    private void deleteEvent(Event event) {
        FactHandle handle = eventHandles.get(event.id());
        kieSession.delete(handle);
        eventHandles.remove(event.id());
    }

    private void handleLink(Message<Link> linkMessage) {
        Link link = linkMessage.payload();
        switch (linkMessage.type()) {
            case CREATE -> insertLink(link);
            case DELETE, UPDATE -> removeLink(link);
        }
    }

    private void insertLink(Link link) {
        FactHandle handle = kieSession.insert(link);
        linkHandles.put(new LinkId(link.sourceThingId(), link.targetThingId()), handle);
    }

    private void removeLink(Link link) {
        FactHandle handle = linkHandles.get(new LinkId(link.sourceThingId(), link.targetThingId()));
        kieSession.delete(handle);
        linkHandles.remove(new LinkId(link.sourceThingId(), link.targetThingId()));
    }

    private void handleResource(Message<Resource> resourceMessage) {
        Resource resource = resourceMessage.payload();
        switch (resourceMessage.type()) {
            case CREATE -> insertResource(resource);
            case DELETE -> removeResource(resource);
            case UPDATE -> updateResource(resource);
        }
    }

    private void insertResource(Resource resource) {
        FactHandle handle = kieSession.insert(resource);
        resourceHandles.put(resource.id(), handle);
    }

    private void updateResource(Resource resource) {
        FactHandle handle = resourceHandles.get(resource.id());
        if (resource.deleted()) {
            if (handle != null) {
                removeResource(resource);
            }
        } else {
            if (handle == null) {
                insertResource(resource);
            } else {
                kieSession.update(handle, resource, "count");
            }
        }
    }

    private void removeResource(Resource resource) {
        FactHandle handle = resourceHandles.get(resource.id());
        kieSession.delete(handle);
        resourceHandles.remove(resource.id());
    }

    private void handleThing(Message<Thing> thingMessage) {
        Thing thing = thingMessage.payload();
        switch (thingMessage.type()) {
            case CREATE -> insertThing(thing);
            case DELETE -> removeThing(thing);
            case UPDATE -> updateThing(thing);
        }
    }

    private void insertThing(Thing thing) {
        FactHandle handle = kieSession.insert(thing);
        thingHandles.put(thing.id(), handle);
    }

    private void updateThing(Thing thing) {
        FactHandle handle = thingHandles.get(thing.id());
        if (thing.deleted()) {
            if (handle != null) {
                removeThing(thing);
            }
        } else {
            if (handle == null) {
                insertThing(thing);
            } else {
                kieSession.update(handle, thing, "name");
            }
        }
    }

    private void removeThing(Thing thing) {
        FactHandle handle = thingHandles.get(thing.id());
        kieSession.delete(handle);
        thingHandles.remove(thing.id());
    }

    private void handleQuery(Message<Query> queryMessage) {
        Query query = queryMessage.payload();
        switch (queryMessage.type()) {
            case CREATE -> insertQuery(query);
            case DELETE -> removeQuery(query);
            case UPDATE -> updateQuery(query);
        }
    }

    private void insertQuery(Query query) {
        FactHandle handle = kieSession.insert(query);
        queryHandles.put(query.id(), handle);
    }

    private void updateQuery(Query query) {
        FactHandle handle = queryHandles.get(query.id());
        kieSession.update(handle, query, "selectedOptionId");
    }

    private void removeQuery(Query query) {
        FactHandle handle = queryHandles.get(query.id());
        kieSession.delete(handle);
        queryHandles.remove(query.id());
    }

    private void handleQueryOption(Message<QueryOption> queryOptionMessage) {
        QueryOption queryOption = queryOptionMessage.payload();
        switch (queryOptionMessage.type()) {
            case CREATE -> insertQueryOption(queryOption);
            case DELETE -> removeQueryOption(queryOption);
            case UPDATE -> updateQueryOption(queryOption);
        }
    }

    private void insertQueryOption(QueryOption queryOption) {
        FactHandle handle = kieSession.insert(queryOption);
        queryOptionHandles.put(queryOption.id(), handle);
    }

    private void updateQueryOption(QueryOption queryOption) {
        FactHandle handle = queryOptionHandles.get(queryOption.id());
        kieSession.update(handle, queryOption);
    }

    private void removeQueryOption(QueryOption queryOption) {
        FactHandle handle = queryOptionHandles.get(queryOption.id());
        kieSession.delete(handle);
        queryOptionHandles.remove(queryOption.id());
    }


}
