package com.earlybird.ticket.admin.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponType {
    BIRTHDAY("BIRTHDAY"),
    ANNIVERSARY("ANNIVERSARY"),
    MEMBERSHIP("MEMBERSHIP");

    private final String type;
}
