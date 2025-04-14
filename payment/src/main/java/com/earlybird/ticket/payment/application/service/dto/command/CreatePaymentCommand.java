package com.earlybird.ticket.payment.application.service.dto.command;

import com.earlybird.ticket.payment.domain.entity.Payment;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentStatus;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreatePaymentCommand(
    Long userId,
    String userEmail,
    String userName,
    BigDecimal amount,
    UUID reservationId,
    String orderName
) {

    public Payment toPayment() {
        return Payment.builder()
            .userId(userId)
            .userEmail(userEmail)
            .userName(userName)
            .amount(amount)
            .reservationId(reservationId)
            .orderName(orderName)
            .status(PaymentStatus.CREATED)
            .build();
    }
}
