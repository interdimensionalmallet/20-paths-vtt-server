package com.interdimensionalmallet.twtpthvtt.topics;

import com.interdimensionalmallet.twtpthvtt.model.Message;
import reactor.core.publisher.Sinks;

public interface Topic<T> extends Sinks.Many<Message<T>> {

    Class<T> type();

}
