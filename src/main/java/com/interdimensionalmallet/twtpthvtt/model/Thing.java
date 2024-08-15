package com.interdimensionalmallet.twtpthvtt.model;

public record Thing(Long id, String name) {

    public Thing withId(Long id) {
        return new Thing(id, name);
    }
}