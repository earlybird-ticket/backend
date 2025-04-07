package com.earlybird.ticket.venue.application.event.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;

import java.util.List;

public record VenueCreatePayload(
        PassportDto passportDto,
        String venueName,
        String location,
        String area,
        int totalNumberOfSeat,
        List<HallCreatePayload> hallList
) implements EventPayload {

    public static record HallCreatePayload(
            String hallName,
            int hallFloor
    ) {

    }
}
