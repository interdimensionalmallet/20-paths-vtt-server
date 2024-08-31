package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Message;
import com.interdimensionalmallet.twtpthvtt.model.Thing;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuleThingHandler implements RuleHandler {
    private final KieSession kieSession;
    private final ConcurrentHashMap<Long, FactHandle> thingHandles = new ConcurrentHashMap<>();

    public RuleThingHandler(KieSession kieSession, Repos repos, Topics topics) {
        this.kieSession = kieSession;
        repos.things().findAll().subscribe(this::insertThing);
        topics.thingTopic().asFlux().subscribe(this::handleThing);
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

}
