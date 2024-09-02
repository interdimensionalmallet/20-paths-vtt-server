package com.interdimensionalmallet.twtpthvtt.rules.objHandlers;

import com.interdimensionalmallet.twtpthvtt.model.Item;
import com.interdimensionalmallet.twtpthvtt.topics.Topic;
import reactor.core.publisher.Flux;

public interface RuleItemHandlerMetadata<T extends Item> {

    public Flux<T> allItems();

    Topic<T> topic();

    String[] updateFields();

    Class<T> itemClass();

}
