package com.interdimensionalmallet.twtpthvtt.db.query;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Query;
import com.interdimensionalmallet.twtpthvtt.model.QueryOption;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class QueryHandler {

    private final Repos repos;

    public QueryHandler(Repos repos) {
        this.repos = repos;
    }

    public Mono<Query> createQuery(Query query) {
        return repos.queries().nextId()
                .map(query::withId)
                .flatMap(repos.entityTemplate()::insert);
    }

    public Mono<QueryOption> createQueryOption(QueryOption queryOption) {
        return repos.queryOptions().nextId()
                .map(queryOption::withId)
                .flatMap(repos.entityTemplate()::insert);
    }

    public Mono<Query> answerQuery(Query query, Long selectedOptionId) {
        return repos.queries().findById(query.id())
                .map(q -> q.withSelectedOptionId(selectedOptionId))
                .flatMap(repos.entityTemplate()::update);
    }

}
