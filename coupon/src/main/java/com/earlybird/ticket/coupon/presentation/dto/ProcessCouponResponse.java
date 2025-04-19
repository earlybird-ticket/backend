package com.earlybird.ticket.coupon.presentation.dto;

import com.earlybird.ticket.coupon.domain.constant.CouponType;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProcessCouponResponse(
        UUID couponId,
        String couponName,
        CouponType couponType,
        Integer discountRate

) {

}
