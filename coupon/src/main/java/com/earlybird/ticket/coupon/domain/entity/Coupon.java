package com.earlybird.ticket.coupon.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.coupon.domain.constant.CouponType;
import com.earlybird.ticket.coupon.domain.constant.CouponUsageStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID couponId;

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    private Integer discountRate;

    private String couponName;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCoupon> userCoupons;

    @Builder
    public Coupon(CouponType couponType, Integer discountRate, String couponName) {
        this.couponType = couponType;
        this.discountRate = discountRate;
        this.couponName = couponName;
    }

    public void addUserCoupon(UserCoupon userCoupon, Long userId) {
        userCoupon.createUserCoupon(userId);
        userCoupons.add(userCoupon);
    }

    public void deleteCoupon(Long userId) {
        super.delete(userId);
    }

    public void updateCoupon(
            Long userId, String couponName, Integer discountRate, CouponType couponType) {
        super.update(userId);
        this.couponName = couponName;
        this.discountRate = discountRate;
        this.couponType = couponType;
    }

    public void createCoupon(Long userId) {
        super.create(userId);
    }

    public void reserveCoupon(Long userId) {
        userCoupons.stream()
                .filter(uc -> uc.getUserId().equals(userId))
                .filter(uc -> uc.getUsageStatus() != CouponUsageStatus.RESERVED
                        && uc.getUsageStatus() != CouponUsageStatus.CONFIRMED)
                .forEach(uc -> uc.reservedCoupon(userId));
    }

    public void confirmCoupon(Long userId) {
        userCoupons.stream()
                .filter(uc -> uc.getUserId().equals(userId))
                .filter(uc -> uc.getUsageStatus() != CouponUsageStatus.CONFIRMED)
                .forEach(uc -> uc.confirmCoupon(userId));
    }
}
