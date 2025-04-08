package com.earlybird.ticket.venue.application.dto.response;

import com.earlybird.ticket.venue.domain.dto.SectionListResult;
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

    public static SectionListQuery from(SectionListResult sectionListResult) {
        return SectionListQuery.builder()
                .concertId(sectionListResult.concertId())
                .concertSequenceId(sectionListResult.concertSequenceId())
                .sectionList(sectionListResult.sectionList().stream()
                        .map(sectionQueryDsl ->
                                SectionQuery.builder()
                                        .section(sectionQueryDsl.section())
                                        .remainingNumberOfSeats(sectionQueryDsl.remainingNumberOfSeats())
                                        .floor(sectionQueryDsl.floor())
                                        .grade(sectionQueryDsl.grade())
                                        .price(sectionQueryDsl.price())
                                        .build()

                        )
                        .toList()
                )
                .build();
    }
}

