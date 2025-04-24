package com.earlybird.ticket.venue.application.dto.request;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ProcessSeatCheckCommand(
        UUID concertSequenceId,
        List<UUID> seatInstanceIdList
) {
}
