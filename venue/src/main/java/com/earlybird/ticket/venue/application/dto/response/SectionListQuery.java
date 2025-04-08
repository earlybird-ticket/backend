package com.earlybird.ticket.venue.application.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record SectionListQuery(
        UUID concertId,
        UUID concertSequenceId,
        List<SectionQuery> sectionList
) {
    @Builder
    public record SectionQuery(
            String section,
            int remainingNumberOfSeats,
            int floor,
            String grade,
            BigDecimal price
    ) {

    }
}

