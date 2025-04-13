package com.earlybird.ticket.payment.application.service;

import com.earlybird.ticket.payment.application.service.dto.command.ConfirmPaymentCommand;
import com.earlybird.ticket.payment.application.service.dto.command.CreatePaymentCommand;
import com.earlybird.ticket.payment.application.service.dto.query.FindPaymentQuery;
import com.earlybird.ticket.payment.application.service.exception.PaymentAmountDoesNotMatchException;
import com.earlybird.ticket.payment.application.service.exception.PaymentNotFoundException;
import com.earlybird.ticket.payment.domain.entity.Payment;
import com.earlybird.ticket.payment.domain.repository.PaymentRepository;
import java.math.BigDecimal;
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
    private final PaymentClient paymentClient;

    @Override
    public UUID createPayment(CreatePaymentCommand paymentRequest) {
        log.info("createPayment = {}", paymentRequest);
        Payment save = paymentRepository.save(paymentRequest.toPayment());
        return save.getId();
    }

    @Override
    public FindPaymentQuery findPayment(UUID paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
            .orElseThrow(PaymentNotFoundException::new);
        return FindPaymentQuery.of(payment);
    }

    @Override
    @Transactional
    public void confirmPayment(ConfirmPaymentCommand confirmPaymentCommand) {
        /* TODO:
            1. OrderId로 검색 후 가격 검증 ✅
            2. Base64로 SecretKey 암호화 후 정보 전달 ✅
            3. 엔티티 상태 업데이트[결제 방법, 결제 상태 변경]
            4. 아웃박스 생성
            5. 예약으로 이벤트 발행
         */
        Payment payment = paymentRepository.findByOrderId(confirmPaymentCommand.orderId())
            .orElseThrow(PaymentNotFoundException::new);

        validatePaymentAmount(payment, confirmPaymentCommand.amount());

        paymentClient.confirmPayment(confirmPaymentCommand, payment.getId());

    }

    private void validatePaymentAmount(Payment payment, BigDecimal amount) {
        // equals 검증의 경우 scale까지 비교
        if (payment.getAmount().compareTo(amount) != 0) {
            throw new PaymentAmountDoesNotMatchException();
        }
        log.info("가격 검증 성공");
    }
}
