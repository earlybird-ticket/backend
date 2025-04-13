package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.admin.application.dto.DeleteCouponCommand;
import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CouponDeletePayload(
        PassportDto passport,
        UUID couponId
) implements EventPayload {

    public static CouponDeletePayload toPayload(PassportDto passport, DeleteCouponCommand command) {
        return CouponDeletePayload.builder()
                .passport(passport)
                .couponId(command.couponId())
                .build();
    }
}
