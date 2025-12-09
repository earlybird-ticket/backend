package com.earlybird.ticket.venue.application.dto.response;

import com.earlybird.ticket.venue.domain.dto.SeatListResult;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public record SeatListQueryV2(
        UUID concertId,
        UUID concertSequenceId,
        String section,
        String grade,
        Integer floor,
        String seatJsonList
) {

    public static SeatListQueryV2 from(SeatListResult seatListResult) {
        return SeatListQueryV2.builder()
                .concertId(seatListResult.concertId())
                .concertSequenceId(seatListResult.concertSequenceId())
                .section(seatListResult.section() == null ? null : seatListResult.section().getValue())
                .grade(seatListResult.grade() == null ? null : seatListResult.grade().getValue())
                .floor(seatListResult.floor())
                .seatJsonList("[]")
                .build();
    }

    public static SeatListQueryV2 from(
            UUID concertId,
            UUID concertSequenceId,
            String section,
            String grade,
            Integer floor,
            List<String> seatJsonList
    ) {
        return SeatListQueryV2.builder()
                .concertId(concertId)
                .concertSequenceId(concertSequenceId)
                .section(section)
                .grade(grade)
                .floor(floor)
                .seatJsonList(
                        seatJsonList != null && !seatJsonList.isEmpty() ?
                        seatJsonList.stream()
                        .collect(Collectors.joining(",", "[", "]")) :
                                "[]"
                        )
                .build();
    }
}
