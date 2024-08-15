package com.interdimensionalmallet.twtpthvtt.model;

import org.springframework.data.annotation.Id;

public record Thing(@Id Long id, String name) {

    public Thing withId(Long id) {
        return new Thing(id, name);
    }
}