package com.earlybird.ticket.coupon.application.event.handler;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.coupon.application.event.Event;
import com.earlybird.ticket.coupon.application.event.EventType;
import com.earlybird.ticket.coupon.application.event.dto.CouponCreatePayload;
import com.earlybird.ticket.coupon.domain.CouponRepository;
import com.earlybird.ticket.coupon.domain.entity.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponCreateEventHandler implements EventHandler<CouponCreatePayload> {

    private final CouponRepository couponRepository;

    @Transactional
    @Override
    public void handle(Event<CouponCreatePayload> event) {
        CouponCreatePayload payload = event.getPayload();
        PassportDto passportDto = payload.passport();
        Coupon coupon = couponRepository.save(event.getPayload());
        coupon.createCoupon(passportDto.getUserId());
    }

    @Override
    public boolean supports(Event<CouponCreatePayload> event) {
        return EventType.CREATE_COUPON.equals(event.getEventType());
    }
}
