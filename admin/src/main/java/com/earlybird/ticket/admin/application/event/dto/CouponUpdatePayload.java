package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.UpdateCouponCommand;
import com.earlybird.ticket.admin.common.CouponType;
import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CouponUpdatePayload(
        PassportDto passport,
        UUID couponId,
        String couponName,
        Integer discountRate,
        CouponType couponType
) implements EventPayload {

    public static CouponUpdatePayload toPayload(PassportDto passport, UpdateCouponCommand command) {
        return CouponUpdatePayload.builder()
                .passport(passport)
                .couponId(command.couponId())
                .couponName(command.couponName())
                .discountRate(command.discountRate())
                .couponType(command.couponType())
                .build();
    }
}
