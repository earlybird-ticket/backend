package com.earlybird.ticket.venue.common.event;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.venue.application.event.dto.request.VenueCreatePayload;
import com.earlybird.ticket.venue.application.event.dto.response.SeatConfirmSuccessEvent;
import com.earlybird.ticket.venue.application.event.dto.response.SeatPreemptSuccessEvent;
import com.earlybird.ticket.venue.application.event.dto.response.SeatReturnSuccessEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    VENUE_CREATE(VenueCreatePayload.class, Topic.VENUE_TOPIC),
    SEAT_PREEMPT_SUCCESS(SeatPreemptSuccessEvent.class, Topic.SEAT_PREEMPT_TOPIC),
    SEAT_RETURN_SUCCESS(SeatReturnSuccessEvent.class, Topic.SEAT_RESERVE_TOPIC),
    SEAT_CONFIRM_SUCCESS(SeatConfirmSuccessEvent.class, Topic.SEAT_RESERVE_TOPIC)
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
        public static final String VENUE_TOPIC = "AdminToVenue";
        public static final String SEAT_PREEMPT_TOPIC = "seatToReservationForPreemption";
        public static final String SEAT_RESERVE_TOPIC = "seatToReservation";
    }
}
