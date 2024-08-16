package com.interdimensionalmallet.twtpthvtt.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

public record Link(@Id @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) LinkId linkId, Boolean deleted) implements WorldItem {
    public Link {
        if (linkId == null) {
            throw new IllegalArgumentException("linkId cannot be null");
        }
    }

    public Link reverse() {
        return new Link(new LinkId(linkId().targetThingId(), linkId().sourceThingId()), deleted);
    }

    public WorldItem.Type type() {
        return WorldItem.Type.LINK;
    }

    public Link withDeleted(Boolean deleted) {
        return new Link(linkId, deleted);
    }
}
