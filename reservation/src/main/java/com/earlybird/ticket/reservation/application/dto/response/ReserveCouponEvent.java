package com.earlybird.ticket.reservation.application.dto.response;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.reservation.domain.entity.constant.CouponType;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public record ReserveCouponEvent(UUID couponId,
                                 String couponName,
                                 CouponType couponType,
                                 List<UUID> reservationList,
                                 PassportDto passport) implements EventPayload {

    @Builder
    public ReserveCouponEvent(UUID couponId,
                              String couponName,
                              CouponType couponType,
                              List<UUID> reservationList,
                              PassportDto passport) {
        this.couponId = couponId;
        this.couponName = couponName;
        this.couponType = couponType;
        this.reservationList = reservationList;
        this.passport = passport;
    }
}