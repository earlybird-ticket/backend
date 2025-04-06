package com.earlybird.ticket.common.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ACCESS_TOKEN_EXPIRED("A01"),
    ACCESS_TOKEN_INVALID("A02"),
    ACCESS_TOKEN_NOT_FOUND("A03"),
    REFRESH_TOKEN_EXPIRED("A04"),
    REFRESH_TOKEN_INVALID("A05"),
    REFRESH_TOKEN_NOT_FOUND("A06"),

    FORBIDDEN("G01"),
    UNAUTHORIZED("G02"),
    BAD_REQUEST("G03"),
    NOT_FOUND("G04"),
    CONFLICT("G05"),

    FEIGN_BAD_REQUEST("F01"),
    FEIGN_NOT_FOUND("F02"),
    FEIGN_CONFLICT("F03"),
    FEIGN_UNAUTHORIZED("F04"),
    FEIGN_INTERNAL_SERVER_ERROR("F05");

    private final String message;

    public ErrorCode value() {
        return this;
    }
}
