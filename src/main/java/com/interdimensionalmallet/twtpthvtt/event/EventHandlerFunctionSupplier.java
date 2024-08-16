package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.WorldItem;

public interface EventHandlerFunctionSupplier<T extends WorldItem> {

    EventHandlerFunction<T> getHandlerFunction(Event.EventType eventType, Event.EventDirection eventDirection);

}
