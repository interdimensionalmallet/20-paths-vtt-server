package com.interdimensionalmallet.twtpthvtt.model;


public record LinkId(Long sourceThingId, Long targetThingId) {
    public LinkId {
        if (sourceThingId == null) {
            throw new IllegalArgumentException("sourceThingId cannot be null");
        }
        if (targetThingId == null) {
            throw new IllegalArgumentException("targetThingId cannot be null");
        }
    }
}
