package com.interdimensionalmallet.twtpthvtt.model;

public record EventLink(Long index, Event.EventType eventType, String linkId) {
    public EventLink {
        if (index == null) {
            throw new IllegalArgumentException("index cannot be null");
        }
        if (eventType == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (linkId == null) {
            throw new IllegalArgumentException("linkId cannot be null");
        }
    }

}
