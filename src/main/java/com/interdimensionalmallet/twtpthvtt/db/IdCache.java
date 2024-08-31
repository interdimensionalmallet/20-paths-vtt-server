package com.interdimensionalmallet.twtpthvtt.db;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

public class IdCache {

    private final ConcurrentLinkedQueue<Long> idCache = new ConcurrentLinkedQueue<>();
    private final Semaphore cacheAvailability = new Semaphore(0);
    private final Sinks.Many<Long> idLoadSignal;

    public IdCache(Supplier<Mono<Long>> idSupplier) {
        this.idLoadSignal = Sinks.many().unicast().onBackpressureBuffer();
        idLoadSignal.asFlux().flatMap(ignored -> idSupplier.get()).doOnNext(ignored -> cacheAvailability.release()).subscribe(idCache::add);
        Flux.range(0, 5).map(Integer::longValue).subscribe(idLoadSignal::tryEmitNext);
    }

    public Long nextId() {
        cacheAvailability.acquireUninterruptibly();
        Long nextId = idCache.poll();
        assert nextId != null; // This should be impossible due to the semaphore
        idLoadSignal.tryEmitNext(nextId);
        return nextId;
    }



}
