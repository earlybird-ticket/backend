package com.earlybird.ticket.payment.application.service.dto.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ConfirmPaymentCommand(
    BigDecimal amount,
    String paymentKey,
    @JsonProperty("orderId") UUID reservationId
) {

}

