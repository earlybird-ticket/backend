package com.earlybird.ticket.venue.application.dto.request;

import com.earlybird.ticket.venue.domain.entity.constant.ConcertStatus;
import com.earlybird.ticket.venue.domain.entity.constant.Grade;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record SeatPreemptCommand(
        @NotNull(message = "userName is Necessary") String userName,

        //공연 정보
        @NotNull(message = "concertId is Necessary") UUID concertId,
        @NotNull(message = "concertName is Necessary") String concertName,

        //공연 회차 정보(세부정보)
        @NotNull(message = "concertSequenceId is Necessary") UUID concertSequenceId,
        @NotNull(message = "concertSequenceStartDatetime is Necessary") LocalDateTime concertSequenceStartDatetime,
        @NotNull(message = "concertSequenceEndDatetime is Necessary") LocalDateTime concertSequenceEndDatetime,
        @NotNull(message = "concertSequenceStatus is Necessary") ConcertStatus concertSequenceStatus,

        //공연장 정보
        @NotNull(message = "venueId is Necessary") UUID venueId,
        @NotNull(message = "venueArea is Necessary") String venueArea,
        @NotNull(message = "venueLocation is Necessary") String venueLocation,

        @NotNull List<SeatRequest> seatList,

        // 홀 정보
        @NotNull(message = "hallId is Necessary") UUID hallId,
        @NotNull(message = "hallName is Necessary") String hallName,
        @NotNull(message = "hallFloor is Necessary") Integer hallFloor
) {

    @Builder
    public record SeatRequest(
            //논리적 좌석 정보
            @NotNull(message = "seatInstanceId is Necessary") UUID seatInstanceId,
            @NotNull(message = "seatRow is Necessary") Integer seatRow,
            @NotNull(message = "seatCol is Necessary") Integer seatCol,
            @NotNull(message = "seatGrade is Necessary") Grade seatGrade,
            @NotNull(message = "seatPrice is Necessary") BigDecimal seatPrice) {
    }
}
