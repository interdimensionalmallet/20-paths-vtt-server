package com.interdimensionalmallet.twtpthvtt.event;

import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.model.WorldItem;
import org.springframework.stereotype.Component;

@Component
public class EventHandlerFunctions {

    private final EventHandlerFunctionSupplier<? extends WorldItem> thingEventFunctions;
    private final EventHandlerFunctionSupplier<? extends WorldItem> linkEventFunctions;
    private final EventHandlerFunctionSupplier<? extends WorldItem> resourceEventFunctions;

    public EventHandlerFunctions(ThingEventFunctions thingEventFunctions, LinkEventFunctions linkEventFunctions, ResourceEventFunctions resourceEventFunctions) {
        this.thingEventFunctions = thingEventFunctions;
        this.linkEventFunctions = linkEventFunctions;
        this.resourceEventFunctions = resourceEventFunctions;
    }

    public EventHandlerFunction<? extends WorldItem> getHandlerFunction(Event event, Event.EventDirection eventDirection) {
        return switch (event.worldItemType()) {
            case THING -> thingEventFunctions.getHandlerFunction(event.eventType(), eventDirection);
            case LINK -> linkEventFunctions.getHandlerFunction(event.eventType(), eventDirection);
            case RESOURCE -> resourceEventFunctions.getHandlerFunction(event.eventType(), eventDirection);
        };
    }

}
