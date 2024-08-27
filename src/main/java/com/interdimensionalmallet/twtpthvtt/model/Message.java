package com.interdimensionalmallet.twtpthvtt.model;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.function.Function;

public record Message<T>(MessageType type, T payload) {

    public enum MessageType {
        CREATE,
        UPDATE,
        DELETE
    }

    public static <E> Function<Mono<E>, Mono<E>> publish(MessageType type, Sinks.Many<Message<E>> topic) {
        return mono -> mono.map(e -> new Message<>(type, e))
                .doOnNext(topic::tryEmitNext)
                .map(Message::payload);
    }

}
