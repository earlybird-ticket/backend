package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.UUID;

public record VenueUpdatePayload(
        PassportDto passportDto,
        UUID venueId,
        String venueName,
        String location,
        String area,
        int totalNumberOfSeat
) implements EventPayload {

}
