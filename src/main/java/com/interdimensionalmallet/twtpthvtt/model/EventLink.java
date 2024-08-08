package com.interdimensionalmallet.twtpthvtt.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public record EventLink(@QuerySqlField(index = true, notNull = true, descending = true) Long index, Event.EventType eventType, Event.EventStyle eventStyle, Long sourceThingId, Long targetThingId) implements Event{
    public EventLink {
        if (eventType == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (eventType != Event.EventType.LINK) {
            throw new IllegalArgumentException("type must be LINK");
        }
        if (eventStyle == null) {
            throw new IllegalArgumentException("style cannot be null");
        }
        if (sourceThingId == null) {
            throw new IllegalArgumentException("sourceThingId cannot be null");
        }
        if (targetThingId == null) {
            throw new IllegalArgumentException("targetThingId cannot be null");
        }
    }

    public Event withIndex(Long index) {
        return new EventLink(index, eventType, eventStyle, sourceThingId, targetThingId);
    }

}
