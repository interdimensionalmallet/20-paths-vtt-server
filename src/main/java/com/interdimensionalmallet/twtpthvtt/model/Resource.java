package com.interdimensionalmallet.twtpthvtt.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public record Resource(@QuerySqlField(notNull = true) Long id, @QuerySqlField(index = true, notNull = true) Long thingId, @QuerySqlField(notNull = true) String name, @QuerySqlField(notNull = true) Integer count) {
    public Resource {
        if (thingId == null) {
            throw new IllegalArgumentException("thingId cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (count == null) {
            throw new IllegalArgumentException("count cannot be null");
        }
    }
}
