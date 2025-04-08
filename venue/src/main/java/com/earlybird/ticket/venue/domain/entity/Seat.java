package com.earlybird.ticket.venue.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_seat")
@SQLRestriction(("deleted_at is null"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "hall_id", nullable = false)
    private UUID hallId;

    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @Enumerated(EnumType.STRING)
    @Column(name = "section", nullable = false)
    private Section section;

    @Column(name = "row", nullable = false)
    private Integer row;

    @Column(name = "col", nullable = false)
    private Integer col;

    @Column(name = "floor", columnDefinition = "TINYINT")
    private Integer floor;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<SeatInstant> seatInstants;
}
