package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.model.Item;
import com.interdimensionalmallet.twtpthvtt.model.Link;
import com.interdimensionalmallet.twtpthvtt.model.LinkId;
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
public class RuleItemListener {

    private final KieSession kieSession;
    private final Topics topics;
    private final List<RuleItemHandlerMetadata<? extends Item>> ruleLinkHandlerMetadataList;
    private final Map<Class<? extends Item>, String[]> itemFields = new HashMap<>();
    private final Map<Class<? extends Item>, ConcurrentHashMap<Long, FactHandle>> factHandles = new HashMap<>();
    private final ConcurrentHashMap<LinkId, FactHandle> linkHandles = new ConcurrentHashMap<>();
    private final Topic<FactHandle> factHandleTopic;

    public RuleItemListener(KieSession kieSession, Topics topics, List<RuleItemHandlerMetadata<? extends Item>> ruleItemHandlerMetadataList) {
        this.kieSession = kieSession;
        this.topics = topics;
        this.ruleLinkHandlerMetadataList = ruleItemHandlerMetadataList;
        this.factHandleTopic = topics.factsUpdatedTopic();
    }

    @PostConstruct
    public void init() {
        ruleLinkHandlerMetadataList.forEach(this::initHandler);
        topics.linkTopic().asFlux().subscribe(this::handleLink);
    }

    private <T extends Item> void initHandler(RuleItemHandlerMetadata<T> handlerMetadata) {
        factHandles.put(handlerMetadata.itemClass(), new ConcurrentHashMap<>());
        itemFields.put(handlerMetadata.itemClass(), handlerMetadata.updateFields());
        handlerMetadata.topic().asFlux()
                .map(this::handleEvent)
                .transform(Topics.publishMany(Message.MessageType.CREATE, factHandleTopic))
                .subscribe();
    }

    private <T extends Item> FactHandle handleEvent(Message<T> eventMessage) {
        T item = eventMessage.payload();
        return switch (eventMessage.type()) {
            case CREATE -> insert(item);
            case DELETE -> delete(item);
            case UPDATE -> update(item);
        };
    }

    private FactHandle insert(Item obj) {
        FactHandle handle = kieSession.insert(obj);
        factHandles.get(obj.getClass()).put(obj.id(), handle);
        return handle;
    }

    private FactHandle delete(Item obj) {
        ConcurrentHashMap<Long, FactHandle> facts = factHandles.get(obj.getClass());
        FactHandle handle = facts.get(obj.id());
        if (handle != null) {
            kieSession.delete(handle);
            facts.remove(obj.id());
        }
        return handle;
    }

    private FactHandle update(Item obj) {
        ConcurrentHashMap<Long, FactHandle> facts = factHandles.get(obj.getClass());
        FactHandle handle = facts.get(obj.id());
        if (obj.deleted()) {
            if (handle != null) {
                delete(obj);
            }
        } else {
            if (handle == null) {
                handle = insert(obj);
            } else {
                kieSession.update(handle, obj, itemFields.get(obj.getClass()));
            }
        }
        return handle;
    }

    private FactHandle handleLink(Message<Link> eventMessage) {
        Link link = eventMessage.payload();
        return switch (eventMessage.type()) {
            case CREATE -> insertLink(link);
            case DELETE, UPDATE -> deleteLink(link);
        };
    }

    private FactHandle insertLink(Link link) {
        FactHandle handle = kieSession.insert(link);
        linkHandles.put(link.id(), handle);
        return handle;
    }

    private FactHandle deleteLink(Link link) {
        LinkId id = link.id();
        FactHandle handle = linkHandles.get(id);
        kieSession.delete(handle);
        linkHandles.remove(id);
        return handle;
    }


}
