package com.earlybird.ticket.coupon.domain.dto;

import com.earlybird.ticket.coupon.domain.constant.CouponType;
import java.util.UUID;

public record CouponResult(
        UUID couponId,
        String couponName,
        CouponType couponType,
        Integer discountRate
) {

}
