package com.earlybird.ticket.coupon.domain.dto;

import com.earlybird.ticket.coupon.domain.constant.CouponType;
import com.earlybird.ticket.coupon.domain.constant.CouponUsageStatus;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

public record UserCouponResults(
        List<UserCoupons> userCoupons
) {

    public static UserCouponResults of(
            List<UserCoupons> result
    ) {
        return new UserCouponResults(result);
    }

    @Builder
    public record UserCoupons(
            UUID couponId,
            UUID userCouponId,
            String couponName,
            CouponType couponType,
            Integer discountRate,
            CouponUsageStatus usageStatus
    ) {

    }
}
