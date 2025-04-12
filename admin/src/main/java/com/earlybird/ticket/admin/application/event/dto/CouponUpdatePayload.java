package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.UpdateCouponCommand;
import com.earlybird.ticket.admin.common.CouponType;
import com.earlybird.ticket.common.entity.EventPayload;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CouponUpdatePayload(
        String passport,
        UUID couponId,
        String couponName,
        Integer discountRate,
        CouponType couponType
) implements EventPayload {

    public static CouponUpdatePayload toPayload(String passport, UpdateCouponCommand command) {
        return CouponUpdatePayload.builder()
                .passport(passport)
                .couponId(command.couponId())
                .couponName(command.couponName())
                .discountRate(command.discountRate())
                .couponType(command.couponType())
                .build();
    }
}
