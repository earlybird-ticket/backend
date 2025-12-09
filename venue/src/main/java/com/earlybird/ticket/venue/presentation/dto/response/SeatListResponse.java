package com.earlybird.ticket.venue.presentation.dto.response;

import com.earlybird.ticket.venue.application.dto.response.SeatListQuery;
import com.earlybird.ticket.venue.application.dto.response.SeatListQueryV2;
import com.fasterxml.jackson.annotation.JsonRawValue;
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
        //List<SeatResponse> seatList
        String seatList
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

    public static SeatListResponse from(SeatListQueryV2 seatListQuery) {
        return SeatListResponse.builder()
                .concertId(seatListQuery.concertId())
                .concertSequenceId(seatListQuery.concertSequenceId())
                .section(seatListQuery.section())
                .grade(seatListQuery.grade())
                .floor(seatListQuery.floor())
                .seatList(seatListQuery.seatJsonList())
                .build();
    }

    @Override
    @JsonRawValue
    public String seatList() {
        return seatList;
    }

}
