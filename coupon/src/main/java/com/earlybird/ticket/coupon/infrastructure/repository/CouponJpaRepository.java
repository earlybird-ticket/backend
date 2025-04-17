package com.earlybird.ticket.coupon.infrastructure.repository;

import com.earlybird.ticket.coupon.domain.entity.Coupon;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, UUID> {

}
