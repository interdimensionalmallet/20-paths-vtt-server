package com.interdimensionalmallet.twtpthvtt.repo;

import com.interdimensionalmallet.twtpthvtt.model.Event;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteQueue;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgniteRunnable;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Configuration
public class EventRepoConfig {

    @Bean(name = "futureEvents")
    public IgniteQueue<Event> futureEvents(Ignite ignite) {
        return ignite.queue("futureEvents", 0, new CollectionConfiguration().setBackups(1));
    }

    @Bean(name = "eventRepoConfig2")
    public CacheConfiguration<Long, Event> eventRepoConfig() {
        CacheConfiguration<Long, Event> cfg = new CacheConfiguration<>("eventRepo");
        cfg.setIndexedTypes(Long.class, Event.class);
        return cfg;
    }

    @Bean(name = "eventRepo")
    public IgniteCache<Long, Event> eventRepo(Ignite ignite, @Qualifier("eventRepoConfig2") CacheConfiguration<Long, Event> cfg) {
        return ignite.getOrCreateCache(cfg);
    }


}
