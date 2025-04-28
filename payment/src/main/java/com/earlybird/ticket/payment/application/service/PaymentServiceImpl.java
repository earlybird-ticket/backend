package com.earlybird.ticket.payment.application.service;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.entity.constant.Role;
import com.earlybird.ticket.common.util.CommonUtil;
import com.earlybird.ticket.payment.application.TemporaryStore;
import com.earlybird.ticket.payment.application.event.dto.request.PaymentCancelFailedEvent;
import com.earlybird.ticket.payment.application.event.dto.request.PaymentFailEvent;
import com.earlybird.ticket.payment.application.event.dto.request.PaymentSuccessEvent;
import com.earlybird.ticket.payment.application.service.dto.command.ConfirmPaymentCommand;
import com.earlybird.ticket.payment.application.service.dto.command.CreatePaymentCommand;
import com.earlybird.ticket.payment.application.service.dto.command.UpdatePaymentCommand;
import com.earlybird.ticket.payment.application.service.dto.query.FindPaymentQuery;
import com.earlybird.ticket.payment.application.service.exception.PaymentAmountDoesNotMatchException;
import com.earlybird.ticket.payment.application.service.exception.PaymentCancelException;
import com.earlybird.ticket.payment.application.service.exception.PaymentDuplicatedException;
import com.earlybird.ticket.payment.application.service.exception.PaymentInformationDoesNotMatchException;
import com.earlybird.ticket.payment.application.service.exception.PaymentNotFoundException;
import com.earlybird.ticket.payment.application.service.exception.PaymentTimeoutException;
import com.earlybird.ticket.payment.common.EventConverter;
import com.earlybird.ticket.payment.common.EventType;
import com.earlybird.ticket.payment.domain.entity.Event;
import com.earlybird.ticket.payment.domain.entity.Outbox;
import com.earlybird.ticket.payment.domain.entity.Payment;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentStatus;
import com.earlybird.ticket.payment.domain.repository.OutboxRepository;
import com.earlybird.ticket.payment.domain.repository.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Primary
@Profile("!test")
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
        /*
            TODO:
                1. 결제 가능 시간 가져옴 ✅
                2. 결제 진행 ✅
                3. 결제 성공 시간 받아옴 ✅
                4. 결제 제한 시간과 성공 시간 비교
                4-1. 결제 제한 내에 성공 -> SUCCESS 이벤트 발행 ✅
                4-2. 결제 제한 내 이후에 성공
                    a. Reservation으로 FAIL 이벤트 발행 ✅
                    b. Payment로 CANCEL 이벤트 발행 -> paymentCancel 실행
         */
        validateReservationTimedOut(confirmPaymentCommand.reservationId());
        validateDuplicatePayment(confirmPaymentCommand.reservationId());
        Payment payment = paymentRepository.findByReservationId(
                confirmPaymentCommand.reservationId())
            .orElseThrow(PaymentNotFoundException::new);

        validatePaymentAmount(payment, confirmPaymentCommand.amount());

        // 임시 passport 생성
        PassportDto passport = PassportDto.builder()
            .userId(payment.getUserId())
            .userRole(Role.USER.getValue())
            .build();

        // 결제 시간 제한
        LocalDateTime dueDate = temporaryStore.getExpireDate(payment.getReservationId());
        log.info("결제 제한 시간 : {}", dueDate);

        // 결제 내역 -> 업데이트용 엔티티 변경
        UpdatePaymentCommand updatePaymentCommand = paymentClient.confirmPayment(
            confirmPaymentCommand, payment.getId()
        );
        log.info("결과 -> {}", updatePaymentCommand);
        Payment receipt = updatePaymentCommand.toPayment(payment.getUserId());

        if (updatePaymentCommand.approvedAt().isAfter(dueDate)) {
            // 결제 실패 이벤트 발행해야함.
//            payment.expirePayment(receipt);
            // Reservation으로 보낼 FAIL 이벤트
            PaymentFailEvent fail = PaymentFailEvent.builder()
                .reservationId(payment.getReservationId())
                .passportDto(passport)
                .paymentMethod(receipt.getMethod())
                .paymentStatus(PaymentStatus.EXPIRED)
                .paymentId(payment.getId())
                .build();

            // Payment로 보낼 CANCEL 이벤트
            Outbox outbox = createOutbox(payment, EventType.PAYMENT_FAIL, fail);
            // TODO : 결제 쪽으로 보낼 취소 이벤트도 추가
            // TODO : 취소 성공 이후 EXPIRED로 바꾸기
            outboxRepository.save(outbox);
            return;
        }

        // 결제 방법, 상태 반영
        payment.confirmPayment(receipt);

        temporaryStore.cacheConfirmedPayment(payment.getReservationId());

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
    @Transactional
    public void cancelPayment(UUID paymentId, PassportDto passportDto) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
            .orElseThrow(PaymentNotFoundException::new);

        validateIsUserMatches(passportDto, payment);

        Payment cancelPayment = null;
        try {

            cancelPayment = paymentClient.cancelPayment(
                    payment.getPaymentKey(), payment.getReservationId())
                .toCancelPayment();

        } catch (PaymentCancelException e) {
            // 결제 취소 3회 실패 시 DLQ로 이동
            PaymentCancelFailedEvent paymentCancelFailedEvent = PaymentCancelFailedEvent.builder()
                .paymentKey(payment.getPaymentKey())
                .paymentId(payment.getId())
                .reservationId(payment.getReservationId())
                .build();

            Outbox outbox = createOutbox(payment, EventType.PAYMENT_CANCEL_FAIL,
                paymentCancelFailedEvent);

            outboxRepository.save(outbox);
            return;
        }

        payment.cancelPayment(cancelPayment);
    }

    private static void validateIsUserMatches(PassportDto passportDto, Payment payment) {
        // 사용자 정보 검증
        if (!payment.getUserId().equals(passportDto.getUserId())) {
            throw new PaymentInformationDoesNotMatchException();
        }
    }

    private void validatePaymentAmount(Payment payment, BigDecimal amount) {
        // equals 검증의 경우 scale까지 비교
        if (payment.getAmount().compareTo(amount) != 0) {
            throw new PaymentAmountDoesNotMatchException();
        }
        log.info("가격 검증 성공");
    }

    private void validateReservationTimedOut(UUID reservationId) {
        if (temporaryStore.isTimedOut(reservationId)) {
            throw new PaymentTimeoutException();
        }
    }

    private void validateDuplicatePayment(UUID reservationId) {
        // 결제 내역 검증
        if (temporaryStore.isAlreadyProcessed(reservationId)) {
            throw new PaymentDuplicatedException(reservationId);
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
