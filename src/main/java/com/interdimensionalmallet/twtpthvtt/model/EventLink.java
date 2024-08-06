package com.interdimensionalmallet.twtpthvtt.model;

public record EventLink(Long index, Event.EventType eventType, Event.EventStyle eventStyle, Long sourceThingId, Long targetThingId) implements Event{
    public EventLink {
        if (index == null) {
            throw new IllegalArgumentException("index cannot be null");
        }
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

}
