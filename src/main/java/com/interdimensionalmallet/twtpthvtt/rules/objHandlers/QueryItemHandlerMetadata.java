package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Query;
import com.interdimensionalmallet.twtpthvtt.topics.Topic;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class QueryItemHandlerMetadata implements RuleItemHandlerMetadata<Query> {
    private static final String[] FIELDS = {"selectedOptionId"};
    private final Repos repos;
    private final Topic<Query> queryTopic;

    public QueryItemHandlerMetadata(Repos repos, Topics topics) {
        this.repos = repos;
        this.queryTopic = topics.queryTopic();
    }

    @Override
    public Flux<Query> allItems() {
        return repos.queries().findByDeleted(false);
    }

    @Override
    public Topic<Query> topic() {
        return queryTopic;
    }

    @Override
    public String[] updateFields() {
        return FIELDS;
    }

    @Override
    public Class<Query> itemClass() {
        return Query.class;
    }
}
