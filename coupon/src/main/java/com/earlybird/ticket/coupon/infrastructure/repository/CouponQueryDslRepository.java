package com.earlybird.ticket.coupon.infrastructure.repository;

import com.earlybird.ticket.coupon.domain.dto.CouponResult;
import com.earlybird.ticket.coupon.domain.dto.UserCouponResult;

public interface CouponQueryDslRepository {

    UserCouponResult findAllByUserId(Long userId);

    CouponResult findAllCoupons();
}
