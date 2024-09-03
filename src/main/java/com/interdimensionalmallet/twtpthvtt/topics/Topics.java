package com.interdimensionalmallet.twtpthvtt.topics;

import com.interdimensionalmallet.twtpthvtt.model.*;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Stream;

@Component
public record Topics(
        Topic<Event> eventTopic,
        Topic<Thing> thingTopic,
        Topic<Resource> resourceTopic,
        Topic<Link> linkTopic,
        Topic<WorldItem> worldItemTopic,
        Topic<Query> queryTopic,
        Topic<QueryOption> queryOptionTopic,
        Topic<FactHandle> factsUpdatedTopic
) {

    private static final Logger LOG = LoggerFactory.getLogger(Topics.class);

    public static <E> Function<Mono<E>, Mono<E>> create(Topic<E> topic) {
        return publish(Message.MessageType.CREATE, topic);
    }

    public static <E> Function<Flux<E>, Flux<E>> createMany(Topic<E> topic) {
        return publishMany(Message.MessageType.CREATE, topic);
    }

    public static <E> Function<Mono<E>, Mono<E>> update(Topic<E> topic) {
        return publish(Message.MessageType.UPDATE, topic);
    }

    public static <E> Function<Flux<E>, Flux<E>> updateMany(Topic<E> topic) {
        return publishMany(Message.MessageType.UPDATE, topic);
    }

    public static <E> Function<Mono<E>, Mono<E>> delete(Topic<E> topic) {
        return publish(Message.MessageType.DELETE, topic);
    }

    public static <E> Function<Flux<E>, Flux<E>> deleteMany(Topic<E> topic) {
        return publishMany(Message.MessageType.DELETE, topic);
    }

    public static <E> Function<Mono<E>, Mono<E>> publish(Message.MessageType type, Topic<E> topic) {
        return mono -> mono.map(e -> new Message<>(type, e))
                .doOnNext(msg -> LOG.debug("Publishing message: {}", msg))
                .doOnNext(topic::tryEmitNext)
                .map(Message::payload);
    }

    public static <E> Function<Flux<E>, Flux<E>> publishMany(Message.MessageType type, Topic<E> topic) {
        return flux -> flux.map(e -> new Message<>(type, e))
                .doOnNext(msg -> LOG.debug("Publishing message many: {}", msg))
                .doOnNext(topic::tryEmitNext)
                .map(Message::payload);
    }

    public Stream<Topic<?>> entityTopics() {
        return Stream.of(
                eventTopic,
                thingTopic,
                resourceTopic,
                linkTopic,
                queryTopic,
                queryOptionTopic
        );
    }

}
