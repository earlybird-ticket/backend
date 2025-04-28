package com.earlybird.ticket.reservation.domain.dto.request;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.reservation.domain.entity.constant.ConcertStatus;
import com.earlybird.ticket.reservation.domain.entity.constant.SeatGrade;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateReservationPayload implements EventPayload {
    @NotNull(message = "reservationId is Necessary")
    private UUID reservationId;
    @NotNull(message = "userName is Necessary")
    private String userName;

    //공연 정보
    @NotNull(message = "concertId is Necessary")
    private UUID concertId;
    @NotNull(message = "concertName is Necessary")
    private String concertName;

    //공연 회차 정보(세부정보)
    @NotNull(message = "concertSequenceId is Necessary")
    private UUID concertSequenceId;
    @NotNull(message = "concertSequenceStartDatetime is Necessary")
    private LocalDateTime concertSequenceStartDatetime;
    @NotNull(message = "concertSequenceEndDatetime is Necessary")
    private LocalDateTime concertSequenceEndDatetime;
    @NotNull(message = "concertSequenceStatus is Necessary")
    private ConcertStatus concertSequenceStatus;

    //공연장 정보
    @NotNull(message = "venueId is Necessary")
    private UUID venueId;
    @NotNull(message = "venueArea is Necessary")
    private String venueArea;
    @NotNull(message = "venueLocation is Necessary")
    private String venueLocation;

    @NotNull
    private List<SeatRequest> seatList;

    // 홀 정보
    @NotNull(message = "hallId is Necessary")
    private UUID hallId;
    @NotNull(message = "hallName is Necessary")
    private String hallName;
    @NotNull(message = "hallFloor is Necessary")
    private Integer hallFloor;

    private PassportDto passportDto;

    @Builder
    public CreateReservationPayload(UUID reservationId,
                                    String userName,
                                    UUID concertId,
                                    String concertName,
                                    UUID concertSequenceId,
                                    LocalDateTime concertSequenceStartDatetime,
                                    LocalDateTime concertSequenceEndDatetime,
                                    ConcertStatus concertSequenceStatus,
                                    UUID venueId,
                                    String venueArea,
                                    String venueLocation,
                                    List<SeatRequest> seatList,
                                    UUID hallId,
                                    String hallName,
                                    Integer hallFloor,
                                    PassportDto passportDto) {
        this.reservationId = reservationId;
        this.userName = userName;
        this.concertId = concertId;
        this.concertName = concertName;
        this.concertSequenceId = concertSequenceId;
        this.concertSequenceStartDatetime = concertSequenceStartDatetime;
        this.concertSequenceEndDatetime = concertSequenceEndDatetime;
        this.concertSequenceStatus = concertSequenceStatus;
        this.venueId = venueId;
        this.venueArea = venueArea;
        this.venueLocation = venueLocation;

        this.seatList = seatList;
        this.hallId = hallId;
        this.hallName = hallName;
        this.hallFloor = hallFloor;
        this.passportDto = passportDto;
    }

    @Getter
    @NoArgsConstructor
    public static class SeatRequest {


        //논리적 좌석 정보
        @NotNull(message = "seatInstanceId is Necessary")
        UUID seatInstanceId;
        @NotNull(message = "seatRow is Necessary")
        Integer seatRow;
        @NotNull(message = "seatCol is Necessary")
        Integer seatCol;
        @NotNull(message = "seatGrade is Necessary")
        SeatGrade seatGrade;
        @NotNull(message = "seatPrice is Necessary")
        Integer seatPrice;

        @Builder
        public SeatRequest(@NotNull(message = "seatInstanceId is Necessary") UUID seatInstanceId,
                           @NotNull(message = "seatRow is Necessary") Integer seatRow,
                           @NotNull(message = "seatCol is Necessary") Integer seatCol,
                           @NotNull(message = "seatGrade is Necessary") SeatGrade seatGrade,
                           @NotNull(message = "seatPrice is Necessary") Integer seatPrice) {
            this.seatInstanceId = seatInstanceId;
            this.seatRow = seatRow;
            this.seatCol = seatCol;
            this.seatGrade = seatGrade;
            this.seatPrice = seatPrice;
        }
    }
}
