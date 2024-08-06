package com.interdimensionalmallet.twtpthvtt.repo;

import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.EventLink;
import com.interdimensionalmallet.twtpthvtt.model.EventResource;
import com.interdimensionalmallet.twtpthvtt.model.EventThing;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteQueue;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

public class EventRepoConfig {

    @Bean
    public IgniteQueue<Event> futureEvents(Ignite ignite) {
        return ignite.queue("futureEvents", 0, new CollectionConfiguration());
    }

    @Bean(name = "eventRepoConfig")
    public CacheConfiguration<Long, Event> eventRepoConfig() {
        CacheConfiguration<Long, Event> cfg = new CacheConfiguration<>("eventRepo");
        cfg.setIndexedTypes(Long.class, Event.class, EventThing.class, EventResource.class, EventLink.class);
        return cfg;
    }

    @Bean(name = "eventRepo")
    public IgniteCache<Long, Event> eventRepo(Ignite ignite, @Qualifier("eventRepoConfig") CacheConfiguration<Long, Event> cfg) {
        return ignite.getOrCreateCache(cfg);
    }


}
