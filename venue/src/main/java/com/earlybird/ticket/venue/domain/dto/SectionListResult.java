package com.earlybird.ticket.venue.domain.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record SectionListResult(
        UUID concertId,
        UUID concertSequenceId,
        List<SectionResult> sectionList
) {
@Builder
public record SectionResult(
        String section,
        int remainingNumberOfSeats,
        int floor,
        String grade,
        BigDecimal price
) {

}
}
