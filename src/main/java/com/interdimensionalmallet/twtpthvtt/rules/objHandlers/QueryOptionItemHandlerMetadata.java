package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.QueryOption;
import com.interdimensionalmallet.twtpthvtt.topics.Topic;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class QueryOptionItemHandlerMetadata implements RuleItemHandlerMetadata<QueryOption> {
    private static final String[] FIELDS = new String[0];
    private final Repos repos;
    private final Topic<QueryOption> queryOptionTopic;

    public QueryOptionItemHandlerMetadata(Repos repos, Topics topics) {
        this.repos = repos;
        this.queryOptionTopic = topics.queryOptionTopic();
    }

    @Override
    public Flux<QueryOption> allItems() {
        return repos.queryOptions().findByDeleted(false);
    }

    @Override
    public Topic<QueryOption> topic() {
        return queryOptionTopic;
    }

    @Override
    public String[] updateFields() {
        return FIELDS;
    }

    @Override
    public Class<QueryOption> itemClass() {
        return QueryOption.class;
    }
}