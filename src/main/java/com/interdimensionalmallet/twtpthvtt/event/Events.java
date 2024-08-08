package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.model.Event;

public interface Events {

    void enqueueEvent(Event event);

    void advanceEvent();



}
