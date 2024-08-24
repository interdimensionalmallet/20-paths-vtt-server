package com.interdimensionalmallet.twtpthvtt.model;

import org.springframework.data.annotation.Id;

public record Resource(@Id Long id, Long thingId, String name, Integer count, Boolean deleted) implements WorldItem {
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

    public WorldItem.Type type() {
        return WorldItem.Type.RESOURCE;
    }

    public Resource withDeleted(Boolean deleted) {
        return new Resource(id, thingId, name, count, deleted);
    }

    public Resource withModifier(Integer modifier) {
        return new Resource(id, thingId, name, count + modifier, deleted);
    }
    
}
