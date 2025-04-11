package com.earlybird.ticket.reservation.domain.entity.constant;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.domain.dto.request.SeatAssignPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    INSTANCE_SEAT_RESERVATION(SeatAssignPayload.class,
                              Topic.ReservationToSeatForPreemption);

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

        public static final String ReservationToSeatForPreemption = "ReservationToSeatForPreemption";
    }
}