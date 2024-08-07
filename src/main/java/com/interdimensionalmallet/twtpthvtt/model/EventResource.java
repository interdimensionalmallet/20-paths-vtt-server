package com.interdimensionalmallet.twtpthvtt.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public record EventResource(@QuerySqlField(index = true, notNull = true, descending = true) Long index, Event.EventType eventType, Event.EventStyle eventStyle, Long thingId, String resourceName, Integer resourceModifier) implements Event {
    public EventResource {
        if (index == null) {
            throw new IllegalArgumentException("index cannot be null");
        }
        if (eventType == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (eventType != Event.EventType.RESOURCE) {
            throw new IllegalArgumentException("type must be RESOURCE");
        }
        if (eventStyle == null) {
            throw new IllegalArgumentException("style cannot be null");
        }
        if (thingId == null) {
            throw new IllegalArgumentException("thingId cannot be null");
        }
        if (resourceName == null) {
            throw new IllegalArgumentException("resourceName cannot be null");
        }
        if (resourceModifier == null) {
            throw new IllegalArgumentException("resourceModifier cannot be null");
        }
        if (resourceModifier <= 0) {
            throw new IllegalArgumentException("resourceModifier must be greater than 0");
        }
    }
}
