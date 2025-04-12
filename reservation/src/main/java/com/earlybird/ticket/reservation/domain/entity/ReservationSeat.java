package com.earlybird.ticket.reservation.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.reservation.domain.entity.constant.SeatGrade;
import com.earlybird.ticket.reservation.domain.entity.constant.SeatStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_reservation_seat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
public class ReservationSeat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reservation_seat_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Column(name = "seat_instance_id", nullable = false)
    private UUID seatInstanceId;

    @Column(name = "concert_id", nullable = false)
    private UUID concertId;

    @Column(name = "row", nullable = false)
    private Integer row;

    @Column(name = "col", nullable = false)
    private Integer col;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    private SeatGrade grade;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SeatStatus status;

    @Builder
    public ReservationSeat(UUID id,
                           Reservation reservation,
                           UUID seatInstanceId,
                           UUID concertId,
                           Integer row,
                           Integer col,
                           SeatGrade grade,
                           BigDecimal price,
                           SeatStatus status) {
        this.id = id;
        this.reservation = reservation;
        this.seatInstanceId = seatInstanceId;
        this.concertId = concertId;
        this.row = row;
        this.col = col;
        this.grade = grade;
        this.price = price;
        this.status = status;
    }

    public static ReservationSeat createReservationSeat(Reservation reservation,
                                                        UUID seatInstanceId,
                                                        UUID concertId,
                                                        Integer row,
                                                        Integer col,
                                                        SeatGrade grade,
                                                        BigDecimal price) {
        return ReservationSeat.builder()
                              .reservation(reservation)
                              .seatInstanceId(seatInstanceId)
                              .concertId(concertId)
                              .row(row)
                              .col(col)
                              .grade(grade)
                              .price(price)
                              .status(SeatStatus.RESERVED)
                              .build();
    }

    public void updateStatusReserveSuccess() {
        this.status = SeatStatus.RESERVED;
    }

    public void updateStatusReserveFail() {
        this.status = SeatStatus.FREE;
    }
}