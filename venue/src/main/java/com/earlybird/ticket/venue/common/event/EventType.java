package com.earlybird.ticket.venue.common.event;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.venue.application.event.dto.request.*;
import com.earlybird.ticket.venue.application.event.dto.response.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    //consume
    VENUE_CREATE(VenueCreatePayload.class, Topic.ADMIN_TO_VENUE_TOPIC),
    VENUE_UPDATE(VenueUpdatePayload.class, Topic.ADMIN_TO_VENUE_TOPIC),
    VENUE_DELETE(VenueDeletePayload.class, Topic.ADMIN_TO_VENUE_TOPIC),

    SEAT_CREATE(SeatCreatePayload.class, Topic.ADMIN_TO_SEAT_TOPIC),
    SEAT_INSTANCE_UPDATE(SeatInstanceUpdatePayload.class, Topic.ADMIN_TO_SEAT_TOPIC),
    SEAT_INSTANCE_DELETE(SeatInstanceDeletePayload.class, Topic.ADMIN_TO_SEAT_TOPIC),

    SEAT_INSTANCE_CREATE(SeatInstanceCreatePayload.class, Topic.CONCERT_TO_SEAT_TOPIC),

    SEAT_PREEMPT(SeatPreemptPayload.class, Topic.RESERVATION_TO_SEAT_FOR_PREEMPT_TOPIC),
    SEAT_CONFIRM(SeatConfirmPayload.class, Topic.RESERVATION_TO_SEAT_TOPIC),
    SEAT_RETURN(SeatReturnPayload.class, Topic.RESERVATION_TO_SEAT_TOPIC),

    //produce
    SEAT_PREEMPT_SUCCESS(SeatPreemptSuccessEvent.class, Topic.SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC),
    SEAT_RETURN_SUCCESS(SeatReturnSuccessEvent.class, Topic.SEAT_TO_RESERVATION_TOPIC),
    SEAT_CONFIRM_SUCCESS(SeatConfirmSuccessEvent.class, Topic.SEAT_TO_RESERVATION_TOPIC),

    SEAT_PREEMPT_FAIL(SeatPreemptFailEvent.class, Topic.SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC),
    SEAT_RETURN_FAIL(SeatReturnFailEvent.class, Topic.SEAT_TO_RESERVATION_TOPIC),
    SEAT_CONFIRM_FAIL(SeatConfirmFailEvent.class, Topic.SEAT_TO_RESERVATION_TOPIC)
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
        public static final String ADMIN_TO_VENUE_TOPIC = "AdminToVenue";
        public static final String ADMIN_TO_SEAT_TOPIC = "AdminToSeat";
        public static final String CONCERT_TO_SEAT_TOPIC = "ConcertToSeat";
        public static final String SEAT_TO_RESERVATION_FOR_PREEMPT_TOPIC = "SeatToReservationForPreemption";
        public static final String SEAT_TO_RESERVATION_TOPIC = "SeatToReservation";
        public static final String RESERVATION_TO_SEAT_FOR_PREEMPT_TOPIC = "ReservationToSeatForPreemption";
        public static final String RESERVATION_TO_SEAT_TOPIC = "ReservationToSeat";
    }
}
