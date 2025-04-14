package com.earlybird.ticket.payment.presentation.dto.request;

import com.earlybird.ticket.payment.application.service.dto.query.FindPaymentQuery;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record FindPaymentResponse(
    String userId,
    String userEmail,
    String userName,
    BigDecimal amount,
    UUID reservationId,
    String orderName
) {

    public static FindPaymentResponse of(FindPaymentQuery payment) {
        return FindPaymentResponse.builder()
            .userId("EBT-" + payment.userId())
            .userEmail(payment.userEmail())
            .userName(payment.userName())
            .amount(payment.amount())
            .reservationId(payment.reservationId())
            .orderName(payment.orderName())
            .build();
    }
}
