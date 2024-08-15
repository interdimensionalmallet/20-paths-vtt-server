package com.interdimensionalmallet.twtpthvtt.model;


import org.springframework.data.annotation.Id;

public record Link(@Id Long id, Long sourceThingId, Long targetThingId) {
    public Link {
        if (sourceThingId == null) {
            throw new IllegalArgumentException("sourceThingId cannot be null");
        }
        if (targetThingId == null) {
            throw new IllegalArgumentException("targetThingId cannot be null");
        }
    }

    public Link withId(Long id) {
        return new Link(id, sourceThingId, targetThingId);
    }

    public Link reverse() {
        return new Link(id, targetThingId, sourceThingId);
    }

}
