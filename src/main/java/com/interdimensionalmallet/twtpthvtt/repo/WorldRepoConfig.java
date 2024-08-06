package com.interdimensionalmallet.twtpthvtt.repo;

import com.interdimensionalmallet.twtpthvtt.model.Link;
import com.interdimensionalmallet.twtpthvtt.model.Resource;
import com.interdimensionalmallet.twtpthvtt.model.Thing;
import org.apache.ignite.*;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorldRepoConfig {


    @Bean(name = "thingIDSequence")
    public IgniteAtomicSequence thingIDSequence(Ignite ignite) {
        return ignite.atomicSequence("thingIDSequence", 0, true);
    }


    @Bean(name = "thingRepoConfig")
    public CacheConfiguration<Long, Thing> thingRepoConfig() {
        CacheConfiguration<Long, Thing> cfg = new CacheConfiguration<>("thingRepo");
        cfg.setIndexedTypes(Long.class, Thing.class);
        return cfg;
    }

    @Bean(name = "thingRepo")
    public IgniteCache<Long, Thing> thingRepo(Ignite ignite, @Qualifier("thingRepoConfig") CacheConfiguration<Long, Thing> cfg) {
        return ignite.getOrCreateCache(cfg);
    }

    @Bean(name = "resourceIDSequence")
    public IgniteAtomicSequence resourceIDSequence(Ignite ignite) {
        return ignite.atomicSequence("resourceIDSequence", 0, true);
    }

    @Bean(name = "resourceRepoConfig")
    public CacheConfiguration<Long, Resource> resourceRepoConfig() {
        CacheConfiguration<Long, Resource> cfg = new CacheConfiguration<>("resourceRepo");
        cfg.setIndexedTypes(Long.class, Resource.class);
        return cfg;
    }

    @Bean(name = "resourceRepo")
    public IgniteCache<Long, Resource> resourceRepo(Ignite ignite, @Qualifier("resourceRepoConfig") CacheConfiguration<Long, Resource> cfg) {
        return ignite.getOrCreateCache(cfg);
    }

    @Bean(name = "linkIDSequence")
    public IgniteAtomicSequence linkIDSequence(Ignite ignite) {
        return ignite.atomicSequence("linkIDSequence", 0, true);
    }

    @Bean(name = "linkRepoConfig")
    public CacheConfiguration<Long, Link> linkRepoConfig() {
        CacheConfiguration<Long, Link> cfg = new CacheConfiguration<>("linkRepo");
        cfg.setIndexedTypes(Long.class, Link.class);
        return cfg;
    }

    @Bean(name = "linkRepo")
    public IgniteCache<Long, Link> linkRepo(Ignite ignite, @Qualifier("linkRepoConfig") CacheConfiguration<Long, Link> cfg) {
        return ignite.getOrCreateCache(cfg);
    }


}
