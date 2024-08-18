package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.WorldItem;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface EventHandlerFunction<T extends WorldItem> {

    Mono<T> apply(Event t);

}
