package com.earlybird.ticket.coupon.domain.dto;

import com.earlybird.ticket.coupon.domain.constant.CouponType;
import com.earlybird.ticket.coupon.domain.entity.Coupon;
import java.util.List;
import java.util.UUID;

public record CouponResult(
        List<CouponResults> couponResults
) {

    public static CouponResult of(List<Coupon> result) {
        return new CouponResult(result.stream().map((coupon) -> new CouponResults(
                coupon.getCouponId(),
                coupon.getCouponName(),
                coupon.getCouponType(),
                coupon.getDiscountRate()
        )).toList());
    }

    public record CouponResults(
            UUID couponId,
            String couponName,
            CouponType couponType,
            Integer discountRate
    ) {

    }
}
