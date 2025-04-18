package com.earlybird.ticket.coupon.application.event;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.coupon.application.event.dto.CouponConfirmPayload;
import com.earlybird.ticket.coupon.application.event.dto.CouponCreatePayload;
import com.earlybird.ticket.coupon.application.event.dto.CouponDeletePayload;
import com.earlybird.ticket.coupon.application.event.dto.CouponReserveEvent;
import com.earlybird.ticket.coupon.application.event.dto.CouponReservePayload;
import com.earlybird.ticket.coupon.application.event.dto.CouponUpdatePayload;
import com.earlybird.ticket.coupon.application.event.dto.ReturnCouponPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    CREATE_COUPON(CouponCreatePayload.class, Topic.ADMIN_TO_COUPON_TOPIC),
    DELETE_COUPON(CouponDeletePayload.class, Topic.ADMIN_TO_COUPON_TOPIC),
    UPDATE_COUPON(CouponUpdatePayload.class, Topic.ADMIN_TO_COUPON_TOPIC),
    // 문제 발생시 쿠폰 반환
    COUPON_RETURN(
            ReturnCouponPayload.class,
            Topic.RESERVATION_TO_COUPON_TOPIC
    ),
    // 결제 성공 시 쿠폰 확정
    COUPON_CONFIRM(
            CouponConfirmPayload.class,
            Topic.RESERVATION_TO_COUPON_TOPIC
    ),
    // 쿠폰에서 예약되었다고 요청이 온경우
    COUPON_FAIL(
            ReturnCouponPayload.class,
            Topic.RESERVATION_TO_COUPON_TOPIC
    ),
    COUPON_SUCCESS(
            CouponReservePayload.class,
            Topic.RESERVATION_TO_COUPON_TOPIC
    ),
    COUPON_RESERVE(
            CouponReserveEvent.class,
            Topic.COUPON_TO_RESERVATION_TOPIC
    ),
    ;

    private final Class<? extends EventPayload> payloadClass;
    private final String topic;

    public static EventType from(String type) {
        try {
            return valueOf(type);
        } catch (Exception e) {
            log.error("type={} is invalid", type, e);
            return null;
        }
    }

    public static class Topic {

        public static final String ADMIN_TO_COUPON_TOPIC = "AdminToCoupon";
        public static final String COUPON_TO_RESERVATION_TOPIC = "CouponToReservation";
        public static final String RESERVATION_TO_COUPON_TOPIC = "ReservationToCoupon";

    }
}