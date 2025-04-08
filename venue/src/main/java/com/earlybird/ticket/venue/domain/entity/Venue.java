package com.earlybird.ticket.venue.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_venue")
@SQLRestriction(("deleted_at is null"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "area", length = 50, nullable = false)
    private String area;

    @Column(name = "total_number_of_seats")
    private Integer totalNumberOfSeats;

    @OneToMany(mappedBy = "venue", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Hall> halls;
}
