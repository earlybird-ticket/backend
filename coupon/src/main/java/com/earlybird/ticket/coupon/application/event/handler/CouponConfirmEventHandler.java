package com.earlybird.ticket.coupon.application.event.handler;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.coupon.application.event.Event;
import com.earlybird.ticket.coupon.application.event.EventType;
import com.earlybird.ticket.coupon.application.event.dto.CouponConfirmPayload;
import com.earlybird.ticket.coupon.domain.CouponRepository;
import com.earlybird.ticket.coupon.domain.entity.Coupon;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponConfirmEventHandler implements EventHandler<CouponConfirmPayload> {

    private final CouponRepository couponRepository;

    @Transactional
    @Override
    public void handle(Event<CouponConfirmPayload> event) {
        CouponConfirmPayload payload = event.getPayload();
        PassportDto passportDto = payload.passportDto();
        Coupon coupon = couponRepository.findByCouponId(payload.couponId())
                                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 쿠폰입니다."));
        coupon.confirmCoupon(passportDto.getUserId());
    }

    @Override
    public boolean supports(Event<CouponConfirmPayload> event) {
        return EventType.COUPON_CONFIRM.equals(event.getEventType());
    }
}
