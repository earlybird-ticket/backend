package com.earlybird.ticket.concert.domain;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.concert.domain.constant.ConcertStatus;
import com.earlybird.ticket.concert.domain.constant.Genre;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_concert")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(("deleted_at is null"))
public class Concert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID concertId;

    private String concertName;

    private String entertainerName;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    private Integer runningTime;

    private Long sellerId;

    private String priceInfo;

    private UUID hallId;

    private UUID venueId;

    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConcertSequence> concertSequences = new ArrayList<>();

    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatInstanceInfo> seatInstanceInfo = new ArrayList<>();

    @Builder
    public Concert(String concertName,
                   String entertainerName,
                   LocalDateTime startDate,
                   LocalDateTime endDate,
                   Genre genre,
                   Integer runningTime,
                   Long sellerId,
                   String priceInfo,
                   List<SeatInstanceInfo> seatInstanceInfo,
                   List<ConcertSequence> concertSequences,
                   UUID hallId,
                   UUID venueId) {
        this.concertName = concertName;
        this.entertainerName = entertainerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.genre = genre;
        this.runningTime = runningTime;
        this.sellerId = sellerId;
        this.priceInfo = priceInfo;
        this.seatInstanceInfo = seatInstanceInfo;
        this.concertSequences = concertSequences;
        this.hallId = hallId;
        this.venueId = venueId;
    }

    public void deleteConcertSequence(Long userId,
                                      UUID concertSequenceId) {
        for (ConcertSequence concertSequence : this.concertSequences) {
            if (concertSequence.getDeletedAt() == null && concertSequence.getConcertSequenceId()
                                                                         .equals(concertSequenceId)) {
                concertSequence.delete(userId);
            }
        }
    }

    public void updateConcert(String concertName,
                              String entertainerName,
                              LocalDateTime startDate,
                              LocalDateTime endDate,
                              Genre genre,
                              Integer runningTime,
                              String priceInfo) {
        this.concertName = concertName;
        this.entertainerName = entertainerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.genre = genre;
        this.runningTime = runningTime;
        this.priceInfo = priceInfo;
    }

    public void deleteConcert(Long userId) {
        super.delete(userId);
    }

    @Builder(builderMethodName = "updateConcertSequenceBuilder")
    public void updateConcertSequence(UUID concertSequenceId,
                                      LocalDateTime sequenceStartDate,
                                      LocalDateTime sequenceEndDate,
                                      LocalDateTime ticketSaleStartDate,
                                      LocalDateTime ticketSaleEndDate,
                                      ConcertStatus status) {

        concertSequences.stream()
                        .map(concertSequence -> {
                            if (concertSequence.getConcertSequenceId()
                                               .equals(concertSequenceId)) {
                                concertSequence.update(sequenceStartDate,
                                                       sequenceEndDate,
                                                       ticketSaleStartDate,
                                                       ticketSaleEndDate,
                                                       status);
                            }
                            return concertSequence;
                        })
                        .forEach(concertSequences::add);
    }

    public void addConcertSequences(List<ConcertSequence> sequences) {
        if (this.concertSequences == null) {
            this.concertSequences = new ArrayList<>();
        }
        this.concertSequences.addAll(sequences);
    }

    public void addSeatInstanceInfo(List<SeatInstanceInfo> seats) {
        if (this.seatInstanceInfo == null) {
            this.seatInstanceInfo = new ArrayList<>();
        }
        this.seatInstanceInfo.addAll(seats);
    }
}
