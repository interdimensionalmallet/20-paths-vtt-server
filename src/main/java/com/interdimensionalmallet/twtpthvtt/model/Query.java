package com.interdimensionalmallet.twtpthvtt.model;


public record Query(Long id, String queryName, Long targetId, Long selectedOptionId) {
    public Query {
        if (queryName == null) {
            throw new IllegalArgumentException("queryName cannot be null");
        }
    }

    public Query withId(Long id) {
        return new Query(id, queryName, targetId, selectedOptionId);
    }

    public Query withSelectedOptionId(Long selectedOptionId) {
        return new Query(id, queryName, targetId, selectedOptionId);
    }

}
