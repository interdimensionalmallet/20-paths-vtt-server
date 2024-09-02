package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Link;
import com.interdimensionalmallet.twtpthvtt.model.LinkId;
import com.interdimensionalmallet.twtpthvtt.model.Message;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuleLinkHandler {

    private final KieSession kieSession;
    private final ConcurrentHashMap<LinkId, FactHandle> factHandles = new ConcurrentHashMap<>();

    public RuleLinkHandler(KieSession kieSession, Repos repos, Topics topics) {
        this.kieSession = kieSession;
        repos.links().findAll().subscribe(this::insertLink);
        topics.linkTopic().asFlux().subscribe(this::handleLink);
    }

    private void handleLink(Message<Link> eventMessage) {
        Link event = eventMessage.payload();
        switch (eventMessage.type()) {
            case CREATE -> insertLink(event);
            case DELETE, UPDATE -> deleteEvent(event);
        }
    }

    private void insertLink(Link event) {
        FactHandle handle = kieSession.insert(event);
        factHandles.put(event.id(), handle);
    }

    private void deleteEvent(Link event) {
        LinkId id = event.id();
        FactHandle handle = factHandles.get(id);
        kieSession.delete(handle);
        factHandles.remove(id);
    }

}
