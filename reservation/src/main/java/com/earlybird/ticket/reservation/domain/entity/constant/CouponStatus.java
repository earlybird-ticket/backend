package com.earlybird.ticket.reservation.domain.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponStatus {
    RESERVED("RESERVED"),
    CONFIRMED("CONFIRMED"),
    FREE("FREE");

    private final String value;
}
