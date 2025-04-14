package com.earlybird.ticket.venue.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Builder
@Entity
@Getter
@Table(name = "p_hall")
@SQLRestriction(("deleted_at is null"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Hall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "floor", columnDefinition = "SMALLINT", nullable = false)
    private Integer floor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    public static Hall createHall(
            Venue venue,
            String name,
            Integer floor,
            Long userId
    ) {
        Hall hall = Hall.builder()
                .venue(venue)
                .name(name)
                .floor(floor)
                .build();

        hall.create(userId);
        return hall;
    }

    public void deleteHall(Long userId) {
        this.delete(userId);
    }
}
