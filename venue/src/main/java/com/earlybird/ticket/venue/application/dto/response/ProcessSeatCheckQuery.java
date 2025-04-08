package com.earlybird.ticket.venue.application.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ProcessSeatCheckQuery(
        List<UUID> seatIdList,
        boolean status
) {
}
