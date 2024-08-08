package com.interdimensionalmallet.twtpthvtt.model;

public interface Event {

    Long index();

    EventType eventType();

    EventStyle eventStyle();

    Event withIndex(Long index);

    enum EventStyle {
        CREATE,
        DELETE
    }

    enum EventType {

        THING,
        RESOURCE,
        LINK


    }

}
