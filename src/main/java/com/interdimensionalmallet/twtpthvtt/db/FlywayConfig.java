package com.interdimensionalmallet.twtpthvtt.db;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean
    public DataSource dataSource(@Value("${spring.flyway.url}") String dbUrl, @Value("${spring.flyway.user}")String dbUser, @Value("${spring.flyway.password}")String dbPass) {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(dbUrl);
        dataSource.setUser(dbUser);
        dataSource.setPassword(dbPass);
        return dataSource;
    }

    @Bean
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure().dataSource(dataSource).load();
    }

    @Bean
    public MigrateResult flywayMigrateComplete(Flyway flyway) {
        return flyway.migrate();
    }

}
