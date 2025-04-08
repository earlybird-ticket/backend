package com.earlybird.ticket.venue.application.dto.response;

import java.util.List;
import java.util.UUID;

public record ProcessSeatCheckQuery(
        List<UUID> seatIdList,
        boolean status
) {
}
