package com.earlybird.ticket.reservation.domain.entity.constant;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.application.dto.response.SeatPreemptFailEvent;
import com.earlybird.ticket.reservation.application.dto.response.SeatPreemptSuccessEvent;
import com.earlybird.ticket.reservation.domain.dto.request.SeatReservePayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    INSTANCE_SEAT_RESERVATION(SeatReservePayload.class,
                              Topic.RESERVATION_TO_SEAT_FOR_PREEMPTION),
    INSTANCE_SEAT_RETURN(SeatReservePayload.class,
                         Topic.RESERVATION_TO_SEAT),
    INSTANCE_SEAT_CONFIRM(SeatReservePayload.class,
                          Topic.RESERVATION_TO_SEAT),
    COUPON_RESERVATION(SeatReservePayload.class,
                       Topic.RESERVATION_TO_COUPON),
    COUPON_CONFIRM(SeatReservePayload.class,
                   Topic.RESERVATION_TO_COUPON),
    COUPON_RETURN(SeatReservePayload.class,
                  Topic.RESERVATION_TO_COUPON),
    PAYMENT_REQUEST(SeatReservePayload.class,
                    Topic.RESERVATION_TO_PAYMENT),
    SEAT_PREEMPT_SUCCESS(SeatPreemptSuccessEvent.class,
                         Topic.SEAT_RESERVE_TOPIC),
    SEAT_PREEMPT_FAIL(SeatPreemptFailEvent.class,
                      Topic.SEAT_RESERVE_TOPIC);


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
        public static final String SEAT_RESERVE_TOPIC = "seatToReservation";

    }
}