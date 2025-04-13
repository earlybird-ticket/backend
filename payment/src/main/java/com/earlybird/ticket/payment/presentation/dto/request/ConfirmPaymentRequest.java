package com.earlybird.ticket.payment.presentation.dto.request;

import com.earlybird.ticket.payment.application.service.dto.command.ConfirmPaymentCommand;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ConfirmPaymentRequest(
    BigDecimal amount,
    String paymentKey,
    UUID orderId
) {

    public ConfirmPaymentCommand toConfirmPaymentCommand() {
        return ConfirmPaymentCommand.builder()
            .amount(amount)
            .paymentKey(paymentKey)
            .orderId(orderId)
            .build();
    }
}
