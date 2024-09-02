package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.model.Item;
import com.interdimensionalmallet.twtpthvtt.model.Message;
import com.interdimensionalmallet.twtpthvtt.topics.Topic;
import com.interdimensionalmallet.twtpthvtt.topics.Topics;
import jakarta.annotation.PostConstruct;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuleItemHandler {

    private final KieSession kieSession;
    private final List<RuleItemHandlerMetadata<? extends Item>> ruleLinkHandlerMetadataList;
    private final Map<Class<? extends Item>, String[]> itemFields = new HashMap<>();
    private final Map<Class<? extends Item>, ConcurrentHashMap<Long, FactHandle>> factHandles = new HashMap<>();
    private final Topic<FactHandle> factHandleTopic;

    public RuleItemHandler(KieSession kieSession, Topics topics, List<RuleItemHandlerMetadata<? extends Item>> ruleItemHandlerMetadataList) {
        this.kieSession = kieSession;
        this.ruleLinkHandlerMetadataList = ruleItemHandlerMetadataList;
        this.factHandleTopic = topics.factsUpdatedTopic();
    }

    @PostConstruct
    public void init() {
        ruleLinkHandlerMetadataList.forEach(this::initHandler);
    }

    <T extends Item> void initHandler(RuleItemHandlerMetadata<T> handlerMetadata) {
        factHandles.put(handlerMetadata.itemClass(), new ConcurrentHashMap<>());
        itemFields.put(handlerMetadata.itemClass(), handlerMetadata.updateFields());
        handlerMetadata.allItems().subscribe(kieSession::insert);
        handlerMetadata.topic().asFlux()
                .map(this::handleEvent)
                .transform(Topics.publishMany(Message.MessageType.CREATE, factHandleTopic))
                .subscribe();
    }

    <T extends Item> FactHandle handleEvent(Message<T> eventMessage) {
        T item = eventMessage.payload();
        return switch (eventMessage.type()) {
            case CREATE -> insert(item);
            case DELETE -> delete(item);
            case UPDATE -> update(item);
        };
    }

    FactHandle insert(Item obj) {
        FactHandle handle = kieSession.insert(obj);
        factHandles.get(obj.getClass()).put(obj.id(), handle);
        return handle;
    }

    FactHandle delete(Item obj) {
        ConcurrentHashMap<Long, FactHandle> facts = factHandles.get(obj.getClass());
        FactHandle handle = facts.get(obj.id());
        if (handle != null) {
            kieSession.delete(handle);
            facts.remove(obj.id());
        }
        return handle;
    }

    FactHandle update(Item obj) {
        ConcurrentHashMap<Long, FactHandle> facts = factHandles.get(obj.getClass());
        FactHandle handle = facts.get(obj.id());
        if (obj.deleted()) {
            if (handle != null) {
                delete(obj);
            }
        } else {
            if (handle == null) {
                insert(obj);
            } else {
                kieSession.update(handle, obj, itemFields.get(obj.getClass()));
            }
        }
        return handle;
    }


}
