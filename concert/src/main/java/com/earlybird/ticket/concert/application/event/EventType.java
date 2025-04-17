package com.earlybird.ticket.concert.application.event;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.concert.application.event.dto.ConcertCreateSuccessEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {

    SEAT_INSTANCE_CREATE(ConcertCreateSuccessEvent.class, Topic.CONCERT_TO_SEAT_TOPIC);

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

    private static class Topic {

        public static final String CONCERT_TO_SEAT_TOPIC = "ConcertToSeat";

    }
}