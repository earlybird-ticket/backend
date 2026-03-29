package com.earlybird.ticket.venue.presentation.dto.response;

import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import java.util.List;
import lombok.Builder;

@Builder
public record SeatListResponse(
    String section,
    List<Integer> availableIndexes
) {

    public SeatListResponse {
        availableIndexes = availableIndexes == null ? List.of() : List.copyOf(availableIndexes);
    }


    public static SeatListResponse from(SeatListQuery seatListQuery) {
        return SeatListResponse.builder()
            .section(seatListQuery.section())
            .availableIndexes(seatListQuery.availableIndexes())
            .build();
    }


}
