package com.earlybird.ticket.payment.common;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.payment.application.event.dto.request.PaymentCancelFailedEvent;
import com.earlybird.ticket.payment.application.event.dto.request.PaymentFailEvent;
import com.earlybird.ticket.payment.application.event.dto.request.PaymentSuccessEvent;
import com.earlybird.ticket.payment.application.event.dto.response.ReservationCancelPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    PAYMENT_SUCCESS(PaymentSuccessEvent.class, Topic.PAYMENT_TO_RESERVATION_TOPIC),
    PAYMENT_FAIL(PaymentFailEvent.class, Topic.PAYMENT_TO_RESERVATION_TOPIC),
    PAYMENT_CANCEL_FAIL(PaymentCancelFailedEvent.class, Topic.PAYMENT_TO_PAYMENT_TOPIC_DLQ),
    //    PAYMENT_CANCEL(PaymentExpiredEvent.class, Topic.PAYMENT_TO_PAYMENT_TOPIC),
    RESERVATION_CANCEL(ReservationCancelPayload.class, Topic.RESERVATION_TO_PAYMENT_TOPIC),
    ;

    private final Class<? extends EventPayload> payloadClass;
    private final String topic;

    public static EventType from(String type) {
        try {
            return valueOf(type);
        } catch (Exception e) {
            log.error("[EventType.from] type={}", type, e);
            return null;
        }
    }

    public static class Topic {

        public static final String PAYMENT_TO_RESERVATION_TOPIC = "PaymentToReservation";
        public static final String RESERVATION_TO_PAYMENT_TOPIC = "ReservationToPayment";
        //        public static final String PAYMENT_TO_PAYMENT_TOPIC = "PaymentToPayment";
        public static final String PAYMENT_TO_PAYMENT_TOPIC_DLQ = "PaymentToPayment.DLT";

    }
}
