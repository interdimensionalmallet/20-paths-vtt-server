package com.interdimensionalmallet.twtpthvtt.model;

import org.springframework.data.annotation.Id;

public record Event(@Id Long id,
                    Long previousId, Long nextId,
                    WorldItem.Type worldItemType, EventType eventType,
                    Long thingId, String thingName,
                    Long targetThingId,
                    Long resourceId, String resourceName, Integer resourceModifier) {

    public enum EventPointers {
        CURRENT,
        QUEUE_HEAD,
        QUEUE_TAIL
    }

    public enum EventType {
        CREATE,
        REMOVE
    }

    public enum EventDirection {
        FORWARD,
        REVERSE
    }


    public static Event thingEvent(EventType eventType, Long thingId, String thingName) {
        return new Event(null, null, null, WorldItem.Type.THING, eventType, thingId, thingName, null, null, null, null);
    }

    public static Event linkEvent(EventType eventType, Long sourceThingId, Long targetThingId) {
        return new Event(null, null, null, WorldItem.Type.LINK, eventType, sourceThingId, null, targetThingId, null, null, null);
    }

    public static Event resourceEvent(EventType eventType, Long resourceId, Long thingId, String resourceName, Integer resourceModifier) {
        return new Event(null, null, null, WorldItem.Type.RESOURCE, eventType, thingId, null, null, resourceId, resourceName, resourceModifier);
    }

    public Event withId(Long id) {
        return new Event(id, previousId, nextId, worldItemType, eventType, thingId, thingName, targetThingId, resourceId, resourceName, resourceModifier);
    }

    public Event withNextId(Long nextId) {
        return withChains(previousId, nextId);
    }

    public Event withPreviousId(Long previousId) {
        return withChains(previousId, nextId);
    }

    public Event withChains(Long previousId, Long nextId) {
        return new Event(id, previousId, nextId, worldItemType, eventType, thingId, thingName, targetThingId, resourceId, resourceName, resourceModifier);
    }

    public Event withIdAndChains(Long id, Long previousId, Long nextId) {
        return new Event(id, previousId, nextId, worldItemType, eventType, thingId, thingName, targetThingId, resourceId, resourceName, resourceModifier);
    }


}
