package com.earlybird.ticket.concert.domain;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.concert.domain.constant.Genre;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

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

    private Genre genre;

    private Integer runningTime;

    private Long sellerId;

    private String priceInfo;

    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConcertSequence> concertSequences = new ArrayList<>();

    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatInstanceInfo> seatInstanceInfo = new ArrayList<>();

    @Builder
    public Concert(
            String concertName, String entertainerName, LocalDateTime startDate,
            LocalDateTime endDate, Genre genre, Integer runningTime, Long sellerId,
            String priceInfo,
            List<SeatInstanceInfo> seatInstanceInfo,
            List<ConcertSequence> concertSequences
    ) {
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
    }
}
