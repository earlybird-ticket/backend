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
    ISSUE_COUPON(CouponIssueEvent.class, Topic.ADMIN_TO_COUPON_TOPIC),
    CREATE_COUPON(CouponCreateEvent.class, Topic.ADMIN_TO_COUPON_TOPIC),
    DELETE_COUPON(CouponDeleteEvent.class, Topic.ADMIN_TO_COUPON_TOPIC),
    UPDATE_COUPON(CouponUpdateEvent.class, Topic.ADMIN_TO_COUPON_TOPIC),
    SEAT_CREATE(SeatCreateEvent.class, Topic.ADMIN_TO_SEAT_TOPIC),
    SEAT_INSTANCE_UPDATE(SeatInstanceUpdateEvent.class, Topic.ADMIN_TO_SEAT_TOPIC),
    SEAT_INSTANCE_DELETE(SeatInstanceDeleteEvent.class, Topic.ADMIN_TO_SEAT_TOPIC),
    VENUE_CREATE(VenueCreateEvent.class, Topic.ADMIN_TO_VENUE_TOPIC),
    VENUE_UPDATE(VenueUpdateEvent.class, Topic.ADMIN_TO_VENUE_TOPIC),
    VENUE_DELETE(VenueDeleteEvent.class, Topic.ADMIN_TO_VENUE_TOPIC);

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

        public static final String ADMIN_TO_COUPON_TOPIC = "AdminToCoupon";
        public static final String ADMIN_TO_VENUE_TOPIC = "AdminToVenue";
        public static final String ADMIN_TO_SEAT_TOPIC = "AdminToSeat";

    }
}