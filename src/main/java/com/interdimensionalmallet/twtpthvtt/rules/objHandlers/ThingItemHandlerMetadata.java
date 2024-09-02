package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Thing;
import com.interdimensionalmallet.twtpthvtt.topics.Topic;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class ThingItemHandlerMetadata implements RuleItemHandlerMetadata<Thing> {
    private static final String[] FIELDS = {"name"};
    private final Repos repos;
    private final Topic<Thing> thingTopic;

    public ThingItemHandlerMetadata(Repos repos, Topics topics) {
        this.repos = repos;
        this.thingTopic = topics.thingTopic();
    }

    @Override
    public Flux<Thing> allItems() {
        return repos.things().findByDeleted(false);
    }

    @Override
    public Topic<Thing> topic() {
        return thingTopic;
    }

    @Override
    public String[] updateFields() {
        return FIELDS;
    }

    @Override
    public Class<Thing> itemClass() {
        return Thing.class;
    }
}