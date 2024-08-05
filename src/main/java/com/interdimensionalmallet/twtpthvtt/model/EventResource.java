package com.interdimensionalmallet.twtpthvtt.model;

public record EventResource(Long index, Event.EventType eventType, Long thingId, String resourceName, Integer resourceModifier) implements Event {
    public EventResource {
        if (index == null) {
            throw new IllegalArgumentException("index cannot be null");
        }
        if (eventType == null) {
            throw new IllegalArgumentException("type cannot be null");
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
