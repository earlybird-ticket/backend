package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.UpdateCouponCommand;
import com.earlybird.ticket.admin.common.CouponType;
import java.util.UUID;

public record UpdateCouponRequest(
        UUID couponId,
        String couponName,
        Integer discountRate,
        CouponType couponType
) {

    public UpdateCouponCommand toUpdateCouponCommand() {
        return UpdateCouponCommand.builder()
                .couponId(couponId)
                .couponName(couponName)
                .discountRate(discountRate)
                .couponType(couponType)
                .build();
    }
}
