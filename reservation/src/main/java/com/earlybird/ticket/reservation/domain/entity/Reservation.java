package com.earlybird.ticket.reservation.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.reservation.application.dto.response.CouponReservePayload;
import com.earlybird.ticket.reservation.application.dto.response.PaymentSuccessPayload;
import com.earlybird.ticket.reservation.domain.entity.constant.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_reservation")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reservation_id")
    private UUID id;

    @Column(name = "reservation_userId", nullable = false)
    private Long userId;

    @Column(name = "reservation_username", nullable = false, length = 50)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false, length = 20)
    private ReservationStatus reservationStatus;

    @Column(name = "concert_id", nullable = false)
    private UUID concertId;

    @Column(name = "concert_name", nullable = false, length = 50)
    private String concertName;

    @Column(name = "concert_sequence_id", nullable = false)
    private UUID concertSequenceId;

    @Column(name = "concert_sequence_start_datetime", nullable = false)
    private LocalDateTime concertSequenceStartDatetime;

    @Column(name = "concert_sequence_end_datetime", nullable = false)
    private LocalDateTime concertSequenceEndDatetime;

    @Column(name = "concert_sequence_status", nullable = false, length = 25)
    @Enumerated(EnumType.STRING)
    private ConcertStatus concertSequenceStatus;

    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @Column(name = "venue_area", nullable = false, length = 50)
    private String venueArea;

    @Column(name = "venue_location", nullable = false, length = 255)
    private String venueLocation;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "payment_total_price")
    private BigDecimal paymentTotalPrice;

    @Column(name = "payment_method", length = 50)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "coupon_id")
    private UUID couponId;

    @Column(name = "coupon_type", length = 20)
    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    @Column(name = "coupon_name", length = 100)
    private String couponName;

    @Column(name = "coupon_status")
    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    @Column(name = "hall_id", nullable = false)
    private UUID hallId;

    @Column(name = "hall_name", nullable = false, length = 50)
    private String hallName;

    @Column(name = "hall_floor")
    private Integer hallFloor;

    @Builder
    public Reservation(UUID id,
                       Long userId,
                       String username,
                       ReservationStatus reservationStatus,
                       UUID concertId,
                       String concertName,
                       UUID concertSequenceId,
                       LocalDateTime concertSequenceStartDatetime,
                       LocalDateTime concertSequenceEndDatetime,
                       ConcertStatus concertSequenceStatus,
                       UUID venueId,
                       String venueArea,
                       String venueLocation,
                       String content,
                       UUID paymentId,
                       BigDecimal paymentTotalPrice,
                       PaymentMethod paymentMethod,
                       UUID couponId,
                       CouponType couponType,
                       String couponName,
                       CouponStatus couponStatus,
                       UUID hallId,
                       String hallName,
                       Integer hallFloor) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.reservationStatus = reservationStatus;
        this.concertId = concertId;
        this.concertName = concertName;
        this.concertSequenceId = concertSequenceId;
        this.concertSequenceStartDatetime = concertSequenceStartDatetime;
        this.concertSequenceEndDatetime = concertSequenceEndDatetime;
        this.concertSequenceStatus = concertSequenceStatus;
        this.venueId = venueId;
        this.venueArea = venueArea;
        this.venueLocation = venueLocation;
        this.content = content;
        this.paymentId = paymentId;
        this.paymentTotalPrice = paymentTotalPrice;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.couponId = couponId;
        this.couponType = couponType;
        this.couponName = couponName;
        this.couponStatus = couponStatus;
        this.hallId = hallId;
        this.hallName = hallName;
        this.hallFloor = hallFloor;
    }

    public static Reservation createReservation(Long userId,
                                                String username,
                                                UUID concertId,
                                                String concertName,
                                                UUID concertSequenceId,
                                                LocalDateTime concertSequenceStartDatetime,
                                                LocalDateTime concertSequenceEndDatetime,
                                                ConcertStatus concertSequenceStatus,
                                                UUID venueId,
                                                String venueArea,
                                                String venueLocation,
                                                UUID hallId,
                                                String hallName,
                                                Integer hallFloor) {
        return Reservation.builder()
                          .userId(userId)
                          .username(username)
                          .reservationStatus(ReservationStatus.PENDING)
                          .concertId(concertId)
                          .concertName(concertName)
                          .concertSequenceId(concertSequenceId)
                          .concertSequenceStartDatetime(concertSequenceStartDatetime)
                          .concertSequenceEndDatetime(concertSequenceEndDatetime)
                          .concertSequenceStatus(concertSequenceStatus)
                          .venueId(venueId)
                          .venueArea(venueArea)
                          .venueLocation(venueLocation)
                          .hallId(hallId)
                          .hallName(hallName)
                          .hallFloor(hallFloor)
                          .build();
    }

    public void updateCouponData(CouponReservePayload payload) {
        validateCouponData(payload);
        this.couponId = payload.couponId();
        this.couponName = payload.couponName();
        this.couponType = payload.couponType();
        this.couponStatus = CouponStatus.RESERVED;
    }

    private void validateCouponData(CouponReservePayload payload) {
        if (payload.couponId() == null || payload.couponName() == null || payload.couponType() == null) {
            throw new NullPointerException("coupon data should not be null");
        }
    }

    public void updatePaymentInfo(PaymentSuccessPayload payload) {
        this.paymentMethod = payload.paymentMethod();
        this.paymentId = payload.paymentId();
        this.paymentTotalPrice = payload.totalPrice();
        this.paymentStatus = payload.paymentStatus();
        update(payload.passportDto()
                      .getUserId());
    }

    public void updateStatusConfirm(Long userId) {
        this.reservationStatus = ReservationStatus.CONFIRMED;
        update(userId);
    }

    public void updateStatusPaying(Long userId) {
        this.reservationStatus = ReservationStatus.PAYING;
        update(userId);
    }

    public void updateStatusCancelled(Long userId) {
        this.reservationStatus = ReservationStatus.CANCELLED;
        this.delete(userId);
    }


}
