package com.earlybird.ticket.venue.application.event.handler;

import com.earlybird.ticket.venue.application.event.dto.request.VenueCreatePayload;
import com.earlybird.ticket.venue.application.event.dto.request.VenueDeletePayload;
import com.earlybird.ticket.venue.application.event.dto.request.VenueUpdatePayload;
import org.springframework.stereotype.Service;

@Service
public interface VenueHandlerService {

    void create(VenueCreatePayload venueCreatePayload);

    void update(VenueUpdatePayload venueUpdatePayload);

    void delete(VenueDeletePayload venueDeletePayload);
}
