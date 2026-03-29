package com.earlybird.ticket.venue.application.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record SeatListQueryV2(
    String section,
    List<Integer> availableIndexes
) {

    public SeatListQueryV2 {
        availableIndexes = availableIndexes == null ? List.of() : List.copyOf(availableIndexes);
    }

}
