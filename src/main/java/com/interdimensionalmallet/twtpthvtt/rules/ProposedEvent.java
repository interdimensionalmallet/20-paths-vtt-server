package com.interdimensionalmallet.twtpthvtt.rules;

import com.interdimensionalmallet.twtpthvtt.model.Event;

public record ProposedEvent(Event event) implements ProposedItem {

    public Class<Event> itemType() {
        return Event.class;
    }

}
