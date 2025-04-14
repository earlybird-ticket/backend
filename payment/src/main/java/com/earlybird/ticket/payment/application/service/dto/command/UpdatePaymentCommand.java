package com.earlybird.ticket.payment.application.service.dto.command;

import com.earlybird.ticket.payment.domain.entity.Payment;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentMethod;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentStatus;
import lombok.Builder;

@Builder
public record UpdatePaymentCommand(
    PaymentMethod paymentMethod,
    PaymentStatus status,
    String paymentKey
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
