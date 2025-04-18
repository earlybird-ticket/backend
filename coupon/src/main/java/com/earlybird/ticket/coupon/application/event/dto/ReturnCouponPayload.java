package com.earlybird.ticket.coupon.application.event.dto;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.UUID;

// Coupon Event 실패했을때 받는 DTO
public record ReturnCouponPayload(

        UUID couponId,
        PassportDto passportDto,
        String code
)
        implements EventPayload {

}
