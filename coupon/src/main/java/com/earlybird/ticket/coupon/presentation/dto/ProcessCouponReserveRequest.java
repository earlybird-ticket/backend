package com.earlybird.ticket.coupon.presentation.dto;

import com.earlybird.ticket.coupon.application.dto.ProcessCouponReserveCommand;
import java.util.UUID;

public record ProcessCouponReserveRequest(
        UUID couponId
) {

    public ProcessCouponReserveCommand toCommand() {
        return new ProcessCouponReserveCommand(couponId);
    }
}
