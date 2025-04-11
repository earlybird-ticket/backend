package com.earlybird.ticket.reservation.domain.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ConcertStatus {
    OPEN("OPEN"),
    CLOSED("CLOSED"),
    CANCELED("CANCELED");

    private final String value;

}
