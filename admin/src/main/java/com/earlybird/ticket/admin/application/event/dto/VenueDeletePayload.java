package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.UUID;

public record VenueDeletePayload(
        PassportDto passportDto,
        UUID venueId
) implements EventPayload {

}
