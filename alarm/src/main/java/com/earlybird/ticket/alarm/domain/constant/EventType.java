package com.earlybird.ticket.alarm.domain.constant;

import com.earlybird.ticket.alarm.domain.dto.request.CreateAlarmEvent;
import com.earlybird.ticket.common.entity.EventPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {


    //Reservation -> Reservation(락 내 로직에서 발생한 DLT)
    ALARM_CREATE_REQUEST(CreateAlarmEvent.class,
                         Topic.VENUE_TO_ALARM_TOPIC);


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

        public static final String VENUE_TO_ALARM_TOPIC = "VenueToAlarm";
        public static final String RESERVATION_TO_ALARM_TOPIC = "ReservationToAlarm";
        public static final String PAYMENT_TO_ALARM_TOPIC = "PaymentToAlarm";


    }
}