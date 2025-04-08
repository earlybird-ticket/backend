package com.earlybird.ticket.venue.presentation.dto.response;

import com.earlybird.ticket.venue.application.dto.response.GetSeatListQuery;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record GetSeatListResponse(
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

    public static GetSeatListResponse from(GetSeatListQuery getSeatListQuery) {
        return GetSeatListResponse.builder()
                .concertId(getSeatListQuery.concertId())
                .concertSequenceId(getSeatListQuery.concertSequenceId())
                .section(getSeatListQuery.section())
                .grade(getSeatListQuery.grade())
                .floor(getSeatListQuery.floor())
                .seatList(getSeatListQuery.seatList()
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
