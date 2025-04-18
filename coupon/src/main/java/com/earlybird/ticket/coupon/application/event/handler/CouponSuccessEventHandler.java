package com.earlybird.ticket.coupon.application.event.handler;

import com.earlybird.ticket.coupon.application.event.Event;
import com.earlybird.ticket.coupon.application.event.EventType;
import com.earlybird.ticket.coupon.application.event.dto.CouponReservePayload;
import com.earlybird.ticket.coupon.domain.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponSuccessEventHandler implements EventHandler<CouponReservePayload> {

    private final CouponRepository couponRepository;

    @Override
    public void handle(Event<CouponReservePayload> event) {

    }

    @Override
    public boolean supports(Event<CouponReservePayload> event) {
        return EventType.COUPON_SUCCESS.equals(event.getEventType());
    }
}
