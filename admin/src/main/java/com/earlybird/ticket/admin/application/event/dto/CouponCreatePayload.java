package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.RegisterCouponCommand;
import com.earlybird.ticket.admin.common.CouponType;
import com.earlybird.ticket.common.entity.EventPayload;
import lombok.Builder;

@Builder
public record CouponCreatePayload(
        String passport,
        String couponName,
        Integer discountRate,
        CouponType couponType
) implements EventPayload {

    public static CouponCreatePayload toPayload(String passport, RegisterCouponCommand command) {
        return CouponCreatePayload.builder()
                .passport(passport)
                .couponName(command.couponName())
                .discountRate(command.discountRate())
                .couponType(command.couponType())
                .build();
    }
}
