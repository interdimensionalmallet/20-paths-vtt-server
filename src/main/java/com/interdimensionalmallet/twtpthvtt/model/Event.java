package com.interdimensionalmallet.twtpthvtt.model;

public interface Event {

    Long index();

    EventType eventType();

    enum EventType {

        ADD,
        REMOTE


    }

}
