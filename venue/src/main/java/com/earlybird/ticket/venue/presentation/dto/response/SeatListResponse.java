package com.earlybird.ticket.venue.presentation.dto.response;

import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record SeatListResponse(
        UUID concertId,
        UUID concertSequenceId,
        String section,
        String grade,
        int floor,
        List<SeatResponse> seatList
) {
    @Builder
    private record SeatResponse(
        UUID seatId,
        int row,
        int col,
        String seatStatus,
        BigDecimal price
    ) {

    }

    public static SeatListResponse from(SeatListQuery seatListQuery) {
        return SeatListResponse.builder()
                .concertId(seatListQuery.concertId())
                .concertSequenceId(seatListQuery.concertSequenceId())
                .section(seatListQuery.section())
                .grade(seatListQuery.grade())
                .floor(seatListQuery.floor())
                .seatList(seatListQuery.seatList()
                        .stream()
                        .map(seat -> SeatResponse.builder()
                                .seatId(seat.seatId())
                                .row(seat.row())
                                .col(seat.col())
                                .seatStatus(seat.seatStatus())
                                .price(seat.price())
                                .build())
                        .toList()
                )
                .build();
    }


}
