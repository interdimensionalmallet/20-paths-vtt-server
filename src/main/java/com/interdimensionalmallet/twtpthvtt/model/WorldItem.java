package com.interdimensionalmallet.twtpthvtt.model;

public interface WorldItem {

    enum Type {
        THING,
        RESOURCE,
        LINK
    }

    Type type();

}
