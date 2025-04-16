package com.earlybird.ticket.coupon.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.coupon.domain.constant.CouponType;
import com.earlybird.ticket.coupon.domain.constant.CouponUsageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_user_coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID userCouponId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Coupon coupon;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private CouponType couponType;

    @Column(nullable = false)
    private Integer discountRate;

    @Column(length = 100, nullable = false)
    private String couponName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponUsageStatus usageStatus;

    private UUID reservationId;

    @Builder
    public UserCoupon(
            Coupon coupon, Long userId, CouponType couponType, Integer discountRate,
            String couponName, CouponUsageStatus usageStatus, UUID reservationId
    ) {
        this.coupon = coupon;
        this.userId = userId;
        this.couponType = couponType;
        this.discountRate = discountRate;
        this.couponName = couponName;
        this.usageStatus = usageStatus;
        this.reservationId = reservationId;
    }

    public void createUserCoupon(Long userId) {
        super.create(userId);
    }

    public void reservedCoupon(Long userId) {
        this.usageStatus = CouponUsageStatus.RESERVED;
        super.update(userId);
    }

    public void confirmCoupon(Long userId) {
        this.usageStatus = CouponUsageStatus.CONFIRMED;
        super.update(userId);
    }
}
