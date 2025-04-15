package com.earlybird.ticket.payment.application.service;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.entity.constant.Role;
import com.earlybird.ticket.common.util.CommonUtil;
import com.earlybird.ticket.payment.application.TemporaryStore;
import com.earlybird.ticket.payment.application.event.dto.request.PaymentSuccessEvent;
import com.earlybird.ticket.payment.application.service.dto.command.ConfirmPaymentCommand;
import com.earlybird.ticket.payment.application.service.dto.command.CreatePaymentCommand;
import com.earlybird.ticket.payment.application.service.dto.query.FindPaymentQuery;
import com.earlybird.ticket.payment.application.service.exception.PaymentAmountDoesNotMatchException;
import com.earlybird.ticket.payment.application.service.exception.PaymentNotFoundException;
import com.earlybird.ticket.payment.application.service.exception.PaymentTimeoutException;
import com.earlybird.ticket.payment.common.EventConverter;
import com.earlybird.ticket.payment.common.EventType;
import com.earlybird.ticket.payment.domain.entity.Event;
import com.earlybird.ticket.payment.domain.entity.Outbox;
import com.earlybird.ticket.payment.domain.entity.Payment;
import com.earlybird.ticket.payment.domain.repository.OutboxRepository;
import com.earlybird.ticket.payment.domain.repository.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final PaymentClient paymentClient;
    private final EventConverter eventConverter;
    private final TemporaryStore temporaryStore;

    @Override
    public UUID createPayment(CreatePaymentCommand paymentRequest) {
        validateReservationTimedOut(paymentRequest.reservationId());

        log.info("createPayment = {}", paymentRequest);
        Payment save = paymentRepository.save(paymentRequest.toPayment());
        return save.getId();
    }

    @Override
    public FindPaymentQuery findPayment(UUID paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
            .orElseThrow(PaymentNotFoundException::new);
        validateReservationTimedOut(payment.getReservationId());
        return FindPaymentQuery.of(payment);
    }

    @Override
    @Transactional
    public void confirmPayment(ConfirmPaymentCommand confirmPaymentCommand) {
        /* TODO:
            0. PAYMENT:CONFIRM:{RESERVATION_ID}로 결제한 적 있는지 확인
            1. reservationId로 검색 후 가격 검증 ✅
            2. Base64로 SecretKey 암호화 후 정보 전달 ✅
            3. 엔티티 상태 업데이트[결제 방법, 결제 상태 변경] ✅
            4. 결제 성공 후 PAYMENT:CONFIRM:{RESERVATION_ID}로 레디스 캐싱
            5. 아웃박스 생성 ✅
            6. outbox에서 이벤트 발행[결제 성공 이벤트 발행] ✅
         */
        validateReservationTimedOut(confirmPaymentCommand.reservationId());

        Payment payment = paymentRepository.findByReservationId(
                confirmPaymentCommand.reservationId())
            .orElseThrow(PaymentNotFoundException::new);

        validatePaymentAmount(payment, confirmPaymentCommand.amount());

        // 결제 내역 -> 업데이트용 엔티티 변경
        Payment receipt = paymentClient.confirmPayment(
            confirmPaymentCommand, payment.getId()
        ).toPayment(payment.getUserId());
        // 결제 방법, 상태 반영
        payment.confirmPayment(receipt);

        // 임시 passport 생성
        PassportDto passport = PassportDto.builder()
            .userId(payment.getUserId())
            .userRole(Role.USER.getValue())
            .build();

        // 이벤트 생성
        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
            .reservationId(payment.getReservationId())
            .passportDto(passport)
            .totalPrice(payment.getAmount())
            .paymentMethod(payment.getMethod())
            .paymentId(payment.getId())
            .paymentStatus(payment.getStatus())
            .build();

        // 아웃박스 저장
        Outbox outbox = createOutbox(payment, EventType.PAYMENT_SUCCESS, event);
        outboxRepository.save(outbox);


    }

    @Override
    public void cancelPayment(UUID paymentId) {
        // TODO : 결제 취소 요청
        // paymentClient.cancelPayment();
    }

    private void validatePaymentAmount(Payment payment, BigDecimal amount) {
        // equals 검증의 경우 scale까지 비교
        if (payment.getAmount().compareTo(amount) != 0) {
            throw new PaymentAmountDoesNotMatchException();
        }
        log.info("가격 검증 성공");
    }

    private void validateReservationTimedOut(UUID payment) {
        if (temporaryStore.isTimedOut(payment)) {
            throw new PaymentTimeoutException();
        }
    }

    // SUCCESS, FAIL 당장은 두 개만 필요함
    protected <T extends EventPayload> Outbox createOutbox(
        Payment payment,
        EventType eventType,
        T eventPayload
    ) {
        Event<T> event = Event.<T>builder()
            .eventType(eventType)
            .payload(eventPayload)
            .timestamp(CommonUtil.LocalDateTimetoString(LocalDateTime.now()))
            .build();

        return Outbox.builder()
            .aggregateId(payment.getId())
            .aggregateType(Outbox.AggregateType.PAYMENT)
            .eventType(eventType)
            .payload(eventConverter.serializeEvent(event))
            .build();
    }

}
