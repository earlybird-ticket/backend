package com.earlybird.ticket.reservation.domain.entity.constant;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.application.dto.response.*;
import com.earlybird.ticket.reservation.domain.dto.request.*;
import com.earlybird.ticket.reservation.domain.dto.response.TestPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    //produce
    //Seat -> Reservation(예약 실패)
    RESERVATION_CREATE_FAIL(ReservationFailEvent.class,
                            Topic.RESERVATION_TO_SEAT_TOPIC),

    //좌석 확정 요청(Reservation -> Venue)
    SEAT_CONFIRM(ConfirmSeatEvent.class,
                 Topic.RESERVATION_TO_SEAT_TOPIC),

    //좌석 확정 요청(Reservation -> Venue)
    SEAT_RETURN(ReturnSeatEvent.class,
                Topic.RESERVATION_TO_SEAT_TOPIC),

    //쿠폰 확정 요청(Reservation -> Coupon)
    COUPON_CONFIRM(ConfirmCouponEvent.class,
                   Topic.RESERVATION_TO_COUPON_TOPIC),

    //쿠폰 성공 요청(Reservation -> Coupon)
    COUPON_SUCCESS(SuccessCouponEvent.class,
                   Topic.RESERVATION_TO_COUPON_TOPIC),

    //쿠폰 반환 요청(Reservation -> Coupon)
    COUPON_FAIL(ReturnCouponEvent.class,
                Topic.RESERVATION_TO_COUPON_TOPIC),

    //쿠폰 반환 요청(Reservation -> Coupon)
    COUPON_RETURN(ReturnCouponEvent.class,
                  Topic.RESERVATION_TO_COUPON_TOPIC),

    //CONSUME,


    RESERVATION_CREATE(CreateReservationPayload.class,
                       Topic.SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC),

    //Seat -> Reservation(확정 성공)
    SEAT_CONFIRM_SUCCESS(SeatConfirmSuccessPayload.class,
                         Topic.SEAT_TO_RESERVATION_TOPIC),

    //Seat -> Reservation(확정 실패)
    SEAT_CONFIRM_FAIL(SeatConfirmFailPayload.class,
                      Topic.SEAT_TO_RESERVATION_TOPIC),

    //Seat -> Reservation(반환 성공)
    SEAT_RETURN_SUCCESS(SeatReturnSuccessPayload.class,
                        Topic.SEAT_TO_RESERVATION_TOPIC),

    //Seat -> Reservation(반환 실패)
    SEAT_RETURN_FAIL(SeatReturnFailPayload.class,
                     Topic.SEAT_TO_RESERVATION_TOPIC),

    //Coupon -> Reservation(쿠폰 예약)
    COUPON_RESERVE(CouponReservePayload.class,
                   Topic.COUPON_TO_RESERVATION_TOPIC),

    //Payment -> Reservation(결제 성공)
    PAYMENT_SUCCESS(PaymentSuccessPayload.class,
                    Topic.PAYMENT_TO_RESERVATION_TOPIC),

    //Reservation -> Reservation(락 내 로직에서 발생한 DLT)
    RESERVATION_LOCK_FAIL(PreemptSeatDltEvent.class,
                          Topic.CREATE_RESERVATION_DLT),
    TEST_TOPIC(TestPayload.class,
               Topic.TEST_TOPIC);


    private final Class<? extends EventPayload> payloadClass;
    private final String topic;

    public static EventType from(String type) {
        if (type == null || type.isBlank()) {
            log.error("[EventType.from] NULL or BLANK eventType 입력됨");
            return null;
        }

        try {
            return valueOf(type.trim()); // 혹시 공백 포함되어 있을 경우 대비
        } catch (IllegalArgumentException e) {
            log.error("[EventType.from] Unknown eventType='{}'",
                      type,
                      e);
            return null;
        }
    }

    public static class Topic {

        public static final String RESERVATION_TO_SEAT_FOR_PREEMPTION_TOPIC = "ReservationToSeatForPreemption";
        public static final String RESERVATION_TO_SEAT_TOPIC = "ReservationToSeat";
        public static final String PAYMENT_TO_RESERVATION_TOPIC = "PaymentToReservation";
        public static final String RESERVATION_TO_COUPON_TOPIC = "ReservationToCoupon";
        public static final String SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC = "SeatToReservationForPreemption";
        public static final String SEAT_TO_RESERVATION_TOPIC = "SeatToReservation";
        public static final String COUPON_TO_RESERVATION_TOPIC = "CouponToReservation";
        public static final String CREATE_RESERVATION_DLT = "CreateReservation.DLT";
        public static final String TEST_TOPIC = "TestTopic";


    }
}