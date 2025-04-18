package com.earlybird.ticket.coupon.application.dto;

import java.util.UUID;

public record ProcessCouponReserveCommand(
        UUID couponId
) {

}
