package com.earlybird.ticket.coupon.infrastructure.repository;

import com.earlybird.ticket.coupon.domain.dto.CouponResult;
import com.earlybird.ticket.coupon.domain.dto.UserCouponResults;
import java.util.List;

public interface CouponQueryDslRepository {

    UserCouponResults findAllByUserId(Long userId);

    List<CouponResult> findAllCoupons();
}
