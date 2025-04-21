package com.earlybird.ticket.payment.application.event.dto.response;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.UUID;

public record ReservationCancelPayload(
    PassportDto passportDto,
    UUID reservationId,
    UUID paymentId
) implements EventPayload {

}
