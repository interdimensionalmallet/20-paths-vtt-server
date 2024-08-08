package com.interdimensionalmallet.twtpthvtt.repo;

import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.Link;
import com.interdimensionalmallet.twtpthvtt.model.Resource;
import com.interdimensionalmallet.twtpthvtt.model.Thing;
import org.apache.ignite.IgniteAtomicSequence;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteQueue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public record Repos(
        @Qualifier("thingRepo") IgniteCache<Long, Thing> things,
        @Qualifier("resourceRepo") IgniteCache<Long, Resource> resources,
        @Qualifier("linkRepo") IgniteCache<Long, Link> links,
        @Qualifier("thingIDSequence") IgniteAtomicSequence thingIDSequence,
        @Qualifier("resourceIDSequence") IgniteAtomicSequence resourceIDSequence,
        @Qualifier("linkIDSequence") IgniteAtomicSequence linkIDSequence,
        @Qualifier("eventRepo") IgniteCache<Long, Event> events,
        @Qualifier("futureEvents") IgniteQueue<Event> futureEvents) {
}
