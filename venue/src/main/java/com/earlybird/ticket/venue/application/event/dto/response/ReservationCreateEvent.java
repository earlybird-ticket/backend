package com.earlybird.ticket.venue.application.event.dto.response;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.venue.domain.entity.constant.ConcertStatus;
import com.earlybird.ticket.venue.domain.entity.constant.Grade;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ReservationCreateEvent(
        PassportDto passportDto,
        String userName,
        //예약 정보
        UUID reservationId,

        //공연 정보
        UUID concertId,
        String concertName,

        //공연 회차 정보(세부정보)
        UUID concertSequenceId,
        LocalDateTime concertSequenceStartDatetime,
        LocalDateTime concertSequenceEndDatetime,
        ConcertStatus concertSequenceStatus,

        //공연장 정보
        UUID venueId,
        String venueArea,
        String venueLocation,

        List<SeatRequest> seatList,

        // 홀 정보
        UUID hallId,
        String hallName,
        Integer hallFloor
) implements EventPayload {

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
