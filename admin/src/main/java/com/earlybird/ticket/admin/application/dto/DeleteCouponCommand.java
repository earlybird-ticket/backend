package com.earlybird.ticket.admin.application.dto;

import java.util.UUID;

public record DeleteCouponCommand(
        UUID couponId
) {

}
