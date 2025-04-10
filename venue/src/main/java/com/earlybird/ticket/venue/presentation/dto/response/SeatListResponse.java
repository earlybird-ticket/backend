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
        Integer floor,
        List<SeatResponse> seatList
) {
    @Builder
    private record SeatResponse(
        UUID seatInstanceId,
        Integer row,
        Integer col,
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
                                .seatInstanceId(seat.seatInstanceId())
                                .row(seat.row())
                                .col(seat.col())
                                .seatStatus(seat.status())
                                .price(seat.price())
                                .build())
                        .toList()
                )
                .build();
    }


}
