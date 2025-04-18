package com.earlybird.ticket.payment.infrastructure.client.dto.response;

import com.earlybird.ticket.payment.application.service.dto.command.UpdatePaymentCommand;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentMethod;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentStatus;
import lombok.Builder;

@Builder
public record ProcessPaymentConfirmClientResponse(
    String method,
    String status,
    String paymentKey
//    OffsetDateTime approvedAt
) {

    public UpdatePaymentCommand toCommand() {
        return UpdatePaymentCommand.builder()
            .paymentMethod(PaymentMethod.from(method))
            .status(PaymentStatus.from(status))
            .paymentKey(paymentKey)
            .build();
    }
}
