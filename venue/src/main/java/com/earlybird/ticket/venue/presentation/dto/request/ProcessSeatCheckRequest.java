package com.earlybird.ticket.venue.presentation.dto.request;

import com.earlybird.ticket.venue.application.dto.request.ProcessSeatCheckCommand;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ProcessSeatCheckRequest(
        @NotEmpty(message = "[seat_id_list : NotEmpty]") List<UUID> seatIdList
) {
    public ProcessSeatCheckCommand toCommand() {
        return ProcessSeatCheckCommand.builder()
                .seatIdList(seatIdList)
                .build();
    }
}
