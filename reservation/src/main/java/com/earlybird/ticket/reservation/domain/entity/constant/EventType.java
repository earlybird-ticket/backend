package com.earlybird.ticket.reservation.domain.entity.constant;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.application.dto.response.*;
import com.earlybird.ticket.reservation.domain.dto.request.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    //produce
    SEAT_INSTANCE_CONFIRM(ConfirmSeatEvent.class,
                          Topic.RESERVATION_TO_SEAT),
    SEAT_INSTANCE_PREEMPTION(PreemptSeatEvent.class,
                             Topic.RESERVATION_TO_SEAT_FOR_PREEMPTION),
    SEAT_INSTANCE_RETURN(ReturnSeatEvent.class,
                         Topic.RESERVATION_TO_SEAT),
    COUPON_RETURN(ReturnCouponEvent.class,
                  Topic.RESERVATION_TO_COUPON),
    COUPON_CONFIRM(ConfirmCouponEvent.class,
                   Topic.RESERVATION_TO_COUPON),
    COUPON_FAIL(ReturnCouponEvent.class,
                Topic.RESERVATION_TO_COUPON),
    COUPON_SUCCESS(SuccessCouponEvent.class,
                   Topic.RESERVATION_TO_COUPON),
    //consume,
    SEAT_PREEMPT_SUCCESS(SeatPreemptSuccessPayload.class,
                         Topic.SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC),
    SEAT_RETURN_SUCCESS(SeatReturnSuccessPayload.class,
                        Topic.SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC),
    SEAT_CONFIRM_SUCCESS(SeatConfirmSuccessPayload.class,
                         Topic.SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC),
    SEAT_PREEMPT_FAIL(SeatPreemptFailPayload.class,
                      Topic.SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC),
    SEAT_RETURN_FAIL(SeatReturnFailPayload.class,
                     Topic.SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC),
    SEAT_CONFIRM_FAIL(SeatConfirmFailPayload.class,
                      Topic.SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC),
    COUPON_RESERVE(CouponReservePayload.class,
                   Topic.COUPON_TO_RESERVATION),
    PAYMENT_SUCCESS(PaymentSuccessPayload.class,
                    Topic.PAYMENT_TO_RESERVATION),
    RESERVATION_LOCK_FAIL(PreemptSeatDltEvent.class,
                          Topic.CREATE_RESERVATION_DLT);


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
        public static final String PAYMENT_TO_RESERVATION = "PaymentToReservation";
        public static final String RESERVATION_TO_COUPON = "ReservationToCoupon";
        public static final String SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC = "SeatToReservationForPreemption";
        public static final String COUPON_TO_RESERVATION = "CouponToReservation";
        public static final String CREATE_RESERVATION_DLT = "CreateReservation.DLT";


    }
}