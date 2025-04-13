package com.earlybird.ticket.reservation.presentation.dto.response;

import com.earlybird.ticket.reservation.application.dto.response.FindReservationQuery;
import com.earlybird.ticket.reservation.domain.entity.constant.*;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FindReservationResponse(
        //예매 번호
        @NotNull(message = "reservationId is Necessary") UUID reservationId,
        @NotNull(message = "reservationStatus is Necessary") ReservationStatus reservationStatus,
        @NotNull(message = "resrvationCreatedAt is Necessary") LocalDateTime resrvationCreatedAt,
        //공연 정보
        @NotNull(message = "concertId is Necessary") UUID concertId,
        @NotNull(message = "concertName is Necessary") String concertName,

        //공연 회차 정보(세부정보)
        @NotNull(message = "concertSequenceId is Necessary") UUID concertSequenceId,
        @NotNull(message = "concertSequenceStartDatetime is Necessary") LocalDateTime concertSequenceStartDatetime,
        @NotNull(message = "concertSequenceEndDatetime is Necessary") LocalDateTime concertSequenceEndDatetime,

        //공연장 정보
        @NotNull(message = "venueId is Necessary") UUID venueId,
        @NotNull(message = "venueArea is Necessary") String venueArea,
        @NotNull(message = "venueLocation is Necessary") String venueLocation,

        //쿠폰 정보 -> Id가 없다면 사용하지 않은것으로 간주하기
        @Nullable UUID couponId,
        @Nullable CouponType couponType,
        @Nullable String couponName,
        @Nullable CouponStatus couponStatus,

        @NotNull List<SeatResponse> seatResponseList,

        // 홀 정보
        @NotNull(message = "hallId is Necessary") UUID hallId,
        @NotNull(message = "hallName is Necessary") String hallName,
        @NotNull(message = "hallFloor is Necessary") Integer hallFloor,

        // 예약 내용
        @NotNull(message = "content is Necessary") String content,

        @NotNull(message = "TotalPrice is Necessary") BigDecimal totalPrice,
        @NotNull(message = "PaymentMethod is Necessary") PaymentMethod paymentMethod) {


    @Builder
    public FindReservationResponse(@NotNull(message = "reservationId is Necessary") UUID reservationId,
                                   @NotNull(message = "reservationStatus is Necessary") ReservationStatus reservationStatus,
                                   @NotNull(message = "resrvationCreatedAt is Necessary") LocalDateTime resrvationCreatedAt,
                                   @NotNull(message = "concertId is Necessary") UUID concertId,
                                   @NotNull(message = "concertName is Necessary") String concertName,
                                   @NotNull(message = "concertSequenceId is Necessary") UUID concertSequenceId,
                                   @NotNull(message = "concertSequenceStartDatetime is Necessary") LocalDateTime concertSequenceStartDatetime,
                                   @NotNull(message = "concertSequenceEndDatetime is Necessary") LocalDateTime concertSequenceEndDatetime,
                                   @NotNull(message = "venueId is Necessary") UUID venueId,
                                   @NotNull(message = "venueArea is Necessary") String venueArea,
                                   @NotNull(message = "venueLocation is Necessary") String venueLocation,
                                   @Nullable UUID couponId,
                                   @Nullable CouponType couponType,
                                   @Nullable String couponName,
                                   @Nullable CouponStatus couponStatus,
                                   @NotNull List<SeatResponse> seatResponseList,
                                   @NotNull(message = "hallId is Necessary") UUID hallId,
                                   @NotNull(message = "hallName is Necessary") String hallName,
                                   @NotNull(message = "hallFloor is Necessary") Integer hallFloor,
                                   @NotNull(message = "content is Necessary") String content,
                                   @NotNull(message = "TotalPrice is Necessary") BigDecimal totalPrice,
                                   @NotNull(message = "PaymentMethod is Necessary") PaymentMethod paymentMethod) {
        this.reservationId = reservationId;
        this.reservationStatus = reservationStatus;
        this.resrvationCreatedAt = resrvationCreatedAt;
        this.concertId = concertId;
        this.concertName = concertName;
        this.concertSequenceId = concertSequenceId;
        this.concertSequenceStartDatetime = concertSequenceStartDatetime;
        this.concertSequenceEndDatetime = concertSequenceEndDatetime;
        this.venueId = venueId;
        this.venueArea = venueArea;
        this.venueLocation = venueLocation;
        this.couponId = couponId;
        this.couponType = couponType;
        this.couponName = couponName;
        this.couponStatus = couponStatus;
        this.seatResponseList = seatResponseList;
        this.hallId = hallId;
        this.hallName = hallName;
        this.hallFloor = hallFloor;
        this.content = content;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
    }

    public static FindReservationResponse createFindReservationResponse(FindReservationQuery reservation) {
        return FindReservationResponse.builder()
                                      .reservationId(reservation.reservationId())
                                      .reservationStatus(reservation.reservationStatus())
                                      .resrvationCreatedAt(reservation.resrvationCreatedAt())
                                      .concertId(reservation.concertId())
                                      .concertName(reservation.concertName())
                                      .concertSequenceId(reservation.concertSequenceId())
                                      .concertSequenceStartDatetime(reservation.concertSequenceStartDatetime())
                                      .concertSequenceEndDatetime(reservation.concertSequenceEndDatetime())
                                      .venueId(reservation.venueId())
                                      .venueArea(reservation.venueArea())
                                      .venueLocation(reservation.venueLocation())
                                      .couponId(reservation.couponId())
                                      .couponType(reservation.couponType())
                                      .couponName(reservation.couponName())
                                      .couponStatus(reservation.couponStatus())
                                      .seatResponseList(reservation.seatList()
                                                                   .stream()
                                                                   .map(seatQuery -> {
                                                                       return SeatResponse.builder()
                                                                                          .seatInstanceId(seatQuery.seatInstanceId())
                                                                                          .seatRow(seatQuery.seatRow())
                                                                                          .seatCol(seatQuery.seatCol())
                                                                                          .seatGrade(seatQuery.seatGrade())
                                                                                          .seatPrice(seatQuery.seatPrice())
                                                                                          .build();
                                                                   })
                                                                   .toList())
                                      .hallId(reservation.hallId())
                                      .hallName(reservation.hallName())
                                      .hallFloor(reservation.hallFloor())
                                      .content(reservation.content())
                                      .totalPrice(reservation.totalPrice())
                                      .paymentMethod(reservation.paymentMethod())
                                      .build();
    }

    public record SeatResponse(

            //논리적 좌석 정보
            @NotNull(message = "seatInstanceId is Necessary") UUID seatInstanceId,
            @NotNull(message = "seatRow is Necessary") Integer seatRow,
            @NotNull(message = "seatCol is Necessary") Integer seatCol,
            @NotNull(message = "seatGrade is Necessary") SeatGrade seatGrade,
            @NotNull(message = "seatPrice is Necessary") BigDecimal seatPrice) {
        @Builder
        public SeatResponse(@NotNull(message = "seatInstanceId is Necessary") UUID seatInstanceId,
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
