package com.earlybird.ticket.admin.application.event.dto;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import java.util.List;
import lombok.Builder;

@Builder
public record VenueCreatePayload(
        PassportDto passportDto,
        String venueName,
        String location,
        String area,
        int totalNumberOfSeat,
        List<HallCreatePayload> hallList
) implements EventPayload {

    public record HallCreatePayload(
            String hallName,
            int hallFloor
    ) {

    }
}
