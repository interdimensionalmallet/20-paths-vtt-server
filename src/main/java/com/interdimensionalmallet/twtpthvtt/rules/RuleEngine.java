package com.interdimensionalmallet.twtpthvtt.rules;

import com.interdimensionalmallet.twtpthvtt.db.IdCaches;
import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Message;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
public class RuleEngine {

    private final KieSession kieSession;

    public RuleEngine(KieSession kieSession, Repos repos, Topics topics, IdCaches idCaches) {
        this.kieSession = kieSession;
        this.kieSession.setGlobal("idCache", idCaches);
        Sinks.Many<FactHandle> proposals = Sinks.many().multicast().onBackpressureBuffer();
        kieSession.addEventListener(new RuleRuntimeEventListener() {
            @Override
            public void objectInserted(ObjectInsertedEvent objectInsertedEvent) {
                if (objectInsertedEvent.getObject() instanceof ProposedEvent) {
                    proposals.tryEmitNext(objectInsertedEvent.getFactHandle());
                }
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent objectUpdatedEvent) {

            }

            @Override
            public void objectDeleted(ObjectDeletedEvent objectDeletedEvent) {

            }
        });
        proposals.asFlux().subscribe(kieSession::delete);
        proposals.asFlux()
                .map(FactHandle::getObject)
                .cast(ProposedEvent.class)
                .map(ProposedEvent::event)
                .map(event -> event.withId(idCaches.nextEventId()))
                .map(evt -> new Message<>(Message.MessageType.CREATE, evt))
                .subscribe(topics.eventTopic()::tryEmitNext);
    }

    public void stepRules() {
        kieSession.fireAllRules();
    }

}
