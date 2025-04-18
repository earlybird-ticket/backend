package com.earlybird.ticket.coupon.infrastructure.repository;

import com.earlybird.ticket.coupon.application.event.dto.CouponCreatePayload;
import com.earlybird.ticket.coupon.domain.CouponRepository;
import com.earlybird.ticket.coupon.domain.dto.CouponResult;
import com.earlybird.ticket.coupon.domain.dto.UserCouponResults;
import com.earlybird.ticket.coupon.domain.entity.Coupon;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponQueryDslRepository couponQueryDslRepository;
    private final CouponJpaRepository couponJpaRepository;

    @Override
    public UserCouponResults findAllUserCouponByUserId(Long userId) {
        return couponQueryDslRepository.findAllByUserId(userId);
    }

    @Override
    public Coupon save(CouponCreatePayload payload) {
        return couponJpaRepository.save(payload.toEntity());
    }

    @Override
    public Optional<Coupon> findByCouponId(UUID uuid) {
        return couponJpaRepository.findById(uuid);
    }

    @Override
    public List<CouponResult> findAllCoupon() {
        return couponQueryDslRepository.findAllCoupons();
    }

}
