package com.interdimensionalmallet.twtpthvtt.rules;

import com.interdimensionalmallet.twtpthvtt.model.Query;

public record ProposedQuery(Query query) implements ProposedItem {

    public Class<Query> itemType() {
        return Query.class;
    }
}
