package com.earlybird.ticket.concert.domain;

import com.earlybird.ticket.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@Table(name = "p_seat_instance_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(("deleted_at is null"))
public class SeatInstanceInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID seatInstanceInfoId;
    private String section;
    private String grade;
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @Builder
    public SeatInstanceInfo(String section, String grade, Integer price, Concert concert) {
        this.section = section;
        this.grade = grade;
        this.price = price;
        this.concert = concert;
    }
}
