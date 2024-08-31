package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Message;
import com.interdimensionalmallet.twtpthvtt.model.Query;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuleQueryHandler implements RuleHandler {
    private final KieSession kieSession;
    private final ConcurrentHashMap<Long, FactHandle> queryHandles = new ConcurrentHashMap<>();

    public RuleQueryHandler(KieSession kieSession, Repos repos, Topics topics) {
        this.kieSession = kieSession;
        repos.queries().findAll().subscribe(this::insertQuery);
        topics.queryTopic().asFlux().subscribe(this::handleQuery);
    }

    private void handleQuery(Message<Query> queryMessage) {
        Query query = queryMessage.payload();
        switch (queryMessage.type()) {
            case CREATE -> insertQuery(query);
            case DELETE -> deleteQuery(query);
            case UPDATE -> updateQuery(query);
        }
    }

    private void insertQuery(Query query) {
        FactHandle handle = kieSession.insert(query);
        queryHandles.put(query.id(), handle);
    }

    private void updateQuery(Query query) {
        FactHandle handle = queryHandles.get(query.id());
        kieSession.update(handle, query, "queryPosition");
    }

    private void deleteQuery(Query query) {
        FactHandle handle = queryHandles.get(query.id());
        kieSession.delete(handle);
        queryHandles.remove(query.id());
    }
}
