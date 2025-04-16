package com.earlybird.ticket.coupon.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponUsageStatus {
    RESERVED("RESERVED"),
    CONFIRMED("CONFIRMED"),
    FREE("FREE");

    private final String status;
}
