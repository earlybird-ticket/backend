package com.earlybird.ticket.venue.presentation.dto.request;

import com.earlybird.ticket.venue.application.dto.request.ProcessSeatCheckCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ProcessSeatCheckRequest(
        @NotNull(message = "[concert_sequence_id : NotNull]") UUID concertSequenceId,
        @NotEmpty(message = "[seat_id_list : NotEmpty]") List<UUID> seatInstanceIdList
) {
    public ProcessSeatCheckCommand toCommand() {
        return ProcessSeatCheckCommand.builder()
                .concertSequenceId(concertSequenceId)
                .seatInstanceIdList(seatInstanceIdList)
                .build();
    }
}
