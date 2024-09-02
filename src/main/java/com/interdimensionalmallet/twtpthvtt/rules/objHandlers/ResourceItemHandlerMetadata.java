package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Resource;
import com.interdimensionalmallet.twtpthvtt.topics.Topic;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class ResourceItemHandlerMetadata implements RuleItemHandlerMetadata<Resource> {
    private static final String[] FIELDS = {"count"};
    private final Repos repos;
    private final Topic<Resource> resourceTopic;

    public ResourceItemHandlerMetadata(Repos repos, Topics topics) {
        this.repos = repos;
        this.resourceTopic = topics.resourceTopic();
    }

    @Override
    public Flux<Resource> allItems() {
        return repos.resources().findByDeleted(false);
    }

    @Override
    public Topic<Resource> topic() {
        return resourceTopic;
    }

    @Override
    public String[] updateFields() {
        return FIELDS;
    }

    @Override
    public Class<Resource> itemClass() {
        return Resource.class;
    }
}