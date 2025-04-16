package com.earlybird.ticket.concert.domain;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.concert.domain.constant.ConcertStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@Table(name = "p_concert_sequence")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(("deleted_at is null"))
public class ConcertSequence extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID concertSequenceId;

    private LocalDateTime sequenceStartDate;

    private LocalDateTime sequenceEndDate;

    private LocalDateTime ticketSaleStartDate;

    private LocalDateTime ticketSaleEndDate;

    @Enumerated(EnumType.STRING)
    private ConcertStatus concertStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @Builder
    public ConcertSequence(
            LocalDateTime sequenceStartDate,
            LocalDateTime sequenceEndDate, LocalDateTime ticketSaleStartDate,
            LocalDateTime ticketSaleEndDate, ConcertStatus concertStatus,
            Concert concert
    ) {
        this.sequenceStartDate = sequenceStartDate;
        this.sequenceEndDate = sequenceEndDate;
        this.ticketSaleStartDate = ticketSaleStartDate;
        this.ticketSaleEndDate = ticketSaleEndDate;
        this.concertStatus = concertStatus;
        this.concert = concert;
    }


    public void update(
            LocalDateTime sequenceStartDate, LocalDateTime sequenceEndDate,
            LocalDateTime ticketSaleStartDate, LocalDateTime ticketSaleEndDate, ConcertStatus status
    ) {

        this.sequenceStartDate = sequenceStartDate;
        this.sequenceEndDate = sequenceEndDate;
        this.ticketSaleStartDate = ticketSaleStartDate;
        this.ticketSaleEndDate = ticketSaleEndDate;
        this.concertStatus = status;
    }
}
