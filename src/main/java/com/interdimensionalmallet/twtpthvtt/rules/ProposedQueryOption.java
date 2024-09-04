package com.interdimensionalmallet.twtpthvtt.rules;

import com.interdimensionalmallet.twtpthvtt.model.QueryOption;

public record ProposedQueryOption(QueryOption queryOption) implements ProposedItem {

    public Class<QueryOption> itemType() {
        return QueryOption.class;
    }
}
