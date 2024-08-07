package com.interdimensionalmallet.twtpthvtt.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public record Link(Long id, @QuerySqlField(index = true, notNull = true) Long sourceThingId, Long targetThingId) {
    public Link {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (sourceThingId == null) {
            throw new IllegalArgumentException("sourceThingId cannot be null");
        }
        if (targetThingId == null) {
            throw new IllegalArgumentException("targetThingId cannot be null");
        }
    }

}
