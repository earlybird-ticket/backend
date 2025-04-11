package com.earlybird.ticket.reservation.domain.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CouponType {

    SIGN_UP("SIGNUP");

    private final String value;
}
