package com.earlybird.ticket.coupon.infrastructure.repository;

import static com.earlybird.ticket.coupon.domain.entity.QCoupon.coupon;
import static com.earlybird.ticket.coupon.domain.entity.QUserCoupon.userCoupon;

import com.earlybird.ticket.coupon.domain.dto.CouponResult;
import com.earlybird.ticket.coupon.domain.dto.UserCouponResult;
import com.earlybird.ticket.coupon.domain.entity.Coupon;
import com.earlybird.ticket.coupon.domain.entity.UserCoupon;
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
    public UserCouponResult findAllByUserId(Long userId) {

        List<UserCoupon> result = jpaQueryFactory
                .select(Projections.constructor(
                        UserCoupon.class,
                        userCoupon.userCouponId,
                        userCoupon.coupon,
                        userCoupon.userId,
                        userCoupon.couponType,
                        userCoupon.discountRate,
                        userCoupon.couponName,
                        userCoupon.usageStatus
                ))
                .from(userCoupon)
                .join(userCoupon.coupon, coupon)
                .where(userCoupon.userId.eq(userId))
                .fetch();

        return UserCouponResult.of(result);
    }

    @Override
    public CouponResult findAllCoupons() {

        List<Coupon> result = jpaQueryFactory
                .select(Projections.constructor(
                        Coupon.class,
                        coupon.couponId,
                        coupon.couponType,
                        coupon.discountRate,
                        coupon.couponName
                ))
                .from(coupon)
                .where(coupon.deletedAt.isNull())
                .fetch();

        return CouponResult.of(result);
    }
}
