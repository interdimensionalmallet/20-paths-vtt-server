package com.interdimensionalmallet.twtpthvtt.model;

public record Resource(Long id, Long thingId, String name, Integer count) {
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
