package com.earlybird.ticket.venue.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.venue.common.exception.VenueCreateException;
import com.earlybird.ticket.venue.domain.entity.dto.HallCreatResult;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@Getter
@Table(name = "p_venue")
@SQLRestriction(("deleted_at is null"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder.Default
    @OneToMany(mappedBy = "venue", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Hall> halls = new HashSet<>();

    public static Venue create(
            String venueName,
            String location,
            String area,
            int totalNumberOfSeats,
            Long userId,
            List<HallCreatResult> hallList
    ) {
        validateHallList(hallList);

        Venue venue = Venue.builder()
                .name(venueName)
                .location(location)
                .area(area)
                .totalNumberOfSeats(totalNumberOfSeats)
                .build();

        venue.create(userId);

        for(HallCreatResult hallCreateResult : hallList) {
            venue.getHalls().add(Hall.createHall(
                    venue,
                    hallCreateResult.hallName(),
                    hallCreateResult.hallFloor(),
                    userId
            ));
        }

        return venue;
    }

    private static void validateHallList(List<HallCreatResult> hallList) {
        if (hallList == null || hallList.isEmpty()) {
            throw new VenueCreateException();
        }
    }

    public static Hall createHall(
            Venue venue,
            String hallName,
            Integer hallFloor,
            Long userId
    ) {
        return Hall.createHall(venue, hallName, hallFloor, userId);
    }

    public void updateVenue(
            String name,
            String location,
            String area,
            Integer totalNumberOfSeats,
            Long userId
    ) {
        this.name = name;
        this.location = location;
        this.area = area;
        this.totalNumberOfSeats = totalNumberOfSeats;

        this.update(userId);
    }
}
