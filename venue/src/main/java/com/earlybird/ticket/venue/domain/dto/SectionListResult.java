package com.earlybird.ticket.venue.domain.dto;

import com.earlybird.ticket.venue.domain.entity.constant.Grade;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
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
        Section section,
        long remainingNumberOfSeats,
        int floor,
        Grade grade,
        BigDecimal price
) {

}
}
