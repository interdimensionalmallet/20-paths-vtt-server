package com.interdimensionalmallet.twtpthvtt.rules;

import com.interdimensionalmallet.twtpthvtt.db.IdCaches;
import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Message;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import jakarta.annotation.PostConstruct;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

@Component
public class RuleEngine {

    private final KieSession kieSession;
    private final IdCaches idCaches;
    private final Topics topics;

    public RuleEngine(KieSession kieSession, Topics topics, IdCaches idCaches) {
        this.kieSession = kieSession;
        this.topics = topics;
        this.idCaches = idCaches;
    }

    @PostConstruct
    public void init() {
        kieSession.setGlobal("idCache", idCaches);
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

        topics.factsUpdatedTopic().asFlux()
                .windowTimeout(Integer.MAX_VALUE, Duration.ofSeconds(5))
                .doOnNext(window -> System.out.println("windowed released"))
                .flatMap(Flux::collectList)
                .doOnNext(list -> System.out.println("windowed collected: " + list))
                .filter(Predicate.not(List::isEmpty))
                .subscribe(this::stepSubscribe);
    }

    private void stepSubscribe(Object ignored) {
        stepRules();
    }

    public void stepRules() {
        kieSession.fireAllRules();
    }

}
