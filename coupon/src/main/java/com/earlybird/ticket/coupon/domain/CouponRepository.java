package com.earlybird.ticket.coupon.domain;

import com.earlybird.ticket.coupon.application.event.dto.CouponCreatePayload;
import com.earlybird.ticket.coupon.domain.dto.CouponResult;
import com.earlybird.ticket.coupon.domain.dto.UserCouponResults;
import com.earlybird.ticket.coupon.domain.entity.Coupon;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {

    UserCouponResults findAllUserCouponByUserId(Long userId);

    Coupon save(CouponCreatePayload payload);

    Optional<Coupon> findByCouponId(UUID uuid);

    List<CouponResult> findAllCoupon();
}
