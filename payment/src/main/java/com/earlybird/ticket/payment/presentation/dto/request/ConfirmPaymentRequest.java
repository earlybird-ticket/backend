package com.earlybird.ticket.payment.presentation.dto.request;

import com.earlybird.ticket.payment.application.service.dto.command.ConfirmPaymentCommand;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ConfirmPaymentRequest(
    BigDecimal amount,
    String paymentKey,
    @JsonGetter("order_id") UUID reservationId
) {

    public ConfirmPaymentCommand toConfirmPaymentCommand() {
        return ConfirmPaymentCommand.builder()
            .amount(amount)
            .paymentKey(paymentKey)
            .reservationId(reservationId)
            .build();
    }
}
