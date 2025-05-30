package com.earlybird.ticket.admin.application.dto;

import com.earlybird.ticket.admin.common.CouponType;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateCouponCommand(
        UUID couponId,
        String couponName,
        Integer discountRate,
        CouponType couponType
) {

}
