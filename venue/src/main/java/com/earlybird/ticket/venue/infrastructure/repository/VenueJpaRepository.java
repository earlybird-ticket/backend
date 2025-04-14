package com.earlybird.ticket.venue.infrastructure.repository;

import com.earlybird.ticket.venue.domain.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VenueJpaRepository extends JpaRepository<Venue, UUID> {
    Venue findVenueWithHallById(UUID venueId);
}
