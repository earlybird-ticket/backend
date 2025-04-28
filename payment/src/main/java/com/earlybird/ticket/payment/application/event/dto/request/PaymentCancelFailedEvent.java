package com.earlybird.ticket.payment.application.event.dto.request;


import com.earlybird.ticket.common.entity.EventPayload;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PaymentCancelFailedEvent(
    UUID paymentId,
    UUID reservationId,
    String paymentKey
) implements EventPayload {

}
