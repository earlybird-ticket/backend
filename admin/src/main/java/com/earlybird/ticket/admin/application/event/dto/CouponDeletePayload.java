package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.DeleteCouponCommand;
import com.earlybird.ticket.common.entity.EventPayload;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CouponDeletePayload(
        String passport,
        UUID couponId
) implements EventPayload {

    public static CouponDeletePayload toPayload(String passport, DeleteCouponCommand command) {
        return CouponDeletePayload.builder()
                .passport(passport)
                .couponId(command.couponId())
                .build();
    }
}
