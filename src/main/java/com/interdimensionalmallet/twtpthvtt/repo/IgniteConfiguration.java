package com.interdimensionalmallet.twtpthvtt.repo;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.springframework.context.annotation.Bean;

public class IgniteConfiguration {

    @Bean
    public org.apache.ignite.configuration.IgniteConfiguration cfg() {
        org.apache.ignite.configuration.IgniteConfiguration cfg = new org.apache.ignite.configuration.IgniteConfiguration();
        DataStorageConfiguration storageCfg = new DataStorageConfiguration();
        storageCfg.getDefaultDataRegionConfiguration().setPersistenceEnabled(true);
        cfg.setDataStorageConfiguration(storageCfg);
        return cfg;
    }

    @Bean
    public Ignite ignite(org.apache.ignite.configuration.IgniteConfiguration cfg) {
        return Ignition.start(cfg);
    }

}
