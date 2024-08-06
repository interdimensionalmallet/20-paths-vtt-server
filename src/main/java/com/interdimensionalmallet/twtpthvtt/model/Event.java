package com.interdimensionalmallet.twtpthvtt.model;

public interface Event {

    Long index();

    EventType eventType();

    EventStyle eventStyle();

    enum EventStyle {
        ADD,
        REMOVE
    }

    enum EventType {

        THING,
        RESOURCE,
        LINK


    }

}
