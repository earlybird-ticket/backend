package com.earlybird.ticket.payment.application.service.dto.command;

import com.earlybird.ticket.payment.domain.entity.Payment;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentMethod;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UpdatePaymentCommand(
    PaymentMethod paymentMethod,
    PaymentStatus status,
    String paymentKey,
    LocalDateTime approvedAt
) {

    public Payment toPayment(Long userId) {
        return Payment.builder()
            .userId(userId)
            .method(this.paymentMethod)
            .status(this.status)
            .paymentKey(this.paymentKey)
            .build();

    }
}
