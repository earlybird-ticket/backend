package com.earlybird.ticket.payment.presentation.dto.request;

import com.earlybird.ticket.payment.application.service.dto.command.CreatePaymentCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreatePaymentRequest(
    @NotNull(message = "user id cannot be null") Long userId,
    String userEmail,
    @NotEmpty(message = "user name cannot be empty") String userName,
    @NotNull(message = "total amount cannot be null") BigDecimal amount,
    @NotNull(message = "reservation id cannot be null") UUID reservationId,
    @NotEmpty(message = "product info cannot be empty") String orderName
) {

    public CreatePaymentCommand toCreatePaymentCommand() {
        return CreatePaymentCommand.builder()
            .userId(userId)
            .userEmail(userEmail)
            .userName(userName)
            .amount(amount)
            .reservationId(reservationId)
            .orderName(orderName)
            .build();
    }
}
