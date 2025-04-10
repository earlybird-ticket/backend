package com.earlybird.ticket.admin.application.dto;

import java.util.List;

public record RegisterVenueCommand(
        String venueName,
        String location,
        String area,
        int totalNumberOfSeat,
        List<HallCreateCommand> hallList
) {

    public record HallCreateCommand(
            String hallName,
            int hallFloor
    ) {

    }
}
