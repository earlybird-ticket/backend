package com.earlybird.ticket.payment.application.service.dto.query;

import com.earlybird.ticket.payment.domain.entity.Payment;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record FindPaymentQuery(
    Long userId,
    String userEmail,
    String userName,
    BigDecimal amount,
    UUID orderId,
    String orderName
) {

    public static FindPaymentQuery of(Payment payment) {
        return FindPaymentQuery.builder()
            .userId(payment.getUserId())
            .userEmail(payment.getUserEmail())
            .userName(payment.getUserName())
            .amount(payment.getAmount())
            .orderId(payment.getOrderId())
            .orderName(payment.getOrderName())
            .build();
    }
}
