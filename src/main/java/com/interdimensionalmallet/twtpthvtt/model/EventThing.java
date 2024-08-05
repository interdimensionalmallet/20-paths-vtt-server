package com.interdimensionalmallet.twtpthvtt.model;

public record EventThing(Long index, Long thingId, String thingName) implements Event {
    public EventThing {
        if (index == null) {
            throw new IllegalArgumentException("index cannot be null");
        }
        if (thingId == null) {
            throw new IllegalArgumentException("thingId cannot be null");
        }
    }
}
