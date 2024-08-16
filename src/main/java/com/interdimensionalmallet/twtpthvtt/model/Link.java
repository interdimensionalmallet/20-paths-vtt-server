package com.interdimensionalmallet.twtpthvtt.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

public record Link(Long sourceThingId, Long targetThingId, Boolean deleted) implements WorldItem {
    public Link {
        if (sourceThingId == null) {
            throw new IllegalArgumentException("sourceThingId cannot be null");
        }
        if (targetThingId == null) {
            throw new IllegalArgumentException("targetThingId cannot be null");
        }
    }

    public Link reverse() {
        return new Link(targetThingId, sourceThingId, deleted);
    }

    public WorldItem.Type type() {
        return WorldItem.Type.LINK;
    }

    public Link withDeleted(Boolean deleted) {
        return new Link(sourceThingId, targetThingId, deleted);
    }
}
