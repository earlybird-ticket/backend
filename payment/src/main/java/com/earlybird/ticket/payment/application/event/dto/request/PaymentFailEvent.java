package com.earlybird.ticket.payment.application.event.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentMethod;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PaymentFailEvent(
    UUID paymentId,
    UUID reservationId,
    PassportDto passportDto,
    PaymentMethod paymentMethod,
    PaymentStatus paymentStatus
) implements EventPayload {

}
