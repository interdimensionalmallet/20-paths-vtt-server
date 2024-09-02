package com.interdimensionalmallet.twtpthvtt.model;

import com.interdimensionalmallet.twtpthvtt.db.QueryOptions;

public record QueryOption(Long id, Long queryId, String stringOption, Long idOption, Boolean deleted) implements Item {

    public QueryOption withId(Long id) {
        return new QueryOption(id, queryId, stringOption, idOption, deleted);
    }

}
