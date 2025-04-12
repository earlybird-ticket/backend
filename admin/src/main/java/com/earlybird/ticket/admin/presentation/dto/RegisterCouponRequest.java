package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.RegisterCouponCommand;
import com.earlybird.ticket.admin.common.CouponType;

public record RegisterCouponRequest(
        CouponType couponType,
        Integer discountRate,
        String couponName
) {

    public RegisterCouponCommand toRegisterCouponCommand() {
        return RegisterCouponCommand.builder()
                .couponName(couponName)
                .discountRate(discountRate)
                .couponType(couponType)
                .build();
    }
}
