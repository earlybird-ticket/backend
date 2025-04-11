package com.earlybird.ticket.venue.domain.repository;

import com.earlybird.ticket.venue.domain.entity.Venue;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueRepository {
    Venue save(Venue venue);
}
