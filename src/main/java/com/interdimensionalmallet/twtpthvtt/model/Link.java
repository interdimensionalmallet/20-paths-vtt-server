package com.interdimensionalmallet.twtpthvtt.model;


public record Link(Long sourceThingId, Long targetThingId) implements WorldItem {
    public Link {
        if (sourceThingId == null) {
            throw new IllegalArgumentException("sourceThingId cannot be null");
        }
        if (targetThingId == null) {
            throw new IllegalArgumentException("targetThingId cannot be null");
        }
    }

    public Link reverse() {
        return new Link(targetThingId, sourceThingId);
    }

    public WorldItem.Type type() {
        return WorldItem.Type.LINK;
    }

}
