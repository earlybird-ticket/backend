package com.earlybird.ticket.payment.application.service.dto.command;

import com.earlybird.ticket.payment.domain.entity.Payment;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentMethod;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdatePaymentCommand(
    PaymentMethod paymentMethod,
    PaymentStatus status,
    String paymentKey,
    UUID reservationId,
    LocalDateTime approvedAt
) {

    public Payment toPayment(Long userId) {
        return Payment.builder()
            .userId(userId)
            .method(this.paymentMethod)
            .reservationId(this.reservationId)
            .status(this.status)
            .paymentKey(this.paymentKey)
            .build();

    }
}
