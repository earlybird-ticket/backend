package com.earlybird.ticket.admin.application.dto;

import com.earlybird.ticket.admin.common.CouponType;
import lombok.Builder;

@Builder
public record RegisterCouponCommand(
        CouponType couponType,
        Integer discountRate,
        String couponName
) {

}
