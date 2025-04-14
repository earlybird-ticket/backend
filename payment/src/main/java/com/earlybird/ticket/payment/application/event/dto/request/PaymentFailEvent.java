package com.earlybird.ticket.payment.application.event.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import java.util.UUID;

public record PaymentFailEvent(
    UUID paymentId
) implements EventPayload {

}
