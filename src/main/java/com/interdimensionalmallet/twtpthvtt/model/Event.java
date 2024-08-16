package com.interdimensionalmallet.twtpthvtt.model;

import org.springframework.data.annotation.Id;

public record Event(@Id Long id,
                    Long previousId, Long nextId,
                    EventType eventType, EventStyle eventStyle,
                    Long thingId, String thingName,
                    Long linkId, Long targetThingId,
                    Long resourceId, String resourceName, Integer resourceModifier) {

    public enum EventPointers {
        CURRENT,
        QUEUE_HEAD,
        QUEUE_TAIL
    }

    public enum EventStyle {
        CREATE,
        DELETE
    }

    public enum EventType {

        THING,
        RESOURCE,
        LINK

    }

    static Event thingEvent(EventStyle eventStyle, Long thingId, String thingName) {
        return new Event(null, null, null, EventType.THING, eventStyle, thingId, thingName, null, null, null, null, null);
    }

    static Event linkEvent(EventStyle eventStyle, Long linkId, Long sourceThingId, Long targetThingId) {
        return new Event(null, null, null, EventType.LINK, eventStyle, sourceThingId, null, linkId, targetThingId, null, null, null);
    }

    static Event resourceEvent(EventStyle eventStyle, Long resourceId, Long thingId, String resourceName, Integer resourceModifier) {
        return new Event(null, null, null, EventType.RESOURCE, eventStyle, thingId, null, null, null, resourceId, resourceName, resourceModifier);
    }

    public Event withId(Long id) {
        return new Event(id, previousId, nextId, eventType, eventStyle, thingId, thingName, linkId, targetThingId, resourceId, resourceName, resourceModifier);
    }

    public Event withNextId(Long nextId) {
        return withChains(previousId, nextId);
    }

    public Event withPreviousId(Long previousId) {
        return withChains(previousId, nextId);
    }

    public Event withChains(Long previousId, Long nextId) {
        return new Event(id, previousId, nextId, eventType, eventStyle, thingId, thingName, linkId, targetThingId, resourceId, resourceName, resourceModifier);
    }

    public Event withIdAndChains(Long id, Long previousId, Long nextId) {
        return new Event(id, previousId, nextId, eventType, eventStyle, thingId, thingName, linkId, targetThingId, resourceId, resourceName, resourceModifier);
    }


}
