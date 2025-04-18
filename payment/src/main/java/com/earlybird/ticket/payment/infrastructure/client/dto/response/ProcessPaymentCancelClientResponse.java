package com.earlybird.ticket.payment.infrastructure.client.dto.response;

import com.earlybird.ticket.payment.application.service.dto.command.UpdatePaymentCancelCommand;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProcessPaymentCancelClientResponse(
    PaymentStatus status,
    @JsonProperty("orderId") UUID reservationId
) {

    public UpdatePaymentCancelCommand toUpdatePaymentCancelCommand() {
        return UpdatePaymentCancelCommand.builder()
            .status(status)
            .reservationId(reservationId)
            .build();
    }
}
