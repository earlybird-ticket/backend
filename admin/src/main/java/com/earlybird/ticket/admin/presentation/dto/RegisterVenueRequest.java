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
        return new RegisterVenueCommand(
                venueName,
                location,
                area,
                totalNumberOfSeat,
                toHallCreateCommand()
        );
    }

    public List<HallCreateCommand> toHallCreateCommand() {
        return hallList.stream()
                .map(hall -> new HallCreateCommand(hall.hallName(), hall.hallFloor()))
                .toList();
    }

    public record Hall(
            String hallName,
            int hallFloor
    ) {

    }
}
