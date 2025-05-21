package com.gorkem.livebettingapi.domain.constant;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCodes {
    UNDEFINED(0),
    BUSINESS(1000),
    NOT_FOUND(1001),
    REQUEST_VALIDATION(1003),
    BET_ODDS_CHANGED(1004),
    BET_SLIP_TIMEOUT(1005),
    MAX_COUPON_LIMIT(1006),
    MAX_INVESTMENT_LIMIT(1007);

    private final int code;

    ErrorCodes(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }
}