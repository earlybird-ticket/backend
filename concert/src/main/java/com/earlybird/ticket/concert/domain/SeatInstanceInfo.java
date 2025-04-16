package com.earlybird.ticket.concert.domain;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.concert.domain.constant.Grade;
import com.earlybird.ticket.concert.domain.constant.Section;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_seat_instance_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(("deleted_at is null"))
public class SeatInstanceInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID seatInstanceInfoId;
    private Section section;
    @Enumerated(EnumType.STRING)
    private Grade grade;
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @Builder
    public SeatInstanceInfo(Section section,
                            Grade grade,
                            Integer price,
                            Concert concert) {
        this.section = section;
        this.grade = grade;
        this.price = price;
        this.concert = concert;
    }
}
