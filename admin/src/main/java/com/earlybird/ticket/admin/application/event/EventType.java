package com.earlybird.ticket.admin.application.event;

import com.earlybird.ticket.admin.application.event.dto.CouponCreateEvent;
import com.earlybird.ticket.admin.application.event.dto.CouponDeleteEvent;
import com.earlybird.ticket.admin.application.event.dto.CouponIssueEvent;
import com.earlybird.ticket.admin.application.event.dto.CouponUpdateEvent;
import com.earlybird.ticket.admin.application.event.dto.SeatCreateEvent;
import com.earlybird.ticket.admin.application.event.dto.SeatInstanceDeleteEvent;
import com.earlybird.ticket.admin.application.event.dto.SeatInstanceUpdateEvent;
import com.earlybird.ticket.admin.application.event.dto.VenueCreateEvent;
import com.earlybird.ticket.admin.application.event.dto.VenueDeleteEvent;
import com.earlybird.ticket.admin.application.event.dto.VenueUpdateEvent;
import com.earlybird.ticket.common.entity.EventPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    ISSUE_COUPON(CouponIssueEvent.class, Topic.ADMIN_TO_COUPON),
    CREATE_COUPON(CouponCreateEvent.class, Topic.ADMIN_TO_COUPON),
    DELETE_COUPON(CouponDeleteEvent.class, Topic.ADMIN_TO_COUPON),
    UPDATE_COUPON(CouponUpdateEvent.class, Topic.ADMIN_TO_COUPON),
    CREATE_SEAT(SeatCreateEvent.class, Topic.ADMIN_TO_SEAT),
    UPDATE_SEAT_INSTANCE(SeatInstanceUpdateEvent.class, Topic.ADMIN_TO_SEAT),
    DELETE_SEAT_INSTANCE(SeatInstanceDeleteEvent.class, Topic.ADMIN_TO_SEAT),
    CREATE_VENUE(VenueCreateEvent.class, Topic.ADMIN_TO_VENUE),
    UPDATE_VENUE(VenueUpdateEvent.class, Topic.ADMIN_TO_VENUE),
    DELETE_VENUE(VenueDeleteEvent.class, Topic.ADMIN_TO_VENUE);

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

        public static final String ADMIN_TO_COUPON = "AdminToCoupon";
        public static final String ADMIN_TO_VENUE = "AdminToVenue";
        public static final String ADMIN_TO_SEAT = "AdminToSeat";

    }
}