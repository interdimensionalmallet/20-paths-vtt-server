package com.interdimensionalmallet.twtpthvtt.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public record Resource(Long id, @QuerySqlField Long thingId, @QuerySqlField String name, Integer count) {
    public Resource {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
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
