package com.interdimensionalmallet.twtpthvtt.db;

import com.interdimensionalmallet.twtpthvtt.model.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IdCaches {

    private final Map<Class<?>, IdCache> caches;

    public IdCaches(Repos repos) {
        caches = Map.of(
                Event.class, new IdCache(repos.events()::nextId),
                Resource.class, new IdCache(repos.resources()::nextId),
                Thing.class, new IdCache(repos.things()::nextId),
                Query.class, new IdCache(repos.queries()::nextId),
                QueryOption.class, new IdCache(repos.queryOptions()::nextId)
        );
    }

    public Long nextId(Class<?> clazz) {
        return caches.get(clazz).nextId();
    }

    public Long nextEventId() {
        return nextId(Event.class);
    }

    public Long nextThingId() {
        return nextId(Thing.class);
    }

    public Long nextResourceId() {
        return nextId(Resource.class);
    }

    public Long nextQueryId() {
        return nextId(Query.class);
    }

    public Long nextQueryOptionId() {
        return nextId(QueryOption.class);
    }

}
