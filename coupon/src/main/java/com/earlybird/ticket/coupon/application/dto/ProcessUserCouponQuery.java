package com.earlybird.ticket.coupon.application.dto;

import com.earlybird.ticket.coupon.domain.constant.CouponType;
import com.earlybird.ticket.coupon.domain.constant.CouponUsageStatus;
import com.earlybird.ticket.coupon.domain.dto.UserCouponResults.UserCoupons;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

public record ProcessUserCouponQuery(
        List<UserCouponQuery> userCouponQueries
) {

    public static ProcessUserCouponQuery of(List<UserCoupons> result) {
        return new ProcessUserCouponQuery(result.stream().map((userCoupon) ->
                                                                      UserCouponQuery.builder()
                                                                              .couponId(
                                                                                      userCoupon.couponId())
                                                                              .userCouponId(
                                                                                      userCoupon.userCouponId())
                                                                              .couponName(
                                                                                      userCoupon.couponName())
                                                                              .couponType(
                                                                                      userCoupon.couponType())
                                                                              .discountRate(
                                                                                      userCoupon.discountRate())
                                                                              .usageStatus(
                                                                                      userCoupon.usageStatus())
                                                                              .build()
        ).toList());
    }

    @Builder
    public record UserCouponQuery(
            UUID couponId,
            UUID userCouponId,
            String couponName,
            CouponType couponType,
            Integer discountRate,
            CouponUsageStatus usageStatus
    ) {

    }
}
