package com.earlybird.ticket.coupon.application.event.dto;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.UUID;

public record CouponDeletePayload(
        PassportDto passport,
        UUID couponId
) implements EventPayload {

}
