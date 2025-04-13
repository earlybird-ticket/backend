package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.RegisterCouponCommand;
import com.earlybird.ticket.admin.common.CouponType;
import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import lombok.Builder;

@Builder
public record CouponCreatePayload(
        PassportDto passport,
        String couponName,
        Integer discountRate,
        CouponType couponType
) implements EventPayload {

    public static CouponCreatePayload toPayload(
            PassportDto passport, RegisterCouponCommand command) {
        return CouponCreatePayload.builder()
                .passport(passport)
                .couponName(command.couponName())
                .discountRate(command.discountRate())
                .couponType(command.couponType())
                .build();
    }
}
