package com.earlybird.ticket.payment.common;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.payment.application.event.dto.request.PaymentSuccessEvent;
import com.earlybird.ticket.payment.application.event.dto.request.PaymentFailEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    PAYMENT_SUCCESS(PaymentSuccessEvent.class, Topic.PAYMENT_TO_RESERVATION_TOPIC),
    PAYMENT_FAIL(PaymentFailEvent.class, Topic.PAYMENT_TO_RESERVATION_TOPIC),
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
    }
}
