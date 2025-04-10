package com.earlybird.ticket.admin.application.dto;

import com.earlybird.ticket.admin.application.CouponType;

public record IssueCouponCommand(
        CouponType couponType,
        short discountRate,
        String couponName
) {

}
