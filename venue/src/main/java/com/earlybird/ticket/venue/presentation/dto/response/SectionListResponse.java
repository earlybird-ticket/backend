package com.earlybird.ticket.venue.presentation.dto.response;

import com.earlybird.ticket.venue.application.dto.response.SectionListQuery;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record SectionListResponse(
        UUID concertId,
        UUID concertSequenceId,
        List<SectionResponse> sectionList
) {
    @Builder
    private record SectionResponse(
            String section,
            long remainingNumberOfSeats,
            int floor,
            String grade,
            BigDecimal price
    ) {

    }

    public static SectionListResponse from(SectionListQuery sectionListQuery) {
        return SectionListResponse.builder()
                .concertId(sectionListQuery.concertId())
                .concertSequenceId(sectionListQuery.concertSequenceId())
                .sectionList(sectionListQuery.sectionList()
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
