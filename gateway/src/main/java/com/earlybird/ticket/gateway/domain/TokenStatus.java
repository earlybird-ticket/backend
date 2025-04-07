package com.earlybird.ticket.gateway.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenStatus {
    VALID,
    EXPIRED,
    INVALID,
    ;
}
