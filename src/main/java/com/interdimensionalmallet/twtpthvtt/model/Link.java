package com.interdimensionalmallet.twtpthvtt.model;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class Link {

    private Link() {
        // no-op
    }

    public static String linkID(Long thing1, Long thing2) {
        return Math.min(thing1, thing2) + "-" + Math.max(thing1, thing2);
    }

}
