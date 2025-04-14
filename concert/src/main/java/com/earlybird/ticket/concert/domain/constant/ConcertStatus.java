package com.earlybird.ticket.concert.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConcertStatus {
    OPEN("OPEN"),
    CLOSE("CLOSE"),
    CANCEL("CANCEL");

    private final String status;
}
