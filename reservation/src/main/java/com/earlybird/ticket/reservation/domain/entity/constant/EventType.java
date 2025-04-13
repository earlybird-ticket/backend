package com.earlybird.ticket.reservation.domain.entity.constant;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.application.dto.response.ReserveCouponPayload;
import com.earlybird.ticket.reservation.application.dto.response.SeatPreemptFailPayload;
import com.earlybird.ticket.reservation.application.dto.response.SeatPreemptSuccessPayload;
import com.earlybird.ticket.reservation.domain.dto.request.ConfirmCouponPayload;
import com.earlybird.ticket.reservation.domain.dto.request.FailCouponPayload;
import com.earlybird.ticket.reservation.domain.dto.request.PreemptSeatPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    //produce
    SEAT_INSTANCE_CONFIRM(PreemptSeatPayload.class,
                          Topic.RESERVATION_TO_SEAT),
    SEAT_INSTANCE_PREEMPTION(PreemptSeatPayload.class,
                             Topic.RESERVATION_TO_SEAT_FOR_PREEMPTION),
    COUPON_RETURN(PreemptSeatPayload.class,
                  Topic.RESERVATION_TO_COUPON),
    PAYMENT_REQUEST(PreemptSeatPayload.class,
                    Topic.RESERVATION_TO_PAYMENT),
    SEAT_INSTANCE_RETURN(PreemptSeatPayload.class,
                         Topic.RESERVATION_TO_SEAT),
    //consume,
    SEAT_PREEMPT_SUCCESS(SeatPreemptSuccessPayload.class,
                         Topic.RESERVATION_TO_SEAT),
    SEAT_PREEMPT_FAIL(SeatPreemptFailPayload.class,
                      Topic.RESERVATION_TO_SEAT),
    COUPON_CONFIRM(ReserveCouponPayload.class,
                   Topic.RESERVATION_TO_COUPON),
    COUPON_SUCCESS(ConfirmCouponPayload.class,
                   Topic.RESERVATION_TO_COUPON),
    COUPON_FAIL(FailCouponPayload.class,
                Topic.RESERVATION_TO_COUPON);


    private final Class<? extends EventPayload> payloadClass;
    private final String topic;

    public static EventType from(String type) {
        try {
            return valueOf(type);
        } catch (Exception e) {
            log.error("[EventType.from] type={} is invalid",
                      type,
                      e);
            return null;
        }
    }

    public static class Topic {

        public static final String RESERVATION_TO_SEAT_FOR_PREEMPTION = "ReservationToSeatForPreemption";
        public static final String RESERVATION_TO_SEAT = "ReservationToSeat";
        public static final String RESERVATION_TO_PAYMENT = "ReservationToPayment";
        public static final String RESERVATION_TO_COUPON = "ReservationToCoupon";
        public static final String SEAT_RESERVE_TOPIC = "SeatToReservation";

    }
}