package com.earlybird.ticket.venue.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.venue.domain.entity.constant.Grade;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.entity.constant.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Builder
@Entity
@Getter
@Table(name = "p_seat")
@SQLRestriction(("deleted_at is null"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @Column(name = "hall_id", nullable = false)
    private UUID hallId;

    @Enumerated(EnumType.STRING)
    @Column(name = "section", nullable = false)
    private Section section;

    @Column(name = "row", nullable = false)
    private Integer row;

    @Column(name = "col", nullable = false)
    private Integer col;

    @Column(name = "floor", columnDefinition = "SMALLINT")
    private Integer floor;

    @Builder.Default
    @OneToMany(mappedBy = "seat", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<SeatInstance> seatInstances = new HashSet<>();

    public static List<Seat> create(
            Integer rowCnt,
            Integer colCnt,
            String section,
            Integer floor,
            UUID venueId,
            UUID hallId,
            Long userId
    ) {
        List<Seat> seatList = new ArrayList<>();

        for (int i = 1; i <= rowCnt; i++) {
            for (int j = 1; j <= colCnt; j++) {
                Seat seat = Seat.builder()
                        .venueId(venueId)
                        .hallId(hallId)
                        .section(Section.getByValue(section))
                        .row(i)
                        .col(j)
                        .floor(floor)
                        .build();

                seat.create(userId);
                seatList.add(seat);
            }
        }

        return seatList;
    }

    public boolean checkSeatStatus(List<UUID> seatInstanceIdList, Status status) {
        this.seatInstances.stream()
                .filter(seatInstance -> seatInstanceIdList.contains(seatInstance.getId()))
                .forEach(seatInstance -> seatInstance.checkSeatInstanceStatus(status));

        return true;
    }

    public void createSeatInstance(
            String grade,
            BigDecimal price,
            UUID concertSequenceId,
            UUID venueId,
            UUID concertId,
            UUID hallId,
            Long userId
    ) {
        SeatInstance seatInstance = SeatInstance.createSeatInstance(
                this,
                venueId,
                hallId,
                concertId,
                concertSequenceId,
                Grade.getByValue(grade),
                price,
                userId
        );

        this.seatInstances.add(seatInstance);
    }

    public void updateSeatInstance(
            UUID seatInstanceId,
            UUID hallId,
            UUID concertId,
            UUID concertSequenceId,
            String grade,
            String status,
            BigDecimal price,
            Long userId
    ) {
        this.seatInstances.stream()
                .filter(seatInstance -> seatInstanceId.equals(seatInstance.getId()))
                .findFirst()
                .ifPresent(seatInstance -> seatInstance.updateSeatInstance(
                        hallId, concertId, concertSequenceId, grade, status, price, userId
                ));
    }

    public void deleteSeatInstance(UUID seatInstanceId, Long userId) {
        this.seatInstances.stream()
                .filter(seatInstance -> seatInstanceId.equals(seatInstance.getId()))
                .findFirst()
                .ifPresent(seatInstance -> seatInstance.deleteSeatInstance(
                        userId
                ));
    }

    public void preemptSeat(List<UUID> seatInstanceIdList,Long userId) {
        checkSeatStatus(seatInstanceIdList, Status.FREE);
        this.seatInstances.stream()
                .filter(seatInstance -> seatInstanceIdList.contains(seatInstance.getId()))
                .findFirst()
                .ifPresent(seatInstance -> seatInstance.preemptSeatInstance(userId));

    }

    public void confirmSeat(List<UUID> seatInstanceIdList, Long userId) {
        checkSeatStatus(seatInstanceIdList, Status.PREEMPTED);
        this.seatInstances.stream()
                .filter(seatInstance -> seatInstanceIdList.contains(seatInstance.getId()))
                .findFirst()
                .ifPresent(seatInstance -> seatInstance.confirmSeatInstance(userId));
    }

    public void returnSeat(List<UUID> seatInstanceIdList, Long userId) {

        if(checkSeatStatus(seatInstanceIdList, Status.FREE)) {
            log.error("The seat in the free state has been returned.");
        }

        this.seatInstances.stream()
                .filter(seatInstance -> seatInstanceIdList.contains(seatInstance.getId()))
                .findFirst()
                .ifPresent(seatInstance -> seatInstance.returnSeatInstance(userId));
    }

    public void deleteSeatAndSeatInstance(Long userId) {
        this.delete(userId);
        this.seatInstances.stream()
                .forEach(seatInstance -> seatInstance.deleteSeatInstance(userId));
    }
}
