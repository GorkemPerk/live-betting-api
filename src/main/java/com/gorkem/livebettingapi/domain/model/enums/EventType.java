package com.gorkem.livebettingapi.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum EventType {
    UNDEFINED(0),
    FOOTBALL(120),
    BASKETBALL(60),
    TENNIS(180);

    private final int durationMinutes;

    EventType(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    @JsonCreator
    public static EventType parseType(String value) {
        return Arrays
                .stream(values())
                .filter(item -> item.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(UNDEFINED);
    }
}
