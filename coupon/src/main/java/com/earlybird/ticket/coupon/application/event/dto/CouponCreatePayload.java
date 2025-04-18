package com.earlybird.ticket.coupon.application.event.dto;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.coupon.domain.constant.CouponType;
import com.earlybird.ticket.coupon.domain.entity.Coupon;

public record CouponCreatePayload(
        PassportDto passport,
        String couponName,
        Integer discountRate,
        CouponType couponType
) implements EventPayload {

    public Coupon toEntity() {
        return Coupon.builder()
                .couponName(couponName)
                .discountRate(discountRate)
                .couponType(couponType)
                .build();
    }
}
