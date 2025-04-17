package com.earlybird.ticket.coupon.domain.dto;

import com.earlybird.ticket.coupon.domain.constant.CouponType;
import com.earlybird.ticket.coupon.domain.constant.CouponUsageStatus;
import com.earlybird.ticket.coupon.domain.entity.UserCoupon;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

public record UserCouponResult(
        List<UserCouponResults> userCouponQueries
) {

    public static UserCouponResult of(
            List<UserCoupon> result
    ) {
        return new UserCouponResult(
                result.stream().map((userCoupon) ->
                                            UserCouponResults.builder()
                                                    .couponId(
                                                            userCoupon.getCoupon()
                                                                    .getCouponId())
                                                    .userCouponId(
                                                            userCoupon.getUserCouponId())
                                                    .couponName(
                                                            userCoupon.getCouponName())
                                                    .couponType(
                                                            userCoupon.getCouponType())
                                                    .discountRate(
                                                            userCoupon.getDiscountRate())
                                                    .usageStatus(
                                                            userCoupon.getUsageStatus())
                                                    .build()
                ).toList());
    }

    @Builder
    public record UserCouponResults(
            UUID couponId,
            UUID userCouponId,
            String couponName,
            CouponType couponType,
            Integer discountRate,
            CouponUsageStatus usageStatus
    ) {

    }
}
