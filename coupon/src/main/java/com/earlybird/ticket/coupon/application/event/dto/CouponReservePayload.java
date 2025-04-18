package com.earlybird.ticket.coupon.application.event.dto;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.coupon.domain.constant.CouponType;
import java.util.UUID;

public record CouponReservePayload(UUID couponId,
                                   String couponName,
                                   CouponType couponType,
                                   UUID reservationId,
                                   PassportDto passportDto) implements EventPayload {


}