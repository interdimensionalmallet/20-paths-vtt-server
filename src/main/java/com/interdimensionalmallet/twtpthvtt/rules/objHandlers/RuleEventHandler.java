package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.Message;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuleEventHandler implements RuleHandler {

    private final KieSession kieSession;
    private final ConcurrentHashMap<Long, FactHandle> eventHandles = new ConcurrentHashMap<>();

    public RuleEventHandler(KieSession kieSession, Repos repos, Topics topics) {
        this.kieSession = kieSession;
        repos.events().findAll().subscribe(this::insertEvent);
        topics.eventTopic().asFlux().subscribe(this::handleEvent);
    }

    private void handleEvent(Message<Event> eventMessage) {
        Event event = eventMessage.payload();
        System.out.println("Inserting event: " + event);
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
}
