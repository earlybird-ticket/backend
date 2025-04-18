package com.earlybird.ticket.coupon.application.service;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.util.PassportUtil;
import com.earlybird.ticket.coupon.application.dto.ProcessCouponQuery;
import com.earlybird.ticket.coupon.application.dto.ProcessCouponReserveCommand;
import com.earlybird.ticket.coupon.application.dto.ProcessUserCouponQuery;
import com.earlybird.ticket.coupon.application.event.Event;
import com.earlybird.ticket.coupon.application.event.EventType;
import com.earlybird.ticket.coupon.application.event.dto.CouponReserveEvent;
import com.earlybird.ticket.coupon.common.Outbox;
import com.earlybird.ticket.coupon.common.Outbox.AggregateType;
import com.earlybird.ticket.coupon.common.OutboxRepository;
import com.earlybird.ticket.coupon.domain.CouponRepository;
import com.earlybird.ticket.coupon.domain.dto.CouponResult;
import com.earlybird.ticket.coupon.domain.dto.UserCouponResults;
import com.earlybird.ticket.coupon.domain.entity.Coupon;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final PassportUtil passportUtil;
    private final OutboxRepository outboxRepository;

    @Override
    public List<ProcessCouponQuery> getCouponList(String passport) {
        List<CouponResult> coupon = couponRepository.findAllCoupon();

        return coupon.stream()
                .map(couponResult -> new ProcessCouponQuery(
                        couponResult.couponId(), couponResult.couponName(),
                        couponResult.couponType(), couponResult.discountRate()
                ))
                .toList();
    }

    @Override
    public ProcessUserCouponQuery getUserCouponList(String passport) {

        PassportDto passportDto = passportUtil.getPassportDto(passport);
        Long userId = passportDto.getUserId();
        UserCouponResults userCouponResults = couponRepository.findAllUserCouponByUserId(userId);
        return ProcessUserCouponQuery.of(userCouponResults.userCoupons());
    }

    @Transactional
    @Override
    public void reserveCoupon(String passport, ProcessCouponReserveCommand request) {
        PassportDto passportDto = passportUtil.getPassportDto(passport);
        Long userId = passportDto.getUserId();

        Coupon coupon = couponRepository.findByCouponId(request.couponId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        coupon.reserveCoupon(userId);

        Outbox outbox = Outbox.builder()
                .aggregateId(coupon.getCouponId())
                .aggregateType(AggregateType.RESERVATION)
                .eventType(EventType.COUPON_RESERVE)
                .payload(Event.of(
                        EventType.COUPON_RESERVE,
                        CouponReserveEvent.toPayload(passportDto, coupon.getCouponId()),
                        LocalDateTime.now().toString()
                ).toJson())
                .build();

        outboxRepository.save(outbox);
    }
}
