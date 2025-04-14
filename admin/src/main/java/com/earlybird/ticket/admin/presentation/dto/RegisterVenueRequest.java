package com.earlybird.ticket.admin.presentation.dto;

import com.earlybird.ticket.admin.application.dto.RegisterVenueCommand;
import com.earlybird.ticket.admin.application.dto.RegisterVenueCommand.HallCreateCommand;
import java.util.List;

public record RegisterVenueRequest(
        String venueName,
        String location,
        String area,
        int totalNumberOfSeat,
        List<Hall> hallList
) {

    public RegisterVenueCommand toRegisterVenueCommand() {
        return RegisterVenueCommand.builder()
                .venueName(venueName)
                .location(location)
                .area(area)
                .totalNumberOfSeat(totalNumberOfSeat)
                .hallList(toHallCreateCommand())
                .build();
    }

    public List<HallCreateCommand> toHallCreateCommand() {
        return hallList.stream()
                .map(hall -> HallCreateCommand.builder()
                        .hallName(hall.hallName())
                        .hallFloor(hall.hallFloor())
                        .build())
                .toList();
    }

    public record Hall(
            String hallName,
            int hallFloor
    ) {

    }
}
