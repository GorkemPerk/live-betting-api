package com.gorkem.livebettingapi.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum Status {
    UNDEFINED,
    PENDING,
    ACTIVE,
    PASSIVE;

    @JsonCreator
    public static Status parseType(String value) {
        return Arrays
                .stream(values())
                .filter(item -> item.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(UNDEFINED);
    }
}
