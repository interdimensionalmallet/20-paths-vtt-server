package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Event;
import com.interdimensionalmallet.twtpthvtt.topics.Topic;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class EventItemHandlerMetadata implements RuleItemHandlerMetadata<Event> {

    private static final String[] FIELDS = {"eventPosition"};
    private final Repos repos;
    private final Topic<Event> eventTopic;

    public EventItemHandlerMetadata(Repos repos, Topics topics) {
        this.repos = repos;
        this.eventTopic = topics.eventTopic();
    }

    @Override
    public Flux<Event> allItems() {
        return repos.events().findByDeleted(false);
    }

    @Override
    public Topic<Event> topic() {
        return eventTopic;
    }

    @Override
    public String[] updateFields() {
        return FIELDS;
    }

    @Override
    public Class<Event> itemClass() {
        return Event.class;
    }
}
