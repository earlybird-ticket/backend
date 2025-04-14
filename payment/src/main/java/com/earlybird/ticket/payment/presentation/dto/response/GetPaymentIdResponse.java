package com.earlybird.ticket.payment.presentation.dto.response;

import java.util.UUID;
import lombok.Builder;

@Builder
public record GetPaymentIdResponse(
    UUID paymentId
) {

}
