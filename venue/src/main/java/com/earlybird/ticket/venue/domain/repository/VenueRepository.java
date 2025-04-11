package com.earlybird.ticket.venue.domain.repository;

import com.earlybird.ticket.venue.domain.entity.Venue;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VenueRepository {
    Venue save(Venue venue);

    Venue findById(UUID venueId);
}
