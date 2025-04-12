package com.earlybird.ticket.admin.application.event;

import com.earlybird.ticket.admin.application.event.dto.CouponCreatePayload;
import com.earlybird.ticket.admin.application.event.dto.CouponDeletePayload;
import com.earlybird.ticket.admin.application.event.dto.CouponIssuePayload;
import com.earlybird.ticket.admin.application.event.dto.CouponUpdatePayload;
import com.earlybird.ticket.admin.application.event.dto.SeatCreatePayload;
import com.earlybird.ticket.admin.application.event.dto.SeatInstanceDeletePayload;
import com.earlybird.ticket.admin.application.event.dto.SeatInstanceUpdatePayload;
import com.earlybird.ticket.admin.application.event.dto.VenueCreatePayload;
import com.earlybird.ticket.admin.application.event.dto.VenueDeletePayload;
import com.earlybird.ticket.admin.application.event.dto.VenueUpdatePayload;
import com.earlybird.ticket.common.entity.EventPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    ISSUE_COUPON(CouponIssuePayload.class, Topic.ADMIN_TO_COUPON),
    CREATE_COUPON(CouponCreatePayload.class, Topic.ADMIN_TO_COUPON),
    DELETE_COUPON(CouponDeletePayload.class, Topic.ADMIN_TO_COUPON),
    UPDATE_COUPON(CouponUpdatePayload.class, Topic.ADMIN_TO_COUPON),
    CREATE_SEAT(SeatCreatePayload.class, Topic.ADMIN_TO_SEAT),
    UPDATE_SEAT(SeatInstanceUpdatePayload.class, Topic.ADMIN_TO_SEAT),
    DELETE_SEAT(SeatInstanceDeletePayload.class, Topic.ADMIN_TO_SEAT),
    CREATE_VENUE(VenueCreatePayload.class, Topic.ADMIN_TO_VENUE),
    UPDATE_VENUE(VenueUpdatePayload.class, Topic.ADMIN_TO_VENUE),
    DELETE_VENUE(VenueDeletePayload.class, Topic.ADMIN_TO_VENUE);

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