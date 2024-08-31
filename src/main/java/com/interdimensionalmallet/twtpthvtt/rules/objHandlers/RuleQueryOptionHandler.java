package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Message;
import com.interdimensionalmallet.twtpthvtt.model.QueryOption;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuleQueryOptionHandler implements RuleHandler {

    private final KieSession kieSession;
    private final ConcurrentHashMap<Long, FactHandle> queryOptionHandles = new ConcurrentHashMap<>();

    public RuleQueryOptionHandler(KieSession kieSession, Repos repos, Topics topics) {
        this.kieSession = kieSession;
        repos.queryOptions().findAll().subscribe(this::insertQueryOption);
        topics.queryOptionTopic().asFlux().subscribe(this::handleQueryOption);
    }

    private void handleQueryOption(Message<QueryOption> queryMessage) {
        QueryOption query = queryMessage.payload();
        switch (queryMessage.type()) {
            case CREATE -> insertQueryOption(query);
            case DELETE -> deleteQueryOption(query);
            case UPDATE -> updateQueryOption(query);
        }
    }

    private void insertQueryOption(QueryOption query) {
        FactHandle handle = kieSession.insert(query);
        queryOptionHandles.put(query.id(), handle);
    }

    private void updateQueryOption(QueryOption query) {
        FactHandle handle = queryOptionHandles.get(query.id());
        kieSession.update(handle, query, "queryPosition");
    }

    private void deleteQueryOption(QueryOption query) {
        FactHandle handle = queryOptionHandles.get(query.id());
        kieSession.delete(handle);
        queryOptionHandles.remove(query.id());
    }
}
