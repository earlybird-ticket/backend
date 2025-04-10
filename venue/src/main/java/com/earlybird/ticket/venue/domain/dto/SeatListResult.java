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
        Integer floor,
        List<SeatResult> seatList
) {
        public static final SeatListResult EMPTY = new SeatListResult(null, null, null, null, null, null);

        @Builder
        public record SeatResult(
                UUID seatInstanceId,
                Integer row,
                Integer col,
                Status status,
                BigDecimal price
        ) {

        }

        public static SeatListResult copyWithSeatList(SeatListResult original, List<SeatResult> originalSeatList) {
                SeatListResult safeSeatListResult = original == null ? EMPTY : original;

                return SeatListResult.builder().
                        concertId(safeSeatListResult.concertId).
                        concertSequenceId(safeSeatListResult.concertSequenceId()).
                        section(safeSeatListResult.section()).
                        grade(safeSeatListResult.grade()).
                        floor(safeSeatListResult.floor()).
                        seatList(originalSeatList).
                        build();
        }


}


