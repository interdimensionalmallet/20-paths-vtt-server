package com.interdimensionalmallet.twtpthvtt.repo;

import com.interdimensionalmallet.twtpthvtt.model.Resource;
import com.interdimensionalmallet.twtpthvtt.model.Thing;
import org.apache.ignite.*;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorldRepoConfig {


    @Bean
    public IgniteConfiguration cfg() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        DataStorageConfiguration storageCfg = new DataStorageConfiguration();
        storageCfg.getDefaultDataRegionConfiguration().setPersistenceEnabled(true);
        cfg.setDataStorageConfiguration(storageCfg);
        return cfg;
    }

    @Bean
    public Ignite ignite(IgniteConfiguration cfg) {
        return Ignition.start(cfg);
    }

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

    @Bean(name = "linkSet")
    public IgniteSet<String> linkRepo(Ignite ignite) {
        return ignite.set("linkSet", new CollectionConfiguration());
    }

}
