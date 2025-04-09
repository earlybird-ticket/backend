package com.earlybird.ticket.venue.domain.dto;

import com.earlybird.ticket.venue.domain.entity.constant.Grade;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.entity.constant.Status;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record SeatListResult(
        UUID concertId,
        UUID concertSequenceId,
        Section section,
        Grade grade,
        int floor,
        List<SeatResult> seatList
) {
        @Builder
        public record SeatResult(
                UUID seatInstanceId,
                int row,
                int col,
                Status status,
                BigDecimal price
        ) {

        }

        public static SeatListResult copyWithSeatList(SeatListResult original, List<SeatResult> originalSeatList) {
                return SeatListResult.builder().
                        concertId(original.concertId()).
                        concertSequenceId(original.concertSequenceId()).
                        section(original.section()).
                        grade(original.grade()).
                        floor(original.floor()).
                        seatList(originalSeatList).
                        build();
        }


}


