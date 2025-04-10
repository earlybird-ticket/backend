package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.UpdateCouponCommand;
import java.util.UUID;

public record UpdateCouponRequest(
        UUID couponId,
        String couponName

) {

    public UpdateCouponCommand toUpdateCouponCommand() {
        return new UpdateCouponCommand(couponId, couponName);
    }
}
