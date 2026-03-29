package com.earlybird.ticket.venue.application.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record SeatListQuery(
    String section,
    List<Integer> availableIndexes
) {

    public SeatListQuery {
        availableIndexes = availableIndexes == null ? List.of() : List.copyOf(availableIndexes);
    }

}
