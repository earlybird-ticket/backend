package com.earlybird.ticket.admin.application.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record RegisterVenueCommand(
        String venueName,
        String location,
        String area,
        int totalNumberOfSeat,
        List<HallCreateCommand> hallList
) {

    @Builder
    public record HallCreateCommand(
            String hallName,
            int hallFloor
    ) {

    }
}
