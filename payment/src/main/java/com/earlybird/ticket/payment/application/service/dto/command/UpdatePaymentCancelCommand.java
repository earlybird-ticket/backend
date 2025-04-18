package com.earlybird.ticket.payment.application.service.dto.command;

import com.earlybird.ticket.payment.domain.entity.Payment;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdatePaymentCancelCommand(
    PaymentStatus status,
    UUID reservationId
) {

    public Payment toCancelPayment() {
        return Payment.builder()
            .status(status)
            .reservationId(reservationId)
            .build();
    }
}
