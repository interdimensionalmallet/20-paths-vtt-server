package com.interdimensionalmallet.twtpthvtt.model;

public record EventThing(Long index, Event.EventType eventType, Event.EventStyle eventStyle, Long thingId, String thingName) implements Event {
    public EventThing {
        if (index == null) {
            throw new IllegalArgumentException("index cannot be null");
        }
        if (eventType == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (eventType != Event.EventType.THING) {
            throw new IllegalArgumentException("type must be THING");
        }
        if (eventStyle == null) {
            throw new IllegalArgumentException("style cannot be null");
        }
        if (thingId == null) {
            throw new IllegalArgumentException("thingId cannot be null");
        }
    }
}
