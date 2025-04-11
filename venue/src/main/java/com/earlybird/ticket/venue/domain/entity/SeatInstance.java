package com.earlybird.ticket.venue.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.venue.common.exception.SeatUnavailableException;
import com.earlybird.ticket.venue.domain.entity.constant.Grade;
import com.earlybird.ticket.venue.domain.entity.constant.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Entity
@Getter
@Table(name = "p_seat_instance")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatInstance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @Column(name = "hall_id", nullable = false)
    private UUID hallId;

    @Column(name = "concert_id", nullable = false)
    private UUID concertId;

    @Column(name = "concert_sequence_id", nullable = false)
    private UUID concertSequenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public static SeatInstance createSeatInstance(
            Seat seat,
            UUID venueId,
            UUID hallId,
            UUID concertId,
            UUID concertSequenceId,
            Grade grade,
            BigDecimal price,
            Long userId
    ) {
        SeatInstance seatInstance = SeatInstance.builder()
                .seat(seat)
                .venueId(venueId)
                .hallId(hallId)
                .concertId(concertId)
                .concertSequenceId(concertSequenceId)
                .grade(grade)
                .status(Status.FREE)
                .price(price)
                .build();

        seatInstance.create(userId);

        return seatInstance;
    }

    public void checkFreeSeatInstance() {

        if(!Status.FREE.equals(this.status)) {
            throw new SeatUnavailableException();
        }
    }

    public void updateSeatInstance(
            UUID hallId,
            UUID concertId,
            UUID concertSequenceId,
            String grade,
            String status,
            BigDecimal price,
            Long userId
    ) {
        this.hallId = hallId;
        this.concertId = concertId;
        this.concertSequenceId = concertSequenceId;
        this.grade = Grade.getByValue(grade);
        this.status = Status.getByValue(status);
        this.price = price;

        this.update(userId);
    }

    public void deleteSeatInstance(Long userId) {
        this.delete(userId);
    }
}

