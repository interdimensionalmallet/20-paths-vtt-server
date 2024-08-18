package com.interdimensionalmallet.twtpthvtt.db;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@Component
public class Repos {

    private final Things things;
    private final Resources resources;
    private final Links links;
    private final Events events;
    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate entityTemplate;

    public Repos(Things things, Resources resources, Links links, Events events, DatabaseClient databaseClient) {
        this.things = things;
        this.resources = resources;
        this.links = links;
        this.events = events;
        this.databaseClient = databaseClient;
        this.entityTemplate = new R2dbcEntityTemplate(databaseClient.getConnectionFactory());
    }

    public Things things() {
        return things;
    }

    public Resources resources() {
        return resources;
    }

    public Links links() {
        return links;
    }

    public Events events() {
        return events;
    }

    public DatabaseClient databaseClient() {
        return databaseClient;
    }

    public R2dbcEntityTemplate entityTemplate() {
        return entityTemplate;
    }

}
