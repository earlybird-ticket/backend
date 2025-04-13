package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.reservation.application.dto.response.ReserveCouponPayload;
import com.earlybird.ticket.reservation.application.event.EventHandler;
import com.earlybird.ticket.reservation.common.exception.CustomJsonProcessingException;
import com.earlybird.ticket.reservation.domain.dto.request.ConfirmCouponPayload;
import com.earlybird.ticket.reservation.domain.dto.request.FailCouponPayload;
import com.earlybird.ticket.reservation.domain.entity.Event;
import com.earlybird.ticket.reservation.domain.entity.Outbox;
import com.earlybird.ticket.reservation.domain.entity.Reservation;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import com.earlybird.ticket.reservation.domain.repository.OutboxRepository;
import com.earlybird.ticket.reservation.domain.repository.ReservationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReserveCouponPayloadHandler implements EventHandler<ReserveCouponPayload> {

    private final ReservationRepository reservationRepository;
    private final OutboxRepository outboxRepository;

    @Override
    @Transactional
    public void handle(Event<ReserveCouponPayload> event) {
        log.info("[CouponEventHandler] 이벤트 수신: {}",
                 event);

        ReserveCouponPayload payload = event.getPayload();
        if (payload == null) {
            log.error("[CouponEventHandler] payload가 null입니다.");
            return;
        }

        log.info("[CouponEventHandler] payload 정보: {}",
                 payload);
        log.info("[CouponEventHandler] couponId: {}",
                 payload.couponId());
        log.info("[CouponEventHandler] couponType: {}",
                 payload.couponType());
        log.info("[CouponEventHandler] reservationList: {}",
                 payload.reservationList());
        log.info("[CouponEventHandler] passport: {}",
                 payload.passport());

        PassportDto passport = payload.passport();
        if (passport == null) {
            log.error("[CouponEventHandler] passport가 null입니다.");
            return;
        }

        log.info("[CouponEventHandler] userId={}, userRole={}",
                 passport.getUserId(),
                 passport.getUserRole());

        ConfirmCouponPayload confirmPayload = ConfirmCouponPayload.builder()
                                                                  .couponId(payload.couponId())
                                                                  .userId(passport.getUserId())
                                                                  .userRole(passport.getUserRole())
                                                                  .build();

        List<Reservation> reservationList = reservationRepository.findAllById(payload.reservationList());
        log.info("[CouponEventHandler] 조회된 Reservation 수: {}",
                 reservationList.size());

        Event<ConfirmCouponPayload> couponConfirmPayloadEvent = new Event<>(EventType.COUPON_SUCCESS,
                                                                            confirmPayload,
                                                                            LocalDateTime.now()
                                                                                         .toString());

        String payloadJson;
        try {
            payloadJson = new ObjectMapper().writeValueAsString(couponConfirmPayloadEvent);
            log.info("[CouponEventHandler] 직렬화된 ConfirmCouponPayload: {}",
                     payloadJson);
        } catch (JsonProcessingException e) {
            log.error("[CouponEventHandler] ConfirmCouponPayload 직렬화 실패",
                      e);
            throw new CustomJsonProcessingException();
        }

        try {
            reservationList.forEach(reservation -> reservation.updateCouponData(payload));

            Outbox outbox = Outbox.builder()
                                  .aggregateType(Outbox.AggregateType.COUPON)
                                  .aggregateId(payload.couponId())
                                  .eventType(EventType.COUPON_CONFIRM)
                                  .payload(payloadJson)
                                  .build();

            outboxRepository.save(outbox);
            log.info("[CouponEventHandler] 쿠폰 적용 성공 및 Outbox 저장 완료");

        } catch (NullPointerException e) {
            log.error("[CouponEventHandler] 쿠폰 적용 중 예외 발생",
                      e);

            FailCouponPayload failCouponPayload = FailCouponPayload.builder()
                                                                   .couponId(payload.couponId())
                                                                   .passportDto(passport)
                                                                   .code("C01")
                                                                   .build();

            Event<FailCouponPayload> failCouponPayloadEvent = new Event<>(EventType.COUPON_FAIL,
                                                                          failCouponPayload,
                                                                          LocalDateTime.now()
                                                                                       .toString());

            try {
                payloadJson = new ObjectMapper().writeValueAsString(failCouponPayloadEvent);
                log.info("[CouponEventHandler] 직렬화된 FailCouponPayload: {}",
                         payloadJson);
            } catch (JsonProcessingException je) {
                log.error("[CouponEventHandler] FailCouponPayload 직렬화 실패",
                          je);
                throw new CustomJsonProcessingException();
            }

            Outbox outbox = Outbox.builder()
                                  .aggregateType(Outbox.AggregateType.COUPON)
                                  .aggregateId(payload.couponId())
                                  .eventType(EventType.COUPON_FAIL)
                                  .payload(payloadJson)
                                  .build();

            outboxRepository.save(outbox);
            log.info("[CouponEventHandler] 쿠폰 실패 Outbox 저장 완료");
        }
    }

    @Override
    public boolean support(Event<ReserveCouponPayload> event) {
        return event.getEventType() == EventType.COUPON_CONFIRM;
    }
}