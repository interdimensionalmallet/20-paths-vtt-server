package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.event.EventHandler;
import com.interdimensionalmallet.twtpthvtt.rules.ProposedEvent;
import com.interdimensionalmallet.twtpthvtt.rules.RuleEngine;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import jakarta.annotation.PostConstruct;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ProposedItemListener {

    private static final Logger LOG = LoggerFactory.getLogger(ProposedItemListener.class);

    private final KieSession kieSession;
    private final Mono<Void> allItemsLoaded;
    private final EventHandler eventHandler;

    public ProposedItemListener(KieSession kieSession, Mono<Void> allItemsLoaded, EventHandler eventHandler) {
        this.kieSession = kieSession;
        this.allItemsLoaded = allItemsLoaded;
        this.eventHandler = eventHandler;
    }

    @PostConstruct
    public void init() {
        Sinks.Many<FactHandle> proposals = Sinks.many().multicast().onBackpressureBuffer();

        addProposedItemListener(proposals);
        createDeleteProposalsListener(proposals);
        handleProposedEvents(proposals);


    }

    private void addProposedItemListener(Sinks.Many<FactHandle> proposals) {
        kieSession.addEventListener(new RuleRuntimeEventListener() {
            @Override
            public void objectInserted(ObjectInsertedEvent objectInsertedEvent) {
                LOG.debug("Object inserted: {}", objectInsertedEvent.getObject());
                if (objectInsertedEvent.getObject() instanceof ProposedEvent) {
                    LOG.debug("Emitting proposed event");
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
    }

    private void createDeleteProposalsListener(Sinks.Many<FactHandle> proposals) {
        proposals.asFlux().subscribe(kieSession::delete);
    }

    private void handleProposedEvents(Sinks.Many<FactHandle> proposals) {
        AtomicBoolean allItemsLoadedFlag = new AtomicBoolean(false);
        allItemsLoaded.then(
                Mono.just(true)
                        .doOnNext(ignored -> kieSession.fireAllRules())
                        .doOnNext(allItemsLoadedFlag::set)
        ).subscribe();
        proposals.asFlux()
                .filter(ignored -> allItemsLoadedFlag.get())
                .map(FactHandle::getObject)
                .map(obj -> (ProposedEvent) obj)
                .map(ProposedEvent::event)
                .doOnNext(evt -> LOG.debug("Pushing event {}", evt))
                .flatMap(eventHandler::pushEvent)
                .subscribe();
    }

}
