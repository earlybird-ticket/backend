package com.earlybird.ticket.coupon.application.event.handler;

import com.earlybird.ticket.coupon.application.event.Event;
import com.earlybird.ticket.coupon.application.event.EventType;
import com.earlybird.ticket.coupon.application.event.dto.ReturnCouponPayload;
import com.earlybird.ticket.coupon.domain.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CouponFailEventHandler implements EventHandler<ReturnCouponPayload> {

    private final CouponRepository couponRepository;

    @Override
    public void handle(Event<ReturnCouponPayload> event) {

    }

    @Override
    public boolean supports(Event<ReturnCouponPayload> event) {
        return EventType.COUPON_FAIL.equals(event.getEventType());
    }
}
