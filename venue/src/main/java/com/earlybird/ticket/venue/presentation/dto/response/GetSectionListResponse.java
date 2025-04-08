package com.earlybird.ticket.venue.presentation.dto.response;

import com.earlybird.ticket.venue.application.dto.response.GetSectionListQuery;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record GetSectionListResponse(
        UUID concertId,
        UUID concertSequenceId,
        List<SectionResponse> sectionList
) {
    @Builder
    private record SectionResponse(
            String section,
            int remainingNumberOfSeats,
            int floor,
            String grade,
            BigDecimal price
    ) {

    }

    public static GetSectionListResponse from(GetSectionListQuery getSectionListQuery) {
        return GetSectionListResponse.builder()
                .concertId(getSectionListQuery.concertId())
                .concertSequenceId(getSectionListQuery.concertSequenceId())
                .sectionList(getSectionListQuery.sectionList()
                        .stream()
                        .map(section -> SectionResponse.builder()
                                .section(section.section())
                                .remainingNumberOfSeats(section.remainingNumberOfSeats())
                                .floor(section.floor())
                                .grade(section.grade())
                                .price(section.price())
                                .build()
                        )
                        .toList()
                )
                .build();
    }
}
