package com.earlybird.ticket.coupon.application.event.handler;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.coupon.application.event.Event;
import com.earlybird.ticket.coupon.application.event.EventType;
import com.earlybird.ticket.coupon.application.event.dto.CouponUpdatePayload;
import com.earlybird.ticket.coupon.domain.CouponRepository;
import com.earlybird.ticket.coupon.domain.entity.Coupon;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponUpdateEventHandler implements EventHandler<CouponUpdatePayload> {

    private final CouponRepository couponRepository;

    @Transactional
    @Override
    public void handle(Event<CouponUpdatePayload> event) {
        CouponUpdatePayload payload = event.getPayload();
        PassportDto passportDto = payload.passport();

        Coupon coupon = couponRepository.findByCouponId(payload.couponId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 쿠폰입니다."));

        coupon.updateCoupon(
                passportDto.getUserId(), payload.couponName(), payload.discountRate(),
                payload.couponType()
        );
    }

    @Override
    public boolean supports(Event<CouponUpdatePayload> event) {
        return EventType.UPDATE_COUPON.equals(event.getEventType());
    }
}
