package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.db.Repos;
import com.interdimensionalmallet.twtpthvtt.model.Message;
import com.interdimensionalmallet.twtpthvtt.model.Resource;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import org.flywaydb.core.Flyway;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuleResourceHandler implements RuleHandler{

    private final KieSession kieSession;
    private final ConcurrentHashMap<Long, FactHandle> resourceHandles = new ConcurrentHashMap<>();

    public RuleResourceHandler(KieSession kieSession, Repos repos, Topics topics) {
        this.kieSession = kieSession;
        repos.resources().findAll().subscribe(this::insertResource);
        topics.resourceTopic().asFlux().subscribe(this::handleResource);
    }

    private void handleResource(Message<Resource> resourceMessage) {
        Resource resource = resourceMessage.payload();
        switch (resourceMessage.type()) {
            case CREATE -> insertResource(resource);
            case DELETE -> removeResource(resource);
            case UPDATE -> updateResource(resource);
        }
    }

    private void insertResource(Resource resource) {
        FactHandle handle = kieSession.insert(resource);
        resourceHandles.put(resource.id(), handle);
    }

    private void updateResource(Resource resource) {
        FactHandle handle = resourceHandles.get(resource.id());
        if (resource.deleted()) {
            if (handle != null) {
                removeResource(resource);
            }
        } else {
            if (handle == null) {
                insertResource(resource);
            } else {
                kieSession.update(handle, resource, "count");
            }
        }
    }

    private void removeResource(Resource resource) {
        FactHandle handle = resourceHandles.get(resource.id());
        kieSession.delete(handle);
        resourceHandles.remove(resource.id());
    }
}
