package com.earlybird.ticket.reservation.presentation.dto.request;

import com.earlybird.ticket.reservation.application.dto.CreateReservationCommand;
import com.earlybird.ticket.reservation.domain.entity.constant.ConcertStatus;
import com.earlybird.ticket.reservation.domain.entity.constant.CouponStatus;
import com.earlybird.ticket.reservation.domain.entity.constant.CouponType;
import com.earlybird.ticket.reservation.domain.entity.constant.SeatGrade;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateReservationRequest(
        //userName만 필드로 받는게 나을듯? Id는 Passport에서
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

        //쿠폰 정보 -> Id가 없다면 사용하지 않은것으로 간주하기
        @Nullable UUID couponId,
        @Nullable CouponType couponType,
        @Nullable String couponName,
        @Nullable CouponStatus couponStatus,

        @NotNull List<SeatRequest> seatList,

        // 홀 정보
        @NotNull(message = "hallId is Necessary") UUID hallId,
        @NotNull(message = "hallName is Necessary") String hallName,
        @NotNull(message = "hallFloor is Necessary") Integer hallFloor,

        // 예약 내용
        @NotNull(message = "content is Necessary") String content) {

    @Builder
    public CreateReservationRequest(@NotNull(message = "userName is Necessary") String userName,
                                    @NotNull(message = "concertId is Necessary") UUID concertId,
                                    @NotNull(message = "concertName is Necessary") String concertName,
                                    @NotNull(message = "concertSequenceId is Necessary") UUID concertSequenceId,
                                    @NotNull(message = "concertSequenceStartDatetime is Necessary") LocalDateTime concertSequenceStartDatetime,
                                    @NotNull(message = "concertSequenceEndDatetime is Necessary") LocalDateTime concertSequenceEndDatetime,
                                    @NotNull(message = "concertSequenceStatus is Necessary") ConcertStatus concertSequenceStatus,
                                    @NotNull(message = "venueId is Necessary") UUID venueId,
                                    @NotNull(message = "venueArea is Necessary") String venueArea,
                                    @NotNull(message = "venueLocation is Necessary") String venueLocation,
                                    @Nullable UUID couponId,
                                    @Nullable CouponType couponType,
                                    @Nullable String couponName,
                                    @Nullable CouponStatus couponStatus,
                                    @NotNull List<SeatRequest> seatList,
                                    @NotNull(message = "hallId is Necessary") UUID hallId,
                                    @NotNull(message = "hallName is Necessary") String hallName,
                                    @NotNull(message = "hallFloor is Necessary") Integer hallFloor,
                                    @NotNull(message = "content is Necessary") String content) {
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
        this.couponId = couponId;
        this.couponType = couponType;
        this.couponName = couponName;
        this.couponStatus = couponStatus;
        this.seatList = seatList;
        this.hallId = hallId;
        this.hallName = hallName;
        this.hallFloor = hallFloor;
        this.content = content;
    }

    public static CreateReservationCommand toCreateReservationCommand(CreateReservationRequest command) {
        return CreateReservationCommand.builder()
                                       .userName(command.userName())
                                       .concertId(command.concertId())
                                       .concertName(command.concertName())
                                       .concertSequenceId(command.concertSequenceId())
                                       .concertSequenceStartDatetime(command.concertSequenceStartDatetime())
                                       .concertSequenceEndDatetime(command.concertSequenceEndDatetime())
                                       .concertSequenceStatus(command.concertSequenceStatus())
                                       .venueId(command.venueId())
                                       .venueArea(command.venueArea())
                                       .venueLocation(command.venueLocation())
                                       .couponId(command.couponId())
                                       .couponType(command.couponType())
                                       .couponName(command.couponName())
                                       .couponStatus(command.couponStatus())
                                       .seatList(command.seatList()
                                                        .stream()
                                                        .map(seat -> CreateReservationCommand.SeatRequest.builder()
                                                                                                         .seatInstanceId(seat.seatInstanceId())
                                                                                                         .seatRow(seat.seatRow())
                                                                                                         .seatCol(seat.seatCol())
                                                                                                         .seatGrade(seat.seatGrade())
                                                                                                         .seatPrice(seat.seatPrice())
                                                                                                         .build())
                                                        .toList())
                                       .hallId(command.hallId())
                                       .hallName(command.hallName())
                                       .hallFloor(command.hallFloor())
                                       .content(command.content())
                                       .build();
    }

    public record SeatRequest(

            //논리적 좌석 정보
            @NotNull(message = "seatInstanceId is Necessary") UUID seatInstanceId,
            @NotNull(message = "seatRow is Necessary") Integer seatRow,
            @NotNull(message = "seatCol is Necessary") Integer seatCol,
            @NotNull(message = "seatGrade is Necessary") SeatGrade seatGrade,
            @NotNull(message = "seatPrice is Necessary") BigDecimal seatPrice) {
        @Builder
        public SeatRequest(@NotNull(message = "seatInstanceId is Necessary") UUID seatInstanceId,
                           @NotNull(message = "seatRow is Necessary") Integer seatRow,
                           @NotNull(message = "seatCol is Necessary") Integer seatCol,
                           @NotNull(message = "seatGrade is Necessary") SeatGrade seatGrade,
                           @NotNull(message = "seatPrice is Necessary") BigDecimal seatPrice) {
            this.seatInstanceId = seatInstanceId;
            this.seatRow = seatRow;
            this.seatCol = seatCol;
            this.seatGrade = seatGrade;
            this.seatPrice = seatPrice;
        }
    }
}