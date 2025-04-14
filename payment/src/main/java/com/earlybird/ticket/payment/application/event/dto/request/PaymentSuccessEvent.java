package com.earlybird.ticket.payment.application.event.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentMethod;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PaymentSuccessEvent(
    UUID paymentId,
    UUID reservationId,
    PaymentMethod paymentMethod,
    PaymentStatus paymentStatus
)
    implements EventPayload {

}
