package com.earlybird.ticket.venue.presentation.dto.response;

import com.earlybird.ticket.venue.application.dto.response.SeatListQueryV2;
import java.util.List;
import lombok.Builder;

@Builder
public record SeatListResponseV2(
    String section,
    List<Integer> availableIndexes
) {

    public SeatListResponseV2 {
        availableIndexes = availableIndexes == null ? List.of() : List.copyOf(availableIndexes);
    }


    public static SeatListResponseV2 from(SeatListQueryV2 seatListQuery) {
        return SeatListResponseV2.builder()
            .section(seatListQuery.section())
            .availableIndexes(seatListQuery.availableIndexes())
            .build();
    }


}
