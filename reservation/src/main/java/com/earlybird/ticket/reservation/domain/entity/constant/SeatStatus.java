package com.earlybird.ticket.reservation.domain.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatStatus {
    PREEMPTED("PREEMPTED"),
    CONFIRMED("CONFIRMED"),
    FREE("FREE"),
    RESERVED("RESERVED");

    private final String status;
}
