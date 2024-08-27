package com.interdimensionalmallet.twtpthvtt.topics;

import com.interdimensionalmallet.twtpthvtt.model.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinkWrapper<T> implements Topic<T> {
    private final Sinks.Many<Message<T>> sink;
    private final Class<T> type;
    public SinkWrapper(Class<T> type, Sinks.Many<Message<T>> sink) {
        this.sink = sink;
        this.type = type;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public Sinks.EmitResult tryEmitNext(Message<T> tMessage) {
        return sink.tryEmitNext(tMessage);
    }

    @Override
    public Sinks.EmitResult tryEmitComplete() {
        return sink.tryEmitComplete();
    }

    @Override
    public Sinks.EmitResult tryEmitError(Throwable throwable) {
        return sink.tryEmitError(throwable);
    }

    @Override
    public void emitNext(Message<T> tMessage, Sinks.EmitFailureHandler emitFailureHandler) {
        sink.emitNext(tMessage, emitFailureHandler);
    }

    @Override
    public void emitComplete(Sinks.EmitFailureHandler emitFailureHandler) {
        sink.emitComplete(emitFailureHandler);
    }

    @Override
    public void emitError(Throwable throwable, Sinks.EmitFailureHandler emitFailureHandler) {
        sink.emitError(throwable, emitFailureHandler);
    }

    @Override
    public int currentSubscriberCount() {
        return sink.currentSubscriberCount();
    }

    @Override
    public Flux<Message<T>> asFlux() {
        return sink.asFlux();
    }

    @Override
    public Object scanUnsafe(Attr attr) {
        return sink.scanUnsafe(attr);
    }
}
