package com.interdimensionalmallet.twtpthvtt.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.util.List;

public record Event(@QuerySqlField(index = true, notNull = true, descending = true) Long index, EventType eventType, EventStyle eventStyle,
                    Long sourceThingId, Long targetThingId,
                    Long thingId,
                    String resourceName, Integer resourceModifier,
                    String thingName) {


    static Event eventThing(Long index, EventStyle eventStyle, Long thingId, String thingName) {
        return new Event(index, EventType.THING, eventStyle, null, null, thingId, null, null, thingName);
    }

    static Event eventResource(Long index, EventStyle eventStyle, Long thingId, String resourceName, Integer resourceModifier) {
        return new Event(index, EventType.RESOURCE, eventStyle, null, null, thingId, resourceName, resourceModifier, null);
    }

    static Event eventLink(Long index, EventStyle eventStyle, Long sourceThingId, Long targetThingId) {
        return new Event(index, EventType.LINK, eventStyle, sourceThingId, targetThingId, null, null, null, null);
    }

    public Event withIndex(Long index) {
        return new Event(index, eventType, eventStyle, sourceThingId, targetThingId, thingId, resourceName, resourceModifier, thingName);
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

    public static Event fromRow(List<?> row) {
        Long index = (Long) row.get(0);
        EventType eventType = EventType.valueOf((String) row.get(1));
        EventStyle eventStyle = EventStyle.valueOf((String) row.get(2));
        Long sourceThingId = (Long) row.get(3);
        Long targetThingId = (Long) row.get(4);
        Long thingId = (Long) row.get(5);
        String resourceName = (String) row.get(6);
        Integer resourceModifier = (Integer) row.get(7);
        String thingName = (String) row.get(8);
        return new Event(index, eventType, eventStyle, sourceThingId, targetThingId, thingId, resourceName, resourceModifier, thingName);
    }
}
