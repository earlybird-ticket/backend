package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.IssueCouponCommand;
import com.earlybird.ticket.admin.common.CouponType;

public record RegisterCouponIssueRequest(
        CouponType couponType,
        short discountRate,
        String couponName
) {

    public IssueCouponCommand toIssueCouponCommand() {
        return new IssueCouponCommand();
    }
}
