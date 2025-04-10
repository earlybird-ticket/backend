package com.earlybird.ticket.venue.application.dto.response;

import com.earlybird.ticket.venue.domain.dto.SeatListResult;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record SeatListQuery(
        UUID concertId,
        UUID concertSequenceId,
        String section,
        String grade,
        Integer floor,
        List<SeatQuery> seatList
) {
    @Builder
    public record SeatQuery(
            UUID seatInstanceId,
            Integer row,
            Integer col,
            String status,
            BigDecimal price
    ) {

    }

    public static SeatListQuery from(SeatListResult seatListResult) {
        return SeatListQuery.builder()
                .concertId(seatListResult.concertId())
                .concertSequenceId(seatListResult.concertSequenceId())
                .section(seatListResult.section() == null ? null : seatListResult.section().getValue())
                .grade(seatListResult.grade() == null ? null : seatListResult.grade().getValue())
                .floor(seatListResult.floor())
                .seatList(seatListResult.seatList()
                        .stream()
                        .map(seatResult -> SeatQuery.builder()
                                .seatInstanceId(seatResult.seatInstanceId())
                                .row(seatResult.row())
                                .col(seatResult.col())
                                .status(seatResult.status() == null ? null : seatResult.status().getValue())
                                .price(seatResult.price())
                                .build()
                        )
                        .toList()
                )
                .build();
    }

}
