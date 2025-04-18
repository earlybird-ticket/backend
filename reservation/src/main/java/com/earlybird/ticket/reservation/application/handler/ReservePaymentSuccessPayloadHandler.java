package com.earlybird.ticket.reservation.application.handler;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.reservation.application.dto.response.PaymentSuccessPayload;
import com.earlybird.ticket.reservation.application.event.EventHandler;
import com.earlybird.ticket.reservation.common.exception.NotFoundReservationException;
import com.earlybird.ticket.reservation.common.util.EventPayloadConverter;
import com.earlybird.ticket.reservation.domain.dto.request.ConfirmCouponEvent;
import com.earlybird.ticket.reservation.domain.dto.request.ConfirmSeatEvent;
import com.earlybird.ticket.reservation.domain.entity.Event;
import com.earlybird.ticket.reservation.domain.entity.Outbox;
import com.earlybird.ticket.reservation.domain.entity.Reservation;
import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.earlybird.ticket.reservation.domain.entity.constant.EventType;
import com.earlybird.ticket.reservation.domain.repository.OutboxRepository;
import com.earlybird.ticket.reservation.domain.repository.ReservationRepository;
import com.earlybird.ticket.reservation.domain.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Component
@Slf4j
public class ReservePaymentSuccessPayloadHandler implements EventHandler<PaymentSuccessPayload> {
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final EventPayloadConverter eventPayloadConverter;
    private final OutboxRepository outboxRepository;

    @Override
    @Transactional
    public void handle(Event<PaymentSuccessPayload> event) {
        PaymentSuccessPayload payload = event.getPayload();
        UUID reservationId = payload.reservationId();
        PassportDto passportDto = payload.passportDto();


        Reservation reservation = reservationRepository.findById(reservationId)
                                                       .orElseThrow(NotFoundReservationException::new);
        List<ReservationSeat> reservationSeatList = reservationSeatRepository.findByReservation(reservation);
        //결제정보 업데이트 , 예약 확정상태
        reservation.updatePaymentInfo(payload);
        reservationSeatList.forEach(ReservationSeat::updateStatusConfirmSuccess);

        //좌석 확정 상태로 변경
        ConfirmSeatEvent confirmSeatEvent = ConfirmSeatEvent.builder()
                                                            .seatInstanceIdList(reservationSeatList.stream()
                                                                                                   .map(ReservationSeat::getSeatInstanceId)
                                                                                                   .toList())
                                                            .passportDto(passportDto)
                                                            .reservationId(reservationId)
                                                            .build();

        Event<ConfirmSeatEvent> confirmSeatPayloadEvent = new Event<>(EventType.SEAT_CONFIRM,
                                                                      confirmSeatEvent,
                                                                      LocalDateTime.now()
                                                                                   .toString());

        String convertedConfirmSeatPayload = eventPayloadConverter.serializePayload(confirmSeatPayloadEvent);

        Outbox seatOutbox = Outbox.builder()
                                  .aggregateId(reservation.getId())
                                  .aggregateType(Outbox.AggregateType.RESERVATION)
                                  .eventType(EventType.SEAT_CONFIRM)
                                  .payload(convertedConfirmSeatPayload)
                                  .build();

        outboxRepository.save(seatOutbox);


        //쿠폰 확정 상태로 변경
        if (reservation.getCouponId() == null) {
            return;
        }
        ConfirmCouponEvent confirmCouponEvent = ConfirmCouponEvent.createSeatPreemptPayload(reservation.getCouponId(),
                                                                                            passportDto);

        Event<ConfirmCouponEvent> confirmCouponPayloadEvent = new Event<>(EventType.COUPON_CONFIRM,
                                                                          confirmCouponEvent,
                                                                          LocalDateTime.now()
                                                                                       .toString());

        String convertedConfirmCouponPayload = eventPayloadConverter.serializePayload(confirmCouponPayloadEvent);

        Outbox couponOutbox = Outbox.builder()
                                    .aggregateId(reservation.getId())
                                    .aggregateType(Outbox.AggregateType.RESERVATION)
                                    .eventType(EventType.COUPON_CONFIRM)
                                    .payload(convertedConfirmCouponPayload)
                                    .build();

        outboxRepository.save(couponOutbox);
    }


    @Override
    public boolean support(Event<PaymentSuccessPayload> event) {
        return event.getEventType() == EventType.PAYMENT_SUCCESS;
    }
}
