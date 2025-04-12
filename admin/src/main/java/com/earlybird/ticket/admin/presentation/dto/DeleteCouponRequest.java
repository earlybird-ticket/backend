package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.DeleteCouponCommand;
import java.util.UUID;

public record DeleteCouponRequest(
        UUID couponId
) {

    public DeleteCouponCommand toDeleteCouponCommand() {
        return DeleteCouponCommand.builder().couponId(couponId).build();
    }
}
