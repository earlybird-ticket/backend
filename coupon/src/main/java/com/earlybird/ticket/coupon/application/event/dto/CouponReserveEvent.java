package com.earlybird.ticket.coupon.application.event.dto;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.UUID;

public record CouponReserveEvent(
        PassportDto passportDto,
        UUID couponId

) implements EventPayload {

    public static EventPayload toPayload(PassportDto passportDto, UUID couponId) {
        return new CouponReserveEvent(passportDto, couponId);
    }
}
