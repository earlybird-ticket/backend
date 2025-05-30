package com.earlybird.ticket.coupon.application.event.handler;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.coupon.application.event.Event;
import com.earlybird.ticket.coupon.application.event.EventType;
import com.earlybird.ticket.coupon.application.event.dto.CouponDeletePayload;
import com.earlybird.ticket.coupon.domain.CouponRepository;
import com.earlybird.ticket.coupon.domain.entity.Coupon;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponDeleteEventHandler implements EventHandler<CouponDeletePayload> {

    private final CouponRepository couponRepository;

    @Transactional
    @Override
    public void handle(Event<CouponDeletePayload> event) {
        CouponDeletePayload payload = event.getPayload();
        PassportDto passportDto = payload.passportDto();
        Coupon coupon = couponRepository.findByCouponId(payload.couponId())
                                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 쿠폰입니다."));
        coupon.deleteCoupon(passportDto.getUserId());
    }

    @Override
    public boolean supports(Event<CouponDeletePayload> event) {
        return EventType.DELETE_COUPON.equals(event.getEventType());
    }
}
