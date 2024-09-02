package com.interdimensionalmallet.twtpthvtt.db;

import com.interdimensionalmallet.twtpthvtt.model.Link;
import com.interdimensionalmallet.twtpthvtt.model.Message;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PersistenceListener {

    private final Repos repos;

    public PersistenceListener(Repos repos, Topics topics) {
        this.repos = repos;
        topics.entityTopics()
                .filter(topic -> topic.type() != Link.class)
                .map(topic -> topic.asFlux().flatMap(this::persistItem))
                .forEach(Flux::subscribe);
        topics.linkTopic().asFlux().flatMap(this::persistLink).subscribe();
    }

    public Mono<Link> persistLink(Message<Link> linkMessage) {
        return switch (linkMessage.type()) {
            case CREATE -> repos.entityTemplate().insert(linkMessage.payload());
            case DELETE -> repos.links()
                    .deleteByKey(
                            linkMessage.payload().sourceThingId(),
                            linkMessage.payload().targetThingId()
                    )
                    .then(Mono.just(linkMessage.payload()));
            case UPDATE -> Mono.just(linkMessage.payload());
        };
    }

    public <T> Mono<T> persistItem(Message<T> itemMessage) {
        return switch (itemMessage.type()) {
            case CREATE -> repos.entityTemplate().insert(itemMessage.payload());
            case UPDATE -> repos.entityTemplate().update(itemMessage.payload());
            case DELETE -> repos.entityTemplate().delete(itemMessage.payload());
        };
    }

}
