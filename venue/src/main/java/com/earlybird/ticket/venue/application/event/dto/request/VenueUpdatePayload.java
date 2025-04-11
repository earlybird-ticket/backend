package com.earlybird.ticket.venue.application.event.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import lombok.Builder;

import java.util.UUID;

@Builder
public record VenueUpdatePayload(
        PassportDto passportDto,
        UUID venueId,
        String venueName,
        String location,
        String area,
        int totalNumberOfSeat
) implements EventPayload {
}
