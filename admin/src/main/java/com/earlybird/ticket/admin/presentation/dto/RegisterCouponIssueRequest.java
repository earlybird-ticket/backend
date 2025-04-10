package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.CouponType;
import com.earlybird.ticket.admin.application.dto.IssueCouponCommand;

public record RegisterCouponIssueRequest(
        CouponType couponType,
        short discountRate,
        String couponName
) {

    public IssueCouponCommand toIssueCouponCommand() {
        return new IssueCouponCommand(couponType, discountRate, couponName);
    }
}
