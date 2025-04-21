package com.earlybird.ticket.payment.application.event.handler;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.payment.application.event.dto.response.ReservationCancelPayload;
import com.earlybird.ticket.payment.application.service.PaymentService;
import com.earlybird.ticket.payment.common.EventType;
import com.earlybird.ticket.payment.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCancelPayloadHandler implements EventHandler<ReservationCancelPayload> {

    private final PaymentService paymentService;

    @Override
    public void handle(Event<ReservationCancelPayload> event) {
        ReservationCancelPayload payload = event.getPayload();
        log.info("예약 취소 이벤트 수신 -> {}", payload);

        PassportDto passportDto = payload.passportDto();

        paymentService.cancelPayment(payload.paymentId(), passportDto);
    }

    @Override
    public boolean supports(Event<ReservationCancelPayload> event) {
        return event.getEventType() == EventType.RESERVATION_CANCEL;
    }
}
