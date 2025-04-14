package com.earlybird.ticket.venue.infrastructure.repository;

import com.earlybird.ticket.venue.domain.entity.Venue;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VenueQueryRepository {
    Venue findVenueWithHallById(UUID venueId);
}
