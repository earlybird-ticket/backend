package com.earlybird.ticket.coupon.presentation.dto;

import com.earlybird.ticket.coupon.application.dto.ProcessUserCouponQuery;
import com.earlybird.ticket.coupon.domain.constant.CouponType;
import com.earlybird.ticket.coupon.domain.constant.CouponUsageStatus;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

public record ProcessUserCouponResponse(
        List<UserCouponList> userCouponLists
) {

    public static ProcessUserCouponResponse of(ProcessUserCouponQuery query) {
        return new ProcessUserCouponResponse(
                query.userCouponQueries().stream().map((userCoupon) ->
                                                               UserCouponList.builder()
                                                                       .userCouponId(
                                                                               userCoupon.userCouponId())
                                                                       .couponId(
                                                                               userCoupon.couponId())
                                                                       .couponName(
                                                                               userCoupon.couponName())
                                                                       .couponType(
                                                                               userCoupon.couponType())
                                                                       .discountRate(
                                                                               userCoupon.discountRate())
                                                                       .usageStatus(
                                                                               userCoupon.usageStatus())
                                                                       .build()
                        )
                        .toList());
    }

    @Builder
    public record UserCouponList(
            UUID userCouponId,
            UUID couponId,
            String couponName,
            CouponType couponType,
            Integer discountRate,
            CouponUsageStatus usageStatus
    ) {

    }
}
