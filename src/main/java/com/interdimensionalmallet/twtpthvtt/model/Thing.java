package com.interdimensionalmallet.twtpthvtt.model;

import org.springframework.data.annotation.Id;

public record Thing(@Id Long id, String name, Boolean deleted) implements WorldItem, Item {

    public Thing withId(Long id) {
        return new Thing(id, name, deleted);
    }

    public Thing withDeleted(Boolean deleted) {
        return new Thing(id, name, deleted);
    }

    public WorldItem.Type type() {
        return WorldItem.Type.THING;
    }

    //Adding to help Drools
    public String getName() {
        return name;
    }

}