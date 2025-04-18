package com.earlybird.ticket.coupon.application.dto;

import com.earlybird.ticket.coupon.domain.constant.CouponType;
import java.util.UUID;

public record ProcessCouponQuery(
        UUID couponId,
        String couponName,
        CouponType couponType,
        Integer discountRate
) {

}
