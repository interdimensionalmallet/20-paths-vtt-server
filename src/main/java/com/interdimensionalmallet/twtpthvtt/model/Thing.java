package com.interdimensionalmallet.twtpthvtt.model;

public record Thing(Long id, String name) {
    public Thing {
        if (id == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
    }
}