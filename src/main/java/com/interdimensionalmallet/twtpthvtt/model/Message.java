package com.interdimensionalmallet.twtpthvtt.model;

public record Message<T>(MessageType type, T payload) {

    public enum MessageType {
        CREATE,
        UPDATE,
        DELETE
    }

}
