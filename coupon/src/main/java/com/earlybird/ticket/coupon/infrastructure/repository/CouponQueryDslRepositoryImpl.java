package com.earlybird.ticket.coupon.infrastructure.repository;

import static com.earlybird.ticket.coupon.domain.entity.QCoupon.coupon;
import static com.earlybird.ticket.coupon.domain.entity.QUserCoupon.userCoupon;

import com.earlybird.ticket.coupon.domain.dto.CouponResult;
import com.earlybird.ticket.coupon.domain.dto.UserCouponResults;
import com.earlybird.ticket.coupon.domain.dto.UserCouponResults.UserCoupons;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponQueryDslRepositoryImpl implements CouponQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public UserCouponResults findAllByUserId(Long userId) {

        List<UserCouponResults.UserCoupons> result = jpaQueryFactory
                .select(Projections.constructor(
                        UserCoupons.class,
                        userCoupon.coupon.couponId,
                        userCoupon.userCouponId,
                        userCoupon.couponName,
                        userCoupon.couponType,
                        userCoupon.discountRate,
                        userCoupon.usageStatus
                ))
                .from(userCoupon)
                .join(userCoupon.coupon, coupon)
                .where(userCoupon.userId.eq(userId))
                .fetch();

        return UserCouponResults.of(result);
    }

    @Override
    public List<CouponResult> findAllCoupons() {

        return jpaQueryFactory
                .select(Projections.constructor(
                        CouponResult.class,
                        coupon.couponId,
                        coupon.couponName,
                        coupon.couponType,
                        coupon.discountRate
                ))
                .from(coupon)
                .where(coupon.deletedAt.isNull())
                .fetch();
    }
}
